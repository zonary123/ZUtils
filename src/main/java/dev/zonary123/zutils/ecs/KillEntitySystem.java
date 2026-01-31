package dev.zonary123.zutils.ecs;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import dev.zonary123.zutils.ZUtils;
import dev.zonary123.zutils.events.ZUtilsEvents;
import dev.zonary123.zutils.events.models.KillEntity;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

/**
 *
 * @author Carlos Varas Alonso - 20/01/2026 16:07
 */
public class KillEntitySystem extends DeathSystems.OnDeathSystem {


  @Override
  public void onComponentAdded(
    @NonNull Ref<EntityStore> ref,
    @NonNull DeathComponent deathComponent,
    @NonNull Store<EntityStore> store,
    @NonNull CommandBuffer<EntityStore> commandBuffer
  ) {
    if (ZUtilsEvents.DAMAGE_EVENT.isEmpty() && ZUtilsEvents.KILL_ENTITY_EVENT.isEmpty()) return;
    var component = NPCEntity.getComponentType();
    if (component == null) return;
    NPCEntity npcEntity = store.getComponent(ref, NPCEntity.getComponentType());
    if (npcEntity == null) return;
    var deathInfo = deathComponent.getDeathInfo();
    if (deathInfo == null) return;
    var source = deathInfo.getSource();
    if (!(source instanceof Damage.EntitySource entitySource)) return;
    var entityRef = entitySource.getRef();
    Player attackerPlayer = store.getComponent(entityRef, Player.getComponentType());
    PlayerRef attackerPlayerRef = store.getComponent(entityRef, PlayerRef.getComponentType());
    if (attackerPlayer == null || attackerPlayerRef == null) return;

    UUIDComponent targetUuidComponent = store.getComponent(ref, UUIDComponent.getComponentType());
    if (targetUuidComponent == null) return;

    var contributors = DamageSystem.DamageTracker.getContributors(targetUuidComponent.getUuid());
    if (contributors == null) return;
    var entries = contributors.entrySet();
    for (Map.Entry<UUID, Damage> entry : entries) {
      var attackerUuid = entry.getKey();
      PlayerRef attackerRef = Universe.get().getPlayer(attackerUuid);
      if (attackerRef == null) continue;
      var reference = attackerRef.getReference();
      if (reference == null) continue;
      var referenceStore = reference.getStore();
      Player attacker = referenceStore.getComponent(reference, Player.getComponentType());
      if (attacker == null) continue;
      if (ZUtilsEvents.KILL_ENTITY_EVENT.isEmpty()) return;
      ZUtils.ASYNC_CONTEXT.runAsync(() -> {
        ZUtilsEvents.KILL_ENTITY_EVENT.emit(
          new KillEntity(
            attacker,
            attackerRef,
            npcEntity,
            npcEntity.getNPCTypeId(),
            entry.getValue()
          )
        );
        return null;
      });
      DamageSystem.DamageTracker.clear(targetUuidComponent.getUuid());
    }
  }

  @Nullable
  @Override
  public Query<EntityStore> getQuery() {
    return NPCEntity.getComponentType();
  }

}
