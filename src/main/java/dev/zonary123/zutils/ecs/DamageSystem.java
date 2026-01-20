package dev.zonary123.zutils.ecs;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import dev.zonary123.zutils.ZUtils;
import dev.zonary123.zutils.events.ZUtilsEvents;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

/**
 *
 * @author Carlos Varas Alonso - 20/01/2026 16:07
 */
public class DamageSystem extends DamageEventSystem {
  public void handle(int index, ArchetypeChunk<EntityStore> archetypeChunk, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer, Damage damage) {
    var targetRef = archetypeChunk.getReferenceTo(index);
    if (!targetRef.isValid()) return;
    Damage.Source source = damage.getSource();
    if (source instanceof Damage.EntitySource) {
      Damage.EntitySource entitySource = (Damage.EntitySource) source;
      var attackerRef = entitySource.getRef();
      if (!attackerRef.isValid()) {
        return;
      }
      Player attackerPlayer = store.getComponent(attackerRef, Player.getComponentType());
      PlayerRef attackerPlayerRef = store.getComponent(attackerRef, PlayerRef.getComponentType());
      if (attackerPlayer == null) {
        return;
      }
      UUIDComponent attackerUuidComponent = store.getComponent(attackerRef, UUIDComponent.getComponentType());
      if (attackerUuidComponent == null) {
        return;
      }
      UUID attackerUuid = attackerUuidComponent.getUuid();
      UUIDComponent targetUuidComponent = store.getComponent(targetRef, UUIDComponent.getComponentType());
      if (targetUuidComponent == null) {
        return;
      }
      UUID targetUuid = targetUuidComponent.getUuid();
      NPCEntity npcEntity = store.getComponent(targetRef, NPCEntity.getComponentType());
      if (npcEntity == null) return;
      String targetNPCId = npcEntity.getNPCTypeId();
      if (ZUtils.getConfig().isDebug()) {
        ZUtils.getLog().atInfo().log(
          "Entity %s (UUID: %s) attacked by Player %s (UUID: %s)",
          targetNPCId,
          targetUuid,
          attackerPlayer.getDisplayName(),
          attackerUuid
        );
      }

      ZUtils.ASYNC_CONTEXT.runAsync(() -> {
        // Send damage event
        ZUtilsEvents.DAMAGE_EVENT.emit(
          new dev.zonary123.zutils.events.models.Damage(
            attackerPlayer,
            attackerPlayerRef,
            npcEntity,
            targetNPCId
          )
        );
        return null;
      });
    }
  }

  @Nullable
  @Override
  public Query<EntityStore> getQuery() {
    return Query.any();
  }
}
