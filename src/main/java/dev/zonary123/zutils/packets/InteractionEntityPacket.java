package dev.zonary123.zutils.packets;

import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChain;
import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChains;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.io.adapter.PlayerPacketWatcher;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.util.TargetUtil;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import dev.zonary123.zutils.ZUtils;
import dev.zonary123.zutils.events.ZUtilsEvents;
import dev.zonary123.zutils.events.models.InteractionNPC;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InteractionEntityPacket implements PlayerPacketWatcher {
  private static final Map<UUID, Long> lastInteractionTimes = new ConcurrentHashMap<>();

  @Override
  public void accept(PlayerRef playerRef, Packet packet) {
    if (packet instanceof SyncInteractionChains) {
      var updates = ((SyncInteractionChains) packet).updates;
      for (SyncInteractionChain update : updates) {
        if (update.interactionType == InteractionType.Use) {
          var ref = playerRef.getReference();
          if (ref == null) return;
          UUID worldUuid = playerRef.getWorldUuid();
          if (worldUuid == null) return;
          World world = Universe.get().getWorld(worldUuid);
          if (world == null) return;
          world.execute(() -> {
            Player player = ref.getStore().getComponent(ref, Player.getComponentType());
            if (player == null) return;
            var targetEntityRef = TargetUtil.getTargetEntity(ref, ref.getStore());
            if (targetEntityRef == null) return;
            var npcComponent = NPCEntity.getComponentType();
            if (npcComponent == null) return;
            var store = targetEntityRef.getStore();
            NPCEntity npcEntity = store.getComponent(targetEntityRef, npcComponent);
            if (npcEntity == null) return;
            UUIDComponent uuidComponent = store.getComponent(targetEntityRef, UUIDComponent.getComponentType());
            if (uuidComponent == null) return;
            UUID npcId = uuidComponent.getUuid();
            long currentTime = System.currentTimeMillis();
            long lastInteractionTime = lastInteractionTimes.getOrDefault(npcId, 0L);
            if (currentTime - lastInteractionTime < 1000) return;
            lastInteractionTimes.put(npcId, currentTime);
            ZUtils.ASYNC_CONTEXT.runAsync(() -> {
              ZUtilsEvents.INTERACTION_NPC_EVENT.emit(InteractionNPC.builder()
                .playerRef(playerRef)
                .player(player)
                .npcEntity(npcEntity)
                .npcId(npcId)
                .build());
              return null;
            });
          });
        }
      }
    }

  }
}
