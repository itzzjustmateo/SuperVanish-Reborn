/*
 * Copyright © 2015, Leon Mangler and the SuperVanish contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.devflare.svreborn.visibility;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

import de.devflare.svreborn.SuperVanishReborn;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class ActionBarMgr {

    private final SuperVanishReborn plugin;
    private final List<Player> actionBars = new ArrayList<>();

    public ActionBarMgr(SuperVanishReborn plugin) {
        this.plugin = plugin;
        startTask();
    }

    private void startTask() {
        new BukkitRunnable() {

            @Override
            public void run() {
                for (Player p : actionBars) {
                    try {
                        sendActionBar(p, plugin.replacePlaceholders(plugin.getMessage("ActionBarMessage"), p));
                    } catch (Exception | NoSuchMethodError | NoClassDefFoundError e) {
                        cancel();
                        plugin.logException(e);
                        plugin.getLogger().warning("IMPORTANT: Please make sure that you are using the latest " +
                                "dev-build of ProtocolLib and that your server is up-to-date! This error likely " +
                                "happened inside of ProtocolLib code which is out of SuperVanish's control. It's part "
                                +
                                "of an optional feature module and can be removed safely by disabling " +
                                "DisplayActionBar in the config file. Please report this " +
                                "error if you can reproduce it on an up-to-date server with only latest " +
                                "ProtocolLib and latest SV installed.");
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 2 * 20);
    }

    private void sendActionBar(Player p, String bar) {
        try {
            p.sendActionBar(MiniMessage.miniMessage().deserialize(bar));
        } catch (Exception | NoSuchMethodError | NoClassDefFoundError er) {
            WrappedChatComponent msg = WrappedChatComponent
                    .fromJson(net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson()
                            .serialize(MiniMessage.miniMessage().deserialize(bar)));
            PacketContainer chatMsg = new PacketContainer(PacketType.Play.Server.CHAT);
            chatMsg.getChatComponents().write(0, msg);
            if (plugin.getVersionUtil().isOneDotXOrHigher(12)) {
                try {
                    chatMsg.getChatTypes().write(0, EnumWrappers.ChatType.GAME_INFO);
                } catch (NoSuchMethodError | Exception e) {
                    // Ignore or fallback
                }
            } else {
                chatMsg.getBytes().write(0, (byte) 2);
            }
            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(p, chatMsg);
            } catch (Exception e) {
                plugin.logException(e);
            }
        }
    }

    public void addActionBar(Player p) {
        actionBars.add(p);
    }

    public void removeActionBar(Player p) {
        actionBars.remove(p);
    }
}
