/*
 * Copyright © 2015, Leon Mangler and the SuperVanish contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.devflare.svreborn.features;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import de.devflare.svreborn.api.PlayerShowEvent;
import de.devflare.svreborn.api.PostPlayerHideEvent;
import de.devflare.svreborn.SuperVanishReborn;

public class Broadcast extends Feature {

    public Broadcast(SuperVanishReborn plugin) {
        super(plugin);
    }

    public static void announceSilentJoin(Player vanished, SuperVanishReborn plugin) {
        if (plugin.getSettings().getBoolean("message_options.announce_real_join_quit_to_admins", true)) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (vanished == onlinePlayer)
                    continue;
                if (plugin.canSee(onlinePlayer, vanished)) {
                    plugin.sendMessage(onlinePlayer, "silent_join_message_for_admins", vanished, onlinePlayer);
                }
            }
        }
    }

    public static void announceSilentDeath(Player p, SuperVanishReborn plugin, String deathMessage) {
        if (plugin.getSettings().getBoolean("message_options.announce_death_to_admins", true)) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (p == onlinePlayer)
                    continue;
                if (plugin.canSee(onlinePlayer, p)) {
                    String message = plugin.getMessage("silent_death_message")
                            .replace("%death_message%", deathMessage);
                    plugin.sendMessage(onlinePlayer, message, p, onlinePlayer);
                }
            }
        }
    }

    public static void announceSilentQuit(Player p, SuperVanishReborn plugin) {
        if (plugin.getSettings().getBoolean("message_options.announce_real_join_quit_to_admins", true)) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (p == onlinePlayer)
                    continue;
                if (plugin.canSee(onlinePlayer, p)) {
                    plugin.sendMessage(onlinePlayer, "silent_quit_message_for_admins", p, onlinePlayer);
                }
            }
        }
    }

    @Override
    public boolean isActive() {
        return plugin.getSettings().getBoolean("message_options.fake_join_quit_messages.broadcast_fake_quit_on_vanish")
                || plugin.getSettings().getBoolean("message_options.fake_join_quit_messages" +
                        ".broadcast_fake_join_on_reappear");
    }

    @EventHandler
    public void onVanish(PostPlayerHideEvent e) {
        final Player p = e.getPlayer();
        if (plugin.getSettings().getBoolean("MessageOptions.FakeJoinQuitMessages.BroadcastFakeQuitOnVanish")
                && !e.isSilent()) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!plugin.canSee(onlinePlayer, p)) {
                    if (!plugin.getSettings().getBoolean(
                            "MessageOptions.FakeJoinQuitMessages.SendMessageOnlyToAdmins"))
                        plugin.sendMessage(onlinePlayer, "VanishMessage", p, onlinePlayer);
                } else if (!plugin.getSettings().getBoolean(
                        "MessageOptions.FakeJoinQuitMessages.SendMessageOnlyToUsers"))
                    if (!plugin.getSettings().getBoolean(
                            "MessageOptions.FakeJoinQuitMessages.AnnounceVanishReappearToAdmins"))
                        plugin.sendMessage(onlinePlayer, "VanishMessage", p, onlinePlayer);
                    else if (onlinePlayer == p && !plugin.getSettings().getBoolean(
                            "MessageOptions.FakeJoinQuitMessages.SendMessageOnlyToAdmins"))
                        plugin.sendMessage(onlinePlayer, "VanishMessage", p, onlinePlayer);
                    else if (onlinePlayer != p)
                        plugin.sendMessage(onlinePlayer, "VanishMessageWithPermission", p, onlinePlayer);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onReappear(PlayerShowEvent e) {
        Player p = e.getPlayer();
        if (plugin.getSettings().getBoolean(
                "message_options.fake_join_quit_messages.broadcast_fake_join_on_reappear") && !e.isSilent()) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!plugin.canSee(onlinePlayer, p)) {
                    if (!plugin.getSettings().getBoolean(
                            "message_options.fake_join_quit_messages.send_message_only_to_admins"))
                        plugin.sendMessage(onlinePlayer, "reappear_message", p, onlinePlayer);
                } else if (!plugin.getSettings().getBoolean(
                        "message_options.fake_join_quit_messages.send_message_only_to_users"))
                    if (!plugin.getSettings().getBoolean(
                            "message_options.fake_join_quit_messages.announce_vanish_reappear_to_admins"))
                        plugin.sendMessage(onlinePlayer, "reappear_message_with_permission", p, onlinePlayer);
                    else if (onlinePlayer == p && !plugin.getSettings().getBoolean(
                            "message_options.fake_join_quit_messages.send_message_only_to_admins"))
                        plugin.sendMessage(onlinePlayer, "reappear_message", p, onlinePlayer);
                    else if (onlinePlayer != p)
                        plugin.sendMessage(onlinePlayer, "reappear_message_with_permission", p, onlinePlayer);
            }
        }
    }
}
