/*
 * Copyright © 2015, Leon Mangler and the SuperVanish contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.devflare.svreborn.hooks;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import de.devflare.svreborn.SuperVanishReborn;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import static java.util.Map.entry;

public class PluginHookMgr implements Listener {

    private static final Map<String, Class<? extends PluginHook>> REGISTERED_HOOKS
            = Map.ofEntries(
            entry("Essentials", EssentialsHook.class),
            entry("Citizens", CitizensHook.class),
            entry("PlaceholderAPI", PlaceholderAPIHook.class),
            entry("dynmap", DynmapHook.class),
            entry("TrailGUI", TrailGUIHook.class),
            entry("MVdWPlaceholderAPI", MVdWPlaceholderAPIHook.class),
            entry("OpenInv", OpenInvHook.class),
            entry("GriefPrevention", GriefPreventionHook.class)
    );
    private final SuperVanishReborn plugin;
    private final Set<PluginHook> activeHooks = new HashSet<>();

    public PluginHookMgr(SuperVanishReborn plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        for (Plugin alreadyEnabledPlugin : Bukkit.getPluginManager().getPlugins())
            if (alreadyEnabledPlugin.isEnabled())
                onPluginEnable(new PluginEnableEvent(alreadyEnabledPlugin));
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent e) {
        Plugin plugin = e.getPlugin();
        if (!REGISTERED_HOOKS.containsKey(plugin.getName())) return;
        if (isHookDisabled(plugin.getName())) return;
        PluginHook hook = null;
        try {
            hook = REGISTERED_HOOKS.get(plugin.getName())
                    .getConstructor(SuperVanishReborn.class)
                    .newInstance(PluginHookMgr.this.plugin);
            hook.setPlugin(plugin);
            hook.onPluginEnable(plugin);
            Bukkit.getPluginManager().registerEvents(hook, plugin);
            activeHooks.add(hook);
            PluginHookMgr.this.plugin.log(
                    Level.INFO, "Hooked into " + plugin.getName());
        } catch (NoClassDefFoundError er) {
            Bukkit.getLogger().warning("NoClassDefFoundError for SV-Hook(v"
                    + this.plugin.getDescription().getVersion() + ") "
                    + (hook != null ? hook.getClass().getSimpleName() : "?") + " of plugin "
                    + plugin.getName() + " v" + plugin.getDescription().getVersion()
                    + ", please report this if you are using the latest version of that plugin!");
        } catch (Exception er) {
            if (er.getMessage() != null
                    && er.getMessage().contains("Unable to find handler list for event")) {
                this.plugin.log(Level.WARNING, er.getMessage()
                        + "; This is not an issue with SuperVanish");
                return;
            } else if (er.getCause() != null && er.getCause().getMessage() != null && er.getCause()
                    .getMessage().contains("Unable to find handler list for event")) {
                this.plugin.log(Level.WARNING, er.getCause().getMessage()
                        + "; This is not an issue with SuperVanish");
                return;
            }
            this.plugin.logException(new InvalidPluginHookException(er));
            this.plugin.log(Level.WARNING, "Affected by this error is only the "
                    + plugin.getName() + "-Hook, all other hooks and features aren't affected.");
        }
    }

    private static final Map<String, String> HOOK_CONFIG_KEYS = Map.ofEntries(
            entry("Essentials", "enable_essentials"),
            entry("dynmap", "enable_dynmap"),
            entry("TrailGUI", "enable_trail_gui"),
            entry("PlaceholderAPI", "enable_placeholder_api"),
            entry("MVdWPlaceholderAPI", "enable_mvdw_placeholder_api"),
            entry("Citizens", "enable_citizens"),
            entry("OpenInv", "enable_open_inv"),
            entry("GriefPrevention", "enable_grief_prevention")
    );

    private boolean isHookDisabled(String pluginName) {
        FileConfiguration config = plugin.getSettings();
        String configKey = "hook_options." + HOOK_CONFIG_KEYS.getOrDefault(pluginName,
                "enable_" + pluginName.toLowerCase());
        if (pluginName.equalsIgnoreCase("GriefPrevention")) {
            return !config.getBoolean(configKey, false);
        }
        return !config.getBoolean(configKey, true);
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent e) {
        Plugin plugin = e.getPlugin();
        PluginHook hook = getActiveHook(plugin);
        if (hook == null) return;
        try {
            hook.onPluginDisable(plugin);
        } catch (Exception e1) {
            this.plugin.logException(new InvalidPluginHookException(e1));
        }
        hook.setPlugin(null);
        activeHooks.remove(hook);
    }

    private PluginHook getActiveHook(Plugin plugin) {
        for (PluginHook hook : activeHooks)
            if (hook.getPlugin() == plugin) return hook;
        return null;
    }

    public boolean isHookActive(Class<? extends PluginHook> hookClass) {
        for (PluginHook hook : activeHooks) {
            if (hook.getClass().equals(hookClass)) {
                return true;
            }
        }
        return false;
    }
}
