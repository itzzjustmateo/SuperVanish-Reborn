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

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import de.devflare.svreborn.SuperVanishReborn;

import java.util.logging.Level;

public class ExceptionLogger {

    private ExceptionLogger() {
    }

    public static void logException(Throwable e, SuperVanishReborn plugin) {
        try {
            final boolean realEx = e != null;
            Level loggingLevel = realEx ? Level.WARNING : Level.INFO;
            if (realEx) plugin.log(loggingLevel, "Unknown exception occurred!");
            else plugin.log(loggingLevel, "Printing information...");
            if (plugin.getConfigMgr().isSettingsUpdateRequired()
                    || plugin.getConfigMgr().isMessagesUpdateRequired()) {
                if (realEx) plugin.log(loggingLevel, "You have an outdated configuration, " +
                        "regenerating it by using '/sv recreatefiles' might fix this problem!");
                else plugin.log(loggingLevel, "Configuration is outdated.");
            } else if (realEx) plugin.log(loggingLevel, "Please report this issue!");
            if (!realEx) e = new RuntimeException("Information for reporting an issue");
            plugin.log(loggingLevel, "Message: ");
            plugin.log(loggingLevel, "  " + e.getMessage());
            plugin.log(loggingLevel, "General information: ");
            StringBuilder plugins = new StringBuilder();
            for (Plugin pl : Bukkit.getServer().getPluginManager().getPlugins()) {
                if (pl.getName().equalsIgnoreCase("SuperVanish")) continue;
                plugins.append(pl.getName()).append(" v")
                        .append(pl.getDescription().getVersion()).append(", ");
            }
            plugins = new StringBuilder(plugins.substring(0, plugins.length() - 1));
            plugin.log(loggingLevel, "  ServerVersion: " + plugin.getServer().getVersion());
            plugin.log(loggingLevel, "  PluginVersion: "
                    + plugin.getDescription().getVersion());
            plugin.log(loggingLevel, "  ServerPlugins: " + plugins);
            try {
                plugin.log(loggingLevel, "Settings:");
                plugin.log(loggingLevel, "  MsgsVersion: "
                        + plugin.getMessages().getString("messages_version"));
                StringBuilder settings = new StringBuilder("||");
                for (String key : plugin.getSettings().getKeys(true)) {
                    if (!plugin.getSettings().getString(key).contains("MemorySection"))
                        settings.append(key).append(">")
                                .append(plugin.getSettings().getString(key)).append("||");
                }
                plugin.log(loggingLevel, "  Settings: " + settings);
            } catch (Exception er) {
                plugin.log(Level.SEVERE, ">> Error occurred while trying to print config info <<");
            }
            plugin.log(loggingLevel, "StackTrace: ");
            e.printStackTrace();
            if (realEx) {
                if (plugin.getConfigMgr().isSettingsUpdateRequired()
                        || plugin.getConfigMgr().isMessagesUpdateRequired())
                    plugin.log(loggingLevel, "You have an outdated configuration, " +
                            "regenerating it by using '/sv recreatefiles' might fix this problem!");
                plugin.log(loggingLevel, "Please include this information");
                plugin.log(loggingLevel, "if you report the issue.");
            } else
                plugin.log(loggingLevel, "End of information.");
        } catch (Exception e2) {
            plugin.log(Level.WARNING,
                    "An exception occurred while trying to print a detailed stacktrace, " +
                            "printing an undetailed stacktrace of both exceptions:");
            plugin.log(Level.SEVERE, "ORIGINAL EXCEPTION:");
            if (e != null) {
                e.printStackTrace();
            }
            plugin.log(Level.SEVERE, "SECOND EXCEPTION:");
            e2.printStackTrace();
        }
    }
}
