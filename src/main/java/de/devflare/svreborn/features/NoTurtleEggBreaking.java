package de.devflare.svreborn.features;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import de.devflare.svreborn.SuperVanishReborn;

public class NoTurtleEggBreaking extends Feature {

    public NoTurtleEggBreaking(SuperVanishReborn plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (!plugin.getVanishStateMgr().isVanished(p.getUniqueId())) return;
        if (!e.getAction().equals(Action.PHYSICAL) || e.getClickedBlock() == null
                || !e.getClickedBlock().getType().toString().equals("TURTLE_EGG"))
            return;
        e.setCancelled(true);
    }

    @Override
    public boolean isActive() {
        return plugin.getSettings().getBoolean("InvisibilityFeatures.PreventTurtleEggBreaking", true);
    }
}
