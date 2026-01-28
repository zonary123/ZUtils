package dev.zonary123.zutils.ecs;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.event.events.ecs.DropItemEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.zonary123.zutils.ZUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class PlayerThrowItem extends EntityEventSystem<EntityStore, DropItemEvent.Drop> {
  public PlayerThrowItem() {
    super(DropItemEvent.Drop.class);
  }

  @Override
  public void handle(
    int index,
    @NonNull ArchetypeChunk<EntityStore> archetypeChunk,
    @NonNull Store<EntityStore> store,
    @NonNull CommandBuffer<EntityStore> commandBuffer,
    DropItemEvent.@NonNull Drop evt) {
    ItemStack itemStack = evt.getItemStack();
    Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
    PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
    if (playerRef == null) return;

    if (ZUtils.getConfig().isDebug()) {
      ZUtils.getLog().atInfo().log(
        "Player %s has thrown item %s x%d",
        playerRef.getUsername(),
        itemStack.getItemId(),
        itemStack.getQuantity()
      );
    }
  }

  @Override
  public @Nullable Query<EntityStore> getQuery() {
    return PlayerRef.getComponentType();
  }


}
