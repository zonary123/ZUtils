package dev.zonary123.zutils.ecs;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.CraftRecipeEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.zonary123.zutils.ZUtils;
import dev.zonary123.zutils.events.ZUtilsEvents;
import dev.zonary123.zutils.events.models.Craft;
import org.jspecify.annotations.NonNull;

public final class CraftEvent
  extends EntityEventSystem<EntityStore, CraftRecipeEvent.Post> {

  public CraftEvent() {
    super(CraftRecipeEvent.Post.class);
  }


  @Override
  public void handle(int index,
                     @NonNull ArchetypeChunk<EntityStore> archetypeChunk,
                     @NonNull Store<EntityStore> store,
                     @NonNull CommandBuffer<EntityStore> commandBuffer,
                     CraftRecipeEvent.Post evt) {
    var ref = archetypeChunk.getReferenceTo(index);
    var playerRef = store.getComponent(ref, PlayerRef.getComponentType());
    var player = store.getComponent(ref, Player.getComponentType());
    if (playerRef == null || player == null) return;
    var recipe = evt.getCraftedRecipe();
    var quantity = evt.getQuantity();
    var itemStack = recipe.getPrimaryOutput().toItemStack();
    if (itemStack == null) return;
    ZUtils.ASYNC_CONTEXT.runAsync(() -> {
      if (ZUtils.getConfig().isDebug()) {
        ZUtils.getLog().atInfo().log(
          "Player %s crafted item %s x%d using recipe %s",
          playerRef.getUsername(),
          itemStack.getItemId(),
          quantity,
          recipe.getId()
        );
      }

      ZUtilsEvents.CRAFT_EVENT.emit(
        new Craft(
          player,
          playerRef,
          itemStack,
          quantity
        )
      );
      return null;
    });
  }

  @Override public Query<EntityStore> getQuery() {
    return Query.any();
  }
}
