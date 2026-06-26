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

package de.devflare.svreborn.utils;

import org.bukkit.entity.Player;

import de.devflare.svreborn.SuperVanishReborn;

public class BukkitPlayerHidingUtil {

    private BukkitPlayerHidingUtil() {
    }

    public static void hidePlayer(Player player, Player viewer, SuperVanishReborn plugin) {
        if (isNewPlayerHidingAPISupported(plugin))
            viewer.hidePlayer(plugin, player);
        else
            //noinspection deprecation
            viewer.hidePlayer(player);
    }

    public static void showPlayer(Player player, Player viewer, SuperVanishReborn plugin) {
        if (isNewPlayerHidingAPISupported(plugin))
            viewer.showPlayer(plugin, player);
        else
            //noinspection deprecation
            viewer.showPlayer(player);
    }

    public static boolean isNewPlayerHidingAPISupported(SuperVanishReborn plugin) {
        return plugin.getVersionUtil().isOneDotXOrHigher(19);
    }
}
