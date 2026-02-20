/*
 * Copyright Ãƒâ€šÃ‚Â© 2015, Leon Mangler and the SuperVanish contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.devflare.svreborn.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import de.devflare.svreborn.SuperVanishReborn;
import de.devflare.svreborn.commands.CommandAction;

public class WorldChangeListener implements Listener {

    private final SuperVanishReborn plugin;

    public WorldChangeListener(SuperVanishReborn plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldChange(PlayerChangedWorldEvent e) {
        try {
            final Player p = e.getPlayer();
            if (!plugin.getVanishStateMgr().isVanished(p.getUniqueId()))
                return;
            // check auto-reappear option
            if (plugin.getSettings().getBoolean("VanishStateFeatures.ReappearOnWorldChange")
                    || plugin.getSettings().getBoolean("VanishStateFeatures.CheckPermissionOnWorldChange")
                    && !CommandAction.VANISH_SELF.checkPermission(p, plugin)) {
                plugin.getVisibilityChanger().showPlayer(p);
            }
        } catch (Exception er) {
            plugin.logException(er);
        }
    }
}
