/*
 * Copyright © 2015, Leon Mangler and the SuperVanish contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.devflare.svreborn.listeners;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.EventExecutor;

import de.devflare.svreborn.SuperVanishReborn;
import de.devflare.svreborn.commands.CommandAction;
import de.devflare.svreborn.features.Broadcast;

public class QuitListener implements EventExecutor, Listener {

    private final SuperVanishReborn plugin;

    public QuitListener(SuperVanishReborn plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Listener l, Event event) {
        try {
            if (event instanceof PlayerQuitEvent) {
                PlayerQuitEvent e = (PlayerQuitEvent) event;
                FileConfiguration config = plugin.getConfig();
                Player p = e.getPlayer();
                // if is invisible
                if (plugin.getVanishStateMgr().isVanished(p.getUniqueId())) {
                    // remove action bar
                    if (plugin.getActionBarMgr() != null && plugin.getSettings().getBoolean(
                            "message_options.display_action_bar")) {
                        plugin.getActionBarMgr().removeActionBar(p);
                    }
                    // check auto-reappear-option
                    boolean noMsg = false;
                    if (plugin.getSettings().getBoolean("vanish_state_features.reappear_on_quit")
                            || plugin.getSettings().getBoolean("vanish_state_features.check_permission_on_quit")
                                    && !CommandAction.VANISH_SELF.checkPermission(p, plugin)) {
                        plugin.getVanishStateMgr().setVanishedState(p.getUniqueId(), p.getName(), false, null);
                        // check if it should handle the quit msg
                        if (!config.getBoolean("message_options.reappear_on_quit_hide_leave"))
                            noMsg = true;
                    }
                    // check remove-quit-msg option
                    if (!noMsg && config.getBoolean("message_options.hide_real_join_quit")) {
                        e.setQuitMessage(null);
                        Broadcast.announceSilentQuit(p, plugin);
                    }
                }
                // remove VanishPlayer
                plugin.removeVanishPlayer(plugin.getVanishPlayer(p));
            }
        } catch (Exception er) {
            plugin.logException(er);
        }
    }
}
