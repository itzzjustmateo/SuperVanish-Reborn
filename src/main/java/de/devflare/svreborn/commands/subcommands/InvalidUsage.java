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

import de.devflare.svreborn.SuperVanishReborn;
import de.devflare.svreborn.commands.CommandAction;
import de.devflare.svreborn.commands.SubCommand;

public class InvalidUsage extends SubCommand {

    public InvalidUsage(SuperVanishReborn plugin) {
        super(plugin);
    }

    @Override
    public void execute(Command cmd, CommandSender sender, String[] args, String label) {
        if (!CommandAction.hasAnyCmdPermission(sender, plugin)) {
            plugin.sendMessage(sender, "no_permission", sender);
            return;
        }
        plugin.sendMessage(sender, "invalid_usage", sender);
    }
}
