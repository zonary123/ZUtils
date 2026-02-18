package dev.zonary123.zutils.packets;

import com.hypixel.hytale.server.core.io.adapter.PacketAdapters;

public class PacketsRegistry {
  public static void register() {
    PacketAdapters.registerInbound(new InteractionEntityPacket());
  }
}
