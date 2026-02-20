/*
 * Copyright Ãƒâ€šÃ‚Â© 2015, Leon Mangler and the SuperVanish contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.devflare.svreborn.hooks;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.dynmap.DynmapAPI;

import de.devflare.svreborn.api.PlayerHideEvent;
import de.devflare.svreborn.api.PlayerShowEvent;
import de.devflare.svreborn.SuperVanishReborn;

public class DynmapHook extends PluginHook {

    private final boolean sendJoinLeave;
    private final SuperVanishReborn superVanish;

    public DynmapHook(SuperVanishReborn superVanish) {
        super(superVanish);
        this.superVanish = superVanish;
        sendJoinLeave
                = superVanish.getSettings().getBoolean("HookOptions.DynmapSendJoinLeaveMessages")
                && !superVanish.getMessage("DynmapFakeJoin").equals("");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVanish(PlayerHideEvent e) {
        Player p = e.getPlayer();
        DynmapAPI dynmap = (DynmapAPI) plugin;

        dynmap.setPlayerVisiblity(p, false);
        if (sendJoinLeave)
            dynmap.sendBroadcastToWeb("",
                    superVanish.replacePlaceholders(superVanish.getMessage("DynmapFakeQuit"), p));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onReappear(PlayerShowEvent e) {
        Player p = e.getPlayer();
        DynmapAPI dynmap = (DynmapAPI) plugin;

        dynmap.setPlayerVisiblity(p, true);
        if (sendJoinLeave)
            dynmap.sendBroadcastToWeb("",
                    superVanish.replacePlaceholders(superVanish.getMessage("DynmapFakeJoin"), p));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        DynmapAPI dynmap = (DynmapAPI) plugin;

        if (superVanish.getVanishStateMgr().isVanished(p.getUniqueId())) {
            dynmap.setPlayerVisiblity(p, false);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        DynmapAPI dynmap = (DynmapAPI) plugin;

        if (superVanish.getVanishStateMgr().isVanished(p.getUniqueId())) {
            dynmap.setPlayerVisiblity(p, true);
        }
    }
}
