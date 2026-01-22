package dev.zonary123.zutils.ecs;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import dev.zonary123.zutils.ZUtils;
import dev.zonary123.zutils.events.ZUtilsEvents;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

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
    var component = NPCEntity.getComponentType();
    if (component == null) return;
    NPCEntity npcEntity = store.getComponent(ref, NPCEntity.getComponentType());
    if (npcEntity == null) return;
    var deathInfo = deathComponent.getDeathInfo();
    if (deathInfo == null) {
      if (ZUtils.getConfig().isDebug()) {
        ZUtils.getLog().atInfo().log(
          "NPC Entity %s died but no death info available",
          npcEntity.getNPCTypeId()
        );
      }
      return;
    }
    var source = deathInfo.getSource();
    if (!(source instanceof Damage.EntitySource entitySource)) return;
    var entityRef = entitySource.getRef();
    Player attackerPlayer = store.getComponent(entityRef, Player.getComponentType());
    PlayerRef attackerPlayerRef = store.getComponent(entityRef, PlayerRef.getComponentType());
    if (attackerPlayer == null || attackerPlayerRef == null) {
      if (ZUtils.getConfig().isDebug()) {
        ZUtils.getLog().atInfo().log(
          "NPC Entity %s killed but attacker is not a player",
          npcEntity.getNPCTypeId()
        );
      }
      return;
    }
    ZUtils.ASYNC_CONTEXT.runAsync(() -> {
      if (ZUtils.getConfig().isDebug()) {
        ZUtils.getLog().atInfo().log(
          "NPC Entity %s killed by Player %s",
          npcEntity.getNPCTypeId(),
          attackerPlayerRef.getUsername()
        );
      }
      ZUtilsEvents.KILL_ENTITY_EVENT.emit(
        new dev.zonary123.zutils.events.models.KillEntity(
          attackerPlayer,
          attackerPlayerRef,
          npcEntity,
          npcEntity.getNPCTypeId()
        )
      );

      return null;
    });
  }

  @Nullable
  @Override
  public Query<EntityStore> getQuery() {
    return NPCEntity.getComponentType();
  }

}
