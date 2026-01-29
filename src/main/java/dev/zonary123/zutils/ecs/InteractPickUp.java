package dev.zonary123.zutils.ecs;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.InteractivelyPickupItemEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.zonary123.zutils.ZUtils;
import dev.zonary123.zutils.events.ZUtilsEvents;
import org.jspecify.annotations.NonNull;

/**
 *
 * @author Carlos Varas Alonso - 20/01/2026 14:39
 */
public class InteractPickUp extends EntityEventSystem<EntityStore, InteractivelyPickupItemEvent> {
  public InteractPickUp() {
    super(InteractivelyPickupItemEvent.class);
  }

  @Override
  public void handle(int index, @NonNull ArchetypeChunk<EntityStore> archetypeChunk, @NonNull Store<EntityStore> store,
                     @NonNull CommandBuffer<EntityStore> commandBuffer, @NonNull InteractivelyPickupItemEvent evt) {
    var ref = archetypeChunk.getReferenceTo(index);
    var playerRef = store.getComponent(ref, PlayerRef.getComponentType());
    var player = store.getComponent(ref, Player.getComponentType());
    if (playerRef == null || player == null) return;
    var itemStack = evt.getItemStack();
    ZUtils.ASYNC_CONTEXT.runAsync(() -> {
      Integer placed = BlockPlacedEvent.BLOCK_PLACE.get(playerRef.getUuid()).getOrDefault(itemStack.getItem(), 0);
      if (placed != null && placed > 0) {
        if (ZUtils.getConfig().isDebug()) {
          ZUtils.getLog().atInfo().log(
            "Player %s picked up item %s x%d (ignored due to recent placement)",
            playerRef.getUsername(),
            itemStack.getItemId(),
            itemStack.getQuantity()
          );
        }
        BlockPlacedEvent.BLOCK_PLACE.get(playerRef.getUuid()).merge(itemStack.getItem(), -1, Integer::sum);
        return null;
      }
      if (ZUtils.getConfig().isDebug()) {
        ZUtils.getLog().atInfo().log(
          "Player %s picked up item %s x%d",
          playerRef.getUsername(),
          itemStack.getItemId(),
          itemStack.getQuantity()
        );
      }
      ZUtilsEvents.INTERACT_PICKUP_EVENT.emit(new dev.zonary123.zutils.events.models.InteractPickUp(
        player,
        playerRef,
        itemStack
      ));
      return null;
    });
  }

  @Override
  public Query<EntityStore> getQuery() {
    return PlayerRef.getComponentType();
  }
}
