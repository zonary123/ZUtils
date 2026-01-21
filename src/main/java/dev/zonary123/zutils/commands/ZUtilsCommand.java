package dev.zonary123.zutils.commands;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jspecify.annotations.NonNull;

/**
 *
 * @author Carlos Varas Alonso - 21/01/2026 13:36
 */
public class ZUtilsCommand extends CommandBase {
  public ZUtilsCommand() {
    super("zutils", "description");
    this.addSubCommand(new TestCommand());
  }

  @Override protected void executeSync(@NonNull CommandContext commandContext) {

  }
}
