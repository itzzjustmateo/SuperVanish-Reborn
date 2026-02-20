/*
 * Copyright Ãƒâ€šÃ‚Â© 2015, Leon Mangler and the SuperVanish contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.devflare.svreborn.features;

import org.bukkit.event.Listener;

import de.devflare.svreborn.SuperVanishReborn;

/**
 * Represents a toggleable feature of SuperVanish
 */
public abstract class Feature implements Listener {

    protected final SuperVanishReborn plugin;

    public Feature(SuperVanishReborn plugin) {
        this.plugin = plugin;
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public abstract boolean isActive();

    protected void delay(Runnable runnable) {
        plugin.getServer().getScheduler().runTaskLater(plugin, runnable, 1);
    }
}
