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
