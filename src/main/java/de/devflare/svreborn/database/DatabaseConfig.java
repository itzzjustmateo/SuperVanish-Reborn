/*
 * Copyright © 2015, Leon Mangler and the SuperVanish contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.devflare.svreborn.database;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * Immutable data holder for database connection settings read from config.yml.
 */
public record DatabaseConfig(
        boolean enabled,
        String type,
        String host,
        int port,
        String database,
        String username,
        String password,
        int poolSize,
        int syncIntervalTicks) {

    /**
     * Reads a {@link DatabaseConfig} from the plugin's settings file.
     */
    public static DatabaseConfig fromConfig(FileConfiguration config) {
        return new DatabaseConfig(
                config.getBoolean("database.enabled", false),
                config.getString("database.type", "mysql").toLowerCase(),
                config.getString("database.host", "localhost"),
                config.getInt("database.port", 3306),
                config.getString("database.database", "supervanish"),
                config.getString("database.username", "root"),
                config.getString("database.password", ""),
                config.getInt("database.pool_size", 4),
                config.getInt("database.sync_interval_ticks", 40));
    }

    /**
     * Builds the JDBC URL for the configured database type.
     */
    public String buildJdbcUrl() {
        return switch (type) {
            case "postgresql" -> "jdbc:postgresql://" + host + ":" + port + "/" + database;
            default ->
                // mysql and mariadb both use the mysql JDBC scheme
                "jdbc:mysql://" + host + ":" + port + "/" + database
                        + "?useSSL=false&characterEncoding=utf8&autoReconnect=true";
        };
    }

    /**
     * Returns the fully-qualified JDBC driver class name for the configured type.
     */
    public String driverClassName() {
        return switch (type) {
            case "postgresql" -> "org.postgresql.Driver";
            default -> "com.mysql.cj.jdbc.Driver";
        };
    }
}
