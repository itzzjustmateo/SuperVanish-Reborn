/*
 * Copyright © 2015, Leon Mangler and the SuperVanish contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.devflare.svreborn;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Arrays;
import java.util.List;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import de.devflare.svreborn.hooks.PlaceholderAPIHook;

public class PlaceholderConverter {

    private final SuperVanishReborn plugin;

    public PlaceholderConverter(SuperVanishReborn plugin) {
        this.plugin = plugin;
    }

    public String replacePlaceholders(String msg, Object... additionalPlayerInfo) {
        if (additionalPlayerInfo == null || additionalPlayerInfo.length == 0) {
            return msg;
        }

        // check vararg
        final List<Object> additionalPlayerInfoList = Arrays.asList(additionalPlayerInfo);
        Object unspecifiedPlayer = additionalPlayerInfoList.get(0);
        String unspecifiedOtherPlayersName = null;

        if (additionalPlayerInfoList.size() > 1
                && (additionalPlayerInfoList.get(1) instanceof String
                        || additionalPlayerInfoList.get(1) instanceof Player)) {
            unspecifiedOtherPlayersName = (String) (additionalPlayerInfoList.get(1) instanceof Player
                    ? ((Player) additionalPlayerInfoList.get(1)).getName()
                    : additionalPlayerInfoList.get(1));
        }

        if (msg == null)
            return null;
        msg = msg.replace("\\n", "\n");

        // replace sender specific variables
        replaceVariables: {
            if (unspecifiedPlayer instanceof OfflinePlayer
                    && !(unspecifiedPlayer instanceof Player)) {
                // offline player
                OfflinePlayer specifiedPlayer = (OfflinePlayer) unspecifiedPlayer;
                // MVdWPlaceholderAPI
                if (Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")
                        && plugin.getSettings().getBoolean("hook_options.enable_mvdw_placeholder_api", true)) {
                    String replaced = PlaceholderAPI.replacePlaceholders(specifiedPlayer, msg);
                    msg = replaced == null ? msg : replaced;
                }
                // replace essentials nick names
                if (Bukkit.getPluginManager().getPlugin("Essentials") != null) {
                    msg = msg.replace("%player%", specifiedPlayer.getName());
                    msg = msg.replace("%essentials_nick%", specifiedPlayer.getName());
                    msg = msg.replace("%nick%", specifiedPlayer.getName());
                }
                // replace general variables
                msg = msg.replace("%display_name%", specifiedPlayer.getName())
                        .replace("%player%", specifiedPlayer.getName())
                        .replace("%tab_name%", specifiedPlayer.getName())
                        .replace("%d%", specifiedPlayer.getName())
                        .replace("%p%", specifiedPlayer.getName())
                        .replace("%tab%", specifiedPlayer.getName());
                // replace other player's name if possible
                msg = msg.replace("%target%", unspecifiedOtherPlayersName != null
                        ? unspecifiedOtherPlayersName
                        : "UNKNOWN");
                msg = msg.replace("%other%", unspecifiedOtherPlayersName != null
                        ? unspecifiedOtherPlayersName
                        : "UNKNOWN");
                break replaceVariables;
            }
            if (unspecifiedPlayer instanceof Player) {
                // player
                Player specifiedPlayer = (Player) unspecifiedPlayer;
                // PlaceholderAPI
                if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")
                        && plugin.getSettings().getBoolean("hook_options.enable_placeholder_api", true)) {
                    String replaced = PlaceholderAPIHook.translatePlaceholders(msg, specifiedPlayer);
                    msg = replaced == null ? msg : replaced;
                }
                // MVdWPlaceholderAPI
                if (Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")
                        && plugin.getSettings().getBoolean("hook_options.enable_mvdw_placeholder_api", true)) {
                    String replaced = PlaceholderAPI.replacePlaceholders(specifiedPlayer, msg);
                    msg = replaced == null ? msg : replaced;
                }
                // replace essentials nick names
                if (Bukkit.getPluginManager().getPlugin("Essentials") != null) {
                    Essentials ess = (Essentials) Bukkit.getServer()
                            .getPluginManager().getPlugin("Essentials");
                    User u = ess.getUser(specifiedPlayer);
                    if (u != null)
                        if (u.getNickname() != null) {
                            msg = msg.replace("%essentials_nick%", u.getNickname());
                            msg = msg.replace("%nick%", u.getNickname());
                        }
                }
                // replace vault info
                if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
                    RegisteredServiceProvider<Permission> permService = plugin.getServer()
                            .getServicesManager().getRegistration(Permission.class);
                    RegisteredServiceProvider<Chat> chatService = plugin.getServer()
                            .getServicesManager().getRegistration(Chat.class);
                    Permission permAPI = permService != null ? permService.getProvider() : null;
                    Chat chatAPI = chatService != null ? chatService.getProvider() : null;
                    try {
                        if (permAPI != null) {
                            String group = permAPI.getPrimaryGroup(specifiedPlayer);
                            if (group != null) {
                                msg = msg.replace("%player_group%", group);
                                msg = msg.replace("%group%", group);
                            }
                        }
                        if (chatAPI != null) {
                            String prefix = chatAPI.getPlayerPrefix(specifiedPlayer);
                            String suffix = chatAPI.getPlayerSuffix(specifiedPlayer);
                            if (prefix != null) {
                                msg = msg.replace("%vault_prefix%", prefix);
                                msg = msg.replace("%prefix%", prefix);
                            }
                            if (suffix != null) {
                                msg = msg.replace("%vault_suffix%", suffix);
                                msg = msg.replace("%suffix%", suffix);
                            }
                        }
                    } catch (UnsupportedOperationException ignored) {
                    }
                }
                // replace general variables
                msg = msg
                        .replace("%display_name%",
                                LegacyComponentSerializer.legacySection().serialize(specifiedPlayer.displayName()))
                        .replace("%player%", specifiedPlayer.getName())
                        .replace("%tab_name%",
                                LegacyComponentSerializer.legacySection().serialize(specifiedPlayer.playerListName()))
                        .replace("%d%",
                                LegacyComponentSerializer.legacySection().serialize(specifiedPlayer.displayName()))
                        .replace("%p%", specifiedPlayer.getName())
                        .replace("%tab%",
                                LegacyComponentSerializer.legacySection().serialize(specifiedPlayer.playerListName()));
                // replace other player's name if possible
                msg = msg.replace("%target%", unspecifiedOtherPlayersName != null
                        ? unspecifiedOtherPlayersName
                        : "UNKNOWN");
                msg = msg.replace("%other%", unspecifiedOtherPlayersName != null
                        ? unspecifiedOtherPlayersName
                        : "UNKNOWN");

                break replaceVariables;
            }
            if (unspecifiedPlayer instanceof CommandSender) {
                // console
                // replace general variables
                msg = msg.replace("%display_name%", "Console").replace("%player%", "Console")
                        .replace("%tab_name%", "Console")
                        .replace("%d%", "Console").replace("%p%", "Console")
                        .replace("%tab%", "Console");
                // replace other player's name if possible
                msg = msg.replace("%target%", unspecifiedOtherPlayersName != null
                        ? unspecifiedOtherPlayersName
                        : "UNKNOWN");
                msg = msg.replace("%other%", unspecifiedOtherPlayersName != null
                        ? unspecifiedOtherPlayersName
                        : "UNKNOWN");
            }
        }
        // convert color codes
        if (msg.contains("&") || msg.contains("§")) {
            // Convert legacy codes to MiniMessage tags for internal consistency
            msg = MiniMessage.miniMessage().serialize(LegacyComponentSerializer.legacyAmpersand().deserialize(msg));
        }
        return msg;
    }
}
