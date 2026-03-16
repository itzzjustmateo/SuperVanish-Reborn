/*
 * Copyright © 2015, Leon Mangler and the SuperVanish contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.devflare.svreborn.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.devflare.svreborn.SuperVanishReborn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Manages the JDBC connection pool and all raw database operations for vanish
 * state synchronization. Uses HikariCP for connection pooling.
 *
 * <p>
 * Table structure (auto-created on {@link #connect()}):
 * </p>
 * 
 * <pre>
 * sv_vanished_players (
 *   uuid         VARCHAR(36) PRIMARY KEY,
 *   player_name  VARCHAR(50) NOT NULL,
 *   server_id    VARCHAR(64) NOT NULL,
 *   vanished_at  BIGINT      NOT NULL
 * )
 * </pre>
 */
public class DatabaseManager {

    private static final String TABLE_NAME = "sv_vanished_players";

    private static final String SQL_CREATE_TABLE = """
            CREATE TABLE IF NOT EXISTS %s (
                uuid        VARCHAR(36)  NOT NULL PRIMARY KEY,
                player_name VARCHAR(50)  NOT NULL,
                server_id   VARCHAR(64)  NOT NULL,
                vanished_at BIGINT       NOT NULL
            )""".formatted(TABLE_NAME);

    private static final String SQL_UPSERT_MYSQL = """
            INSERT INTO %s (uuid, player_name, server_id, vanished_at)
            VALUES (?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE player_name = VALUES(player_name),
                                    server_id   = VALUES(server_id),
                                    vanished_at = VALUES(vanished_at)
            """.formatted(TABLE_NAME);

    private static final String SQL_UPSERT_PG = """
            INSERT INTO %s (uuid, player_name, server_id, vanished_at)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (uuid) DO UPDATE SET player_name = EXCLUDED.player_name,
                                            server_id   = EXCLUDED.server_id,
                                            vanished_at = EXCLUDED.vanished_at
            """.formatted(TABLE_NAME);

    private static final String SQL_DELETE = "DELETE FROM " + TABLE_NAME + " WHERE uuid = ?";

    private static final String SQL_SELECT_ALL = "SELECT uuid FROM " + TABLE_NAME;

    private final SuperVanishReborn plugin;
    private final DatabaseConfig config;
    private HikariDataSource dataSource;

    public DatabaseManager(SuperVanishReborn plugin, DatabaseConfig config) {
        this.plugin = plugin;
        this.config = config;
    }

    /**
     * Opens the connection pool and ensures the table exists.
     *
     * @throws SQLException if the pool cannot be initialised or the table cannot be
     *                      created.
     */
    public void connect() throws SQLException {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(config.buildJdbcUrl());
        hikariConfig.setDriverClassName(config.driverClassName());
        hikariConfig.setUsername(config.username());
        hikariConfig.setPassword(config.password());
        hikariConfig.setMaximumPoolSize(config.poolSize());
        hikariConfig.setMinimumIdle(1);
        hikariConfig.setConnectionTimeout(10_000);
        hikariConfig.setIdleTimeout(600_000);
        hikariConfig.setMaxLifetime(1_800_000);
        hikariConfig.setPoolName("SVRebornPool");

        dataSource = new HikariDataSource(hikariConfig);
        createTableIfNotExists();

        plugin.log(Level.INFO, "[SVReborn] Database connection pool opened ("
                + config.type() + " @ " + config.host() + ":" + config.port() + ").");
    }

    /** Closes the connection pool gracefully. */
    public void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            plugin.log(Level.INFO, "[SVReborn] Database connection pool closed.");
        }
    }

    /**
     * Marks a player as vanished in the database, or removes them if
     * {@code vanished} is false.
     *
     * @param uuid       the player's UUID
     * @param playerName the player's name
     * @param vanished   {@code true} to add/update, {@code false} to remove
     * @param serverId   a unique identifier for this server (e.g. the server name
     *                   from config)
     */
    public void setVanished(UUID uuid, String playerName, boolean vanished, String serverId) {
        if (vanished) {
            String sql = config.type().equals("postgresql") ? SQL_UPSERT_PG : SQL_UPSERT_MYSQL;
            try (Connection conn = dataSource.getConnection();
                    PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, uuid.toString());
                ps.setString(2, playerName);
                ps.setString(3, serverId);
                ps.setLong(4, System.currentTimeMillis());
                ps.executeUpdate();
            } catch (SQLException e) {
                plugin.log(Level.SEVERE, "[SVReborn] Failed to insert vanish state into database.", e);
            }
        } else {
            try (Connection conn = dataSource.getConnection();
                    PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
                ps.setString(1, uuid.toString());
                ps.executeUpdate();
            } catch (SQLException e) {
                plugin.log(Level.SEVERE, "[SVReborn] Failed to delete vanish state from database.", e);
            }
        }
    }

    /**
     * Returns the set of all vanished player UUIDs currently recorded in the
     * database.
     */
    public Set<UUID> getVanishedUUIDs() {
        Set<UUID> result = new HashSet<>();
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                try {
                    result.add(UUID.fromString(rs.getString("uuid")));
                } catch (IllegalArgumentException ignored) {
                    // skip malformed rows
                }
            }
        } catch (SQLException e) {
            plugin.log(Level.SEVERE, "[SVReborn] Failed to fetch vanished players from database.", e);
        }
        return result;
    }

    /**
     * Attempts to find a vanished player's UUID by their exact name in the
     * database.
     */
    public UUID getVanishedUUIDFromName(String name) {
        String sql = "SELECT uuid FROM " + TABLE_NAME + " WHERE player_name = ?";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return UUID.fromString(rs.getString("uuid"));
                }
            }
        } catch (SQLException | IllegalArgumentException e) {
            plugin.log(Level.SEVERE, "[SVReborn] Failed to fetch uuid from player name.", e);
        }
        return null;
    }

    /** Returns {@code true} if the pool has been initialised and is not closed. */
    public boolean isConnected() {
        return dataSource != null && !dataSource.isClosed();
    }

    // -------------------------------------------------------------------------
    // Internals
    // -------------------------------------------------------------------------

    private void createTableIfNotExists() throws SQLException {
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_CREATE_TABLE)) {
            ps.executeUpdate();
        }
    }
}
