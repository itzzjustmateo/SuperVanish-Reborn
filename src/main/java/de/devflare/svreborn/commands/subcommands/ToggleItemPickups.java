/*
 * Copyright © 2015, Leon Mangler and the SuperVanish contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.devflare.svreborn.commands.subcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.devflare.svreborn.SuperVanishReborn;
import de.devflare.svreborn.VanishPlayer;
import de.devflare.svreborn.commands.CommandAction;
import de.devflare.svreborn.commands.SubCommand;

public class ToggleItemPickups extends SubCommand {

    public ToggleItemPickups(SuperVanishReborn plugin) {
        super(plugin);
    }

    @Override
    public void execute(Command cmd, CommandSender sender, String[] args, String label) {
        if (canDo(sender, CommandAction.TOGGLE_ITEM_PICKUPS, true)) {
            Player p = (Player) sender;
            plugin.sendMessage(p, plugin.getMessage("ToggledPickingUpItems"
                    + (toggleState(plugin.getVanishPlayer(p)) ? "On" : "Off")), p);
        }
    }

    private boolean toggleState(VanishPlayer vp) {
        boolean hasEnabled = plugin.getPlayerData().getBoolean("PlayerData."
                + vp.getPlayerUUID() + ".itemPickUps");
        plugin.getPlayerData().set("PlayerData." + vp.getPlayerUUID() + ".itemPickUps", !hasEnabled);
        vp.setItemPickUps(!hasEnabled);
        plugin.getConfigMgr().getPlayerDataFile().save();
        return !hasEnabled;
    }
}
