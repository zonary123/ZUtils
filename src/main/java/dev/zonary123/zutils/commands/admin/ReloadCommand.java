package dev.zonary123.zutils.commands.admin;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import dev.zonary123.zutils.ZUtils;
import org.jspecify.annotations.NonNull;

public class ReloadCommand extends CommandBase {
  public ReloadCommand() {
    super("reload", "Reloads the plugin configuration");
  }

  @Override
  protected void executeSync(@NonNull CommandContext context) {
    ZUtils.get().reload();
    context.sendMessage(
      Message.raw(
        "&aZUtils configuration reloaded successfully!"
      )
    );
  }
}
