/*
 * Copyright © 2015, Leon Mangler and the SuperVanish contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.devflare.svreborn.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import de.devflare.svreborn.SuperVanishReborn;
import de.devflare.svreborn.commands.CommandAction;
import de.devflare.svreborn.commands.SubCommand;

public class Reload extends SubCommand {

    public Reload(SuperVanishReborn plugin) {
        super(plugin);
    }

    @Override
    public void execute(Command cmd, CommandSender p, String[] args, String label) {
        if (canDo(p, CommandAction.RELOAD, true)) {
            long before = System.currentTimeMillis();
            plugin.reload();
            if (!Bukkit.getPluginManager().isPluginEnabled(plugin)) {
                p.sendMessage(ChatColor.RED +
                        "Failed to reload SuperVanish since it failed to restart itself. " +
                        "More information is in the console. ("
                        + (System.currentTimeMillis() - before) + "ms)");
                return;
            }
            plugin.sendMessage(p, plugin.getMessage("PluginReloaded").replace("%time%",
                    (System.currentTimeMillis() - before) + ""), p);
        }
    }
}
