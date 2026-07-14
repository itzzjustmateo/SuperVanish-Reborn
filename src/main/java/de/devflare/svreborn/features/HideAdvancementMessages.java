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

package de.devflare.svreborn.features;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

import de.devflare.svreborn.SuperVanishReborn;

// This feature is paper-only because the PlayerAdvancementDoneEvent#message() method doesn't exist in Spigot
public class HideAdvancementMessages extends Feature {

    private boolean suppressErrors = false;

    public HideAdvancementMessages(SuperVanishReborn plugin) {
        super(plugin);
    }

    @EventHandler
    public void onAdvancementDone(PlayerAdvancementDoneEvent e) {
        try {
            Player p = e.getPlayer();
            Component message = e.message();
            if (message == null)
                return;
            if (!plugin.getVanishStateMgr().isVanished(p.getUniqueId()))
                return;
            if (e.message() == null)
                return;
            e.message(null);
            p.sendMessage(message);
        } catch (Exception er) {
            if (!suppressErrors) {
                plugin.logException(er);
                suppressErrors = true;
            }
        }
    }

    @Override
    public boolean isActive() {
        return plugin.getSettings().getBoolean("message_options.hide_advancement_messages", true);
    }
}
