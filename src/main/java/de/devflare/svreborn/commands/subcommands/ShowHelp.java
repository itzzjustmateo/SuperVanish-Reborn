/*
 * Copyright Ãƒâ€šÃ‚Â© 2015, Leon Mangler and the SuperVanish contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * license, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.devflare.svreborn.commands.subcommands;


import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import de.devflare.svreborn.SuperVanishReborn;
import de.devflare.svreborn.commands.CommandAction;
import de.devflare.svreborn.commands.SubCommand;

public class ShowHelp extends SubCommand {

    public ShowHelp(SuperVanishReborn plugin) {
        super(plugin);
    }

    @Override
    public void execute(Command cmd, CommandSender sender, String[] args, String label) {
        if (canDo(sender, CommandAction.SHOW_HELP, true)) {
            plugin.sendMessage(sender, "HelpHeader", sender);
            for (CommandAction action : CommandAction.values()) {
                if (canDo(sender, action, false)) {
                    plugin.sendMessage(sender, plugin.getMessage("HelpFormat")
                            .replace("%usage%", action.getUsage())
                            .replace("%description%", action.getDescription())
                            .replace("%permission%", action.getMainPermission()), sender);
                }
            }
        }
    }
}
