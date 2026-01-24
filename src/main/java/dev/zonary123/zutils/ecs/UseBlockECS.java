package dev.zonary123.zutils.ecs;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.event.events.ecs.UseBlockEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.zonary123.zutils.ZUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 *
 * @author Carlos Varas Alonso - 23/01/2026 13:50
 */
public class UseBlockECS extends EntityEventSystem<EntityStore, UseBlockEvent.Post> {

  public UseBlockECS() {
    super(UseBlockEvent.Post.class);
  }

  @Override
  public void handle(
    int index,
    @NonNull ArchetypeChunk<EntityStore> archetypeChunk,
    @NonNull Store<EntityStore> store,
    @NonNull CommandBuffer<EntityStore> commandBuffer,
    UseBlockEvent.@NonNull Post evt
  ) {
    var blockType = evt.getBlockType();
    var ref = archetypeChunk.getReferenceTo(index);
    var playerRef = store.getComponent(ref, PlayerRef.getComponentType());
    if (playerRef == null) {
      if (ZUtils.getConfig().isDebug()) {
        ZUtils.getLog().atWarning().log(
          "UseBlockECS: No PlayerRef found for entity %s when using block of type %s at position %s",
          ref,
          blockType,
          evt.getTargetBlock()
        );
      }
      return;
    }
    if (ZUtils.getConfig().isDebug()) {
      ZUtils.getLog().atInfo().log(
        "UseBlockECS: Player %s used block of type %s at position %s",
        playerRef.getUsername(),
        blockType,
        evt.getTargetBlock()
      );
    }
  }

  @Override public @Nullable Query<EntityStore> getQuery() {
    return PlayerRef.getComponentType();
  }
}
