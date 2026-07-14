/*
 * Copyright © 2026, SuperVanish Reborn contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.devflare.svreborn.visibility;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import com.destroystokyo.paper.profile.PlayerProfile;

import de.devflare.svreborn.SuperVanishReborn;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class PaperServerPingListener implements Listener {

    private boolean errorLogged = false;

    private final SuperVanishReborn plugin;

    public PaperServerPingListener(SuperVanishReborn plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onServerListPing(PaperServerListPingEvent e) {
        try {
            final FileConfiguration settings = plugin.getSettings();
            if (!settings.getBoolean("ExternalInvisibility.ServerList.AdjustAmountOfOnlinePlayers")
                    && !settings.getBoolean("ExternalInvisibility.ServerList.AdjustListOfLoggedInPlayers"))
                return;
            Collection<UUID> onlineVanishedPlayers = plugin.getVanishStateMgr().getOnlineVanishedPlayers();
            int vanishedPlayersCount = onlineVanishedPlayers.size(),
                    playerCount = Bukkit.getOnlinePlayers().size();
            if (settings.getBoolean("ExternalInvisibility.ServerList.AdjustAmountOfOnlinePlayers")) {
                e.setNumPlayers(playerCount - vanishedPlayersCount);
            }
            if (settings.getBoolean("ExternalInvisibility.ServerList.AdjustListOfLoggedInPlayers")) {
                List<PlayerProfile> playerSample = e.getPlayerSample();

                playerSample.removeIf(profile -> onlineVanishedPlayers.contains(profile.getId()));
            }
        } catch (Exception er) {
            if (!errorLogged) {
                plugin.logException(er);
                errorLogged = true;
            }
        }
    }
}
