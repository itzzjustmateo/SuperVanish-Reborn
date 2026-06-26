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

package de.devflare.svreborn.hooks;

import com.griefprevention.events.BoundaryVisualizationEvent;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import de.devflare.svreborn.SuperVanishReborn;

public class GriefPreventionHook extends PluginHook {

    public GriefPreventionHook(SuperVanishReborn superVanish) {
        super(superVanish);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBoundaryVisualization(BoundaryVisualizationEvent event) {
        Player player = event.getPlayer();
        if (superVanish.getVanishStateMgr().isVanished(player.getUniqueId())) {
            event.setProvider(null);
        }
    }
}
