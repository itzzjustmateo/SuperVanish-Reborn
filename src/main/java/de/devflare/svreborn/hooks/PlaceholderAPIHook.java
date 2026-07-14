/*
 * Copyright © 2015, Leon Mangler and the SuperVanish contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.devflare.svreborn.hooks;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import de.devflare.svreborn.SuperVanishReborn;

import java.util.Collection;
import java.util.UUID;

public class PlaceholderAPIHook extends PluginHook {

    private final String yes, no, prefix, suffix;

    public PlaceholderAPIHook(SuperVanishReborn superVanish) {
        super(superVanish);
        yes = superVanish.getMessage("PlaceholderIsVanishedYes");
        no = superVanish.getMessage("PlaceholderIsVanishedNo");
        prefix = superVanish.getMessage("PlaceholderVanishPrefix");
        suffix = superVanish.getMessage("PlaceholderVanishSuffix");
        new SVPlaceholderExpansion().register();
    }

    public static String translatePlaceholders(String msg, Player p) {
        return PlaceholderAPI.setPlaceholders((OfflinePlayer) p, msg);
    }

    public class SVPlaceholderExpansion extends PlaceholderExpansion {

        @Override
        public boolean persist() {
            return true;
        }

        @Override
        public boolean canRegister() {
            return true;
        }

        @Override
        public String getAuthor() {
            return superVanish.getDescription().getAuthors().toString();
        }

        @Override
        public String getIdentifier() {
            return "supervanish";
        }

        @Override
        public String getVersion() {
            return superVanish.getDescription().getVersion();
        }

        @Override
        public String onRequest(OfflinePlayer op, String id) {
            try {
                Player p = op instanceof Player ? (Player) op : null;
                String lower = id.toLowerCase();

                if (lower.equals("isvanished") || lower.equals("isinvisible")
                        || lower.equals("vanished") || lower.equals("invisible"))
                    return p != null && superVanish.getVanishStateMgr().isVanished(p.getUniqueId())
                            ? yes : no;

                if (lower.equals("vanishprefix"))
                    return p != null && superVanish.getVanishStateMgr().isVanished(p.getUniqueId())
                            ? prefix : "";

                if (lower.equals("vanishsuffix"))
                    return p != null && superVanish.getVanishStateMgr().isVanished(p.getUniqueId())
                            ? suffix : "";

                if (lower.equals("can_see") && p != null) {
                    boolean canSee = superVanish.getSettings()
                            .getBoolean("IndicationFeatures.LayeredPermissions.EnableSeePermission", true)
                            && superVanish.getLayeredPermissionChecker()
                                    .getLayeredPermissionLevel(p, "see") > 0;
                    return canSee ? yes : no;
                }

                if (lower.startsWith("is_vanished_") && p != null) {
                    String targetName = lower.substring("is_vanished_".length());
                    Player target = Bukkit.getPlayerExact(targetName);
                    if (target != null) {
                        return superVanish.getVanishStateMgr().isVanished(target.getUniqueId())
                                ? yes : no;
                    }
                    return no;
                }

                if (lower.equals("server_vanished_count")) {
                    return String.valueOf(superVanish.getVanishStateMgr()
                            .getOnlineVanishedPlayers().size());
                }

                if (lower.equals("total_vanished_count")) {
                    return String.valueOf(superVanish.getVanishStateMgr()
                            .getVanishedPlayers().size());
                }

                if (lower.equals("onlinevanishedplayers")
                        || lower.equals("onlinevanished")
                        || lower.equals("invisibleplayers")
                        || lower.equals("vanishedplayers")
                        || lower.equals("hiddenplayers")) {
                    Collection<UUID> onlineVanishedPlayers = superVanish.getVanishStateMgr()
                            .getOnlineVanishedPlayers();
                    StringBuilder playerList = new StringBuilder();
                    for (UUID uuid : onlineVanishedPlayers) {
                        Player onlineVanished = Bukkit.getPlayer(uuid);
                        if (onlineVanished == null) continue;
                        if (superVanish.getSettings().getBoolean(
                                "IndicationFeatures.LayeredPermissions.HideInvisibleInCommands", false)
                                && p != null && !superVanish.hasPermissionToSee(p, onlineVanished)) {
                            continue;
                        }
                        if (!playerList.isEmpty()) playerList.append(", ");
                        playerList.append(onlineVanished.getName());
                    }
                    return playerList.toString();
                }

                if (lower.equals("playercount") || lower.equals("onlineplayers")) {
                    int playercount = Bukkit.getOnlinePlayers().size();
                    for (UUID uuid : superVanish.getVanishStateMgr().getOnlineVanishedPlayers()) {
                        Player onlineVanished = Bukkit.getPlayer(uuid);
                        if (onlineVanished == null) continue;
                        if (p == null || !superVanish.canSee(p, onlineVanished))
                            playercount--;
                    }
                    return String.valueOf(playercount);
                }

                if (lower.equals("can_see_vanished")) {
                    if (p == null) return no;
                    for (UUID uuid : superVanish.getVanishStateMgr().getOnlineVanishedPlayers()) {
                        Player vanished = Bukkit.getPlayer(uuid);
                        if (vanished != null && superVanish.canSee(p, vanished))
                            return yes;
                    }
                    return no;
                }

            } catch (Exception e) {
                superVanish.logException(e);
            }
            return null;
        }
    }
}
