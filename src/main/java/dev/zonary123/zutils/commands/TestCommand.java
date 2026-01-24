package dev.zonary123.zutils.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jspecify.annotations.NonNull;

/**
 *
 * @author Carlos Varas Alonso - 18/01/2026 20:27
 */
public class TestCommand extends AbstractPlayerCommand {


  public TestCommand() {
    super("Test", "description");
  }

  @Override
  protected void execute(
    @NonNull CommandContext context,
    @NonNull Store<EntityStore> store,
    @NonNull Ref<EntityStore> ref,
    @NonNull PlayerRef playerRef,
    @NonNull World world
  ) {
    world.execute(() -> {
      Player player = store.getComponent(ref, Player.getComponentType());
      if (player == null) {
        context.sendMessage(
          Message.raw(
            "Player component not found."
          )
        );
        return;
      }

      NetworkId id = store.getComponent(ref, NetworkId.getComponentType());
      if (id == null) {
        context.sendMessage(
          Message.raw(
            "NetworkId component not found."
          )
        );
        return;
      }
      context.sendMessage(
        Message.raw(
          "EntityId: " + id.getId()
        )
      );
    });
  }
}
