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

/**
 *
 * @author Carlos Varas Alonso - 20/01/2026 14:39
 */
public class UseBlockPickUp extends EntityEventSystem<EntityStore, UseBlockEvent.Post> {
  public UseBlockPickUp() {
    super(UseBlockEvent.Post.class);
  }


  @Override
  public void handle(int i, @NonNull ArchetypeChunk<EntityStore> archetypeChunk, @NonNull Store<EntityStore> store, @NonNull CommandBuffer<EntityStore> commandBuffer, UseBlockEvent.@NonNull Post post) {
    if (ZUtils.getConfig().isDebug()) {
      ZUtils.getLog().atInfo().log("UseBlockPickUp Event Triggered");
    }
  }

  @Override
  public Query<EntityStore> getQuery() {
    return PlayerRef.getComponentType();
  }

}
