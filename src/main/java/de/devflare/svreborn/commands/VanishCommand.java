/*
 * Copyright Ãƒâ€šÃ‚Â© 2015, Leon Mangler and the SuperVanish contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.devflare.svreborn.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import de.devflare.svreborn.SuperVanishReborn;

import java.util.List;

public class VanishCommand {

    private final SuperVanishReborn plugin;
    private final SubCommandMgr subCommandMgr;

    public VanishCommand(SuperVanishReborn plugin) {
        this.plugin = plugin;
        subCommandMgr = new SubCommandMgr(plugin);
    }

    public void execute(Command command, CommandSender sender, String commandLabel, String[] args) {
        subCommandMgr.execute(command, sender, args, commandLabel);
    }

    public List<String> tabComplete(Command command, CommandSender sender, String alias, String[] args)
            throws IllegalArgumentException {
        return subCommandMgr.onTabComplete(command, sender, alias, args);
    }
}
