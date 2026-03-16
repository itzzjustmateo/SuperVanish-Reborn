/*
 * Copyright © 2015, Leon Mangler and the SuperVanish contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.devflare.svreborn.database;

import de.devflare.svreborn.SuperVanishReborn;
import de.devflare.svreborn.api.PlayerVanishStateChangeEvent;
import de.devflare.svreborn.visibility.VanishStateMgr;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * A {@link VanishStateMgr} implementation that persists and synchronises vanish
 * states via a shared relational database (MySQL, MariaDB, or PostgreSQL).
 *
 * <h3>Sync mechanism</h3>
 * <ul>
 * <li>Every write ({@link #setVanishedState}) is immediately applied to the
 * in-memory cache <em>and</em> written to the database.</li>
 * <li>A background polling task runs every
 * {@link DatabaseConfig#syncIntervalTicks()} ticks (async). It fetches the
 * full set of vanished UUIDs from the DB and diffs it against the cache.
 * Players that appeared on another server are hidden locally; players that
 * disappeared on another server are shown locally.</li>
 * </ul>
 */
public class DatabaseVanishStateMgr extends VanishStateMgr {

    /**
     * The unique identifier for this server instance (from config, falls back to
     * hostname).
     */
    private final String serverId;

    /** Thread-safe in-memory cache of vanished UUIDs. */
    private final Set<UUID> cache = ConcurrentHashMap.newKeySet();

    private final DatabaseManager dbManager;
    private final DatabaseConfig dbConfig;
    private int pollTaskId = -1;

    public DatabaseVanishStateMgr(SuperVanishReborn plugin, DatabaseManager dbManager,
            DatabaseConfig dbConfig) {
        super(plugin);
        this.dbManager = dbManager;
        this.dbConfig = dbConfig;

        // Determine server ID: read from config or fall back to localhost hostname
        String cfgId = plugin.getSettings().getString("database.server_id", "").trim();
        this.serverId = cfgId.isEmpty() ? resolveServerId() : cfgId;

        // Seed cache from DB on startup
        cache.addAll(dbManager.getVanishedUUIDs());

        // Start asynchronous polling task
        schedulePollTask();
    }

    // -------------------------------------------------------------------------
    // VanishStateMgr contract
    // -------------------------------------------------------------------------

    @Override
    public boolean isVanished(UUID uuid) {
        return cache.contains(uuid);
    }

    @Override
    public void setVanishedState(UUID uuid, String name, boolean hide, String causeName) {
        // Fire the cancellable API event (main thread)
        PlayerVanishStateChangeEvent event = new PlayerVanishStateChangeEvent(uuid, name, hide, causeName);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return;

        // Update cache immediately
        if (hide) {
            cache.add(uuid);
        } else {
            cache.remove(uuid);
        }

        // Persist asynchronously to avoid blocking the main thread
        Bukkit.getScheduler().runTaskAsynchronously((SuperVanishReborn) plugin,
                () -> dbManager.setVanished(uuid, name, hide, serverId));
    }

    @Override
    public Set<UUID> getVanishedPlayers() {
        return Collections.unmodifiableSet(new HashSet<>(cache));
    }

    @Override
    public Collection<UUID> getOnlineVanishedPlayers() {
        Set<UUID> online = new HashSet<>();
        for (UUID uuid : cache) {
            if (Bukkit.getPlayer(uuid) != null)
                online.add(uuid);
        }
        return online;
    }

    @Override
    public UUID getVanishedUUIDFromName(String name) {
        if (!dbManager.isConnected())
            return null;
        return dbManager.getVanishedUUIDFromName(name);
    }

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    /** Cancels the polling task. Call this from {@code onDisable}. */
    public void shutdown() {
        if (pollTaskId != -1) {
            Bukkit.getScheduler().cancelTask(pollTaskId);
            pollTaskId = -1;
        }
    }

    // -------------------------------------------------------------------------
    // Internals
    // -------------------------------------------------------------------------

    private void schedulePollTask() {
        long interval = Math.max(10, dbConfig.syncIntervalTicks()); // at least 10 ticks
        pollTaskId = Bukkit.getScheduler().runTaskTimerAsynchronously(
                (SuperVanishReborn) plugin, this::pollDatabase, interval, interval).getTaskId();
    }

    /**
     * Fetches the current vanished set from the DB, diffs against the local cache,
     * and applies visibility changes on the main thread for any differences caused
     * by another server.
     */
    private void pollDatabase() {
        if (!dbManager.isConnected())
            return;

        Set<UUID> dbSet;
        try {
            dbSet = dbManager.getVanishedUUIDs();
        } catch (Exception e) {
            plugin.log(Level.WARNING, "[SVReborn] Database poll failed: " + e.getMessage());
            return;
        }

        // UUIDs newly vanished on another server (in DB but not in our cache)
        Set<UUID> nowVanished = new HashSet<>(dbSet);
        nowVanished.removeAll(cache);

        // UUIDs newly unvanished on another server (in our cache but no longer in DB)
        Set<UUID> nowVisible = new HashSet<>(cache);
        nowVisible.removeAll(dbSet);

        if (nowVanished.isEmpty() && nowVisible.isEmpty())
            return;

        // Apply cache changes and visibility updates on the main thread
        Bukkit.getScheduler().runTask((SuperVanishReborn) plugin, () -> {
            for (UUID uuid : nowVanished) {
                cache.add(uuid);
                Player vanished = Bukkit.getPlayer(uuid);
                if (vanished == null)
                    continue; // not online on this server
                for (Player viewer : Bukkit.getOnlinePlayers()) {
                    if (!((SuperVanishReborn) plugin).hasPermissionToSee(viewer, vanished)) {
                        ((SuperVanishReborn) plugin).getVisibilityChanger()
                                .getHider().setHidden(vanished, viewer, true);
                    }
                }
            }
            for (UUID uuid : nowVisible) {
                cache.remove(uuid);
                Player shown = Bukkit.getPlayer(uuid);
                if (shown == null)
                    continue;
                for (Player viewer : Bukkit.getOnlinePlayers()) {
                    ((SuperVanishReborn) plugin).getVisibilityChanger()
                            .getHider().setHidden(shown, viewer, false);
                }
            }
        });
    }

    private static String resolveServerId() {
        try {
            return java.net.InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "unknown-server";
        }
    }
}
