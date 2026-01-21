package dev.zonary123.zutils.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
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
    EntityStatMap stats = store.getComponent(ref, EntityStatMap.getComponentType());
    if (stats == null) {
      context.sendMessage(
        Message.raw(
          "You don't have any stats."
        )
      );
      return;
    }
    context.sendMessage(
      Message.raw(
        "Your stats are: " + stats.toString()
      )
    );

    int healthIdx = DefaultEntityStatTypes.getHealth();
    var value = stats.get(healthIdx);
    if (value == null) {
      context.sendMessage(
        Message.raw(
          "You don't have a health stat."
        )
      );
      return;
    }
    float missing = value.getMax() - value.get();
    context.sendMessage(
      Message.raw(
        "Your missing health is: " + missing
      )
    );
    stats.addStatValue(healthIdx, missing);
  }
}
