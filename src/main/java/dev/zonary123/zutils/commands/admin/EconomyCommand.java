package dev.zonary123.zutils.commands.admin;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import dev.zonary123.zutils.api.EconomyApi;
import dev.zonary123.zutils.utils.economy.Economy;
import org.jspecify.annotations.NonNull;

/**
 *
 * @author Carlos Varas Alonso - 24/01/2026 23:33
 */
public class EconomyCommand extends CommandBase {
  public EconomyCommand() {
    super("economies", "Show economy information");
  }

  @Override protected void executeSync(@NonNull CommandContext commandContext) {
    var economies = EconomyApi.getEconomies();
    StringBuilder message = new StringBuilder("Available Economies:\n");
    for (Economy economy : economies.values()) {
      message.append("- ").append(economy.getEconomyId()).append("\n");
    }
    commandContext.sendMessage(
      Message.raw(
        message.toString()
      )
    );
  }
}
