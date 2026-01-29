package dev.zonary123.zutils.models.rewards;

import com.hypixel.hytale.protocol.packets.interface_.NotificationStyle;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.console.ConsoleSender;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import dev.zonary123.zutils.api.EconomyApi;
import dev.zonary123.zutils.utils.economy.Economy;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author Carlos Varas Alonso - 25/01/2026 0:51
 */
@Data
public class Reward {
  private final String reward;
  private final double weight;

  public Reward() {
    this.reward = "item:1:Soil_Dirt";
    this.weight = 1.0;
  }

  /**
   * Constructor for Reward.
   *
   * @param reward Reward string.
   * @param weight Weight of the reward.
   */
  public Reward(String reward, double weight) {
    this.reward = reward;
    this.weight = weight;
  }

  private String getRewardType() {
    return reward.split(":")[0];
  }

  /* ------------------------------------------------------------ */
  /* Public API                                                   */
  /* ------------------------------------------------------------ */

  /**
   * Give reward to player.
   *
   * @param data       DataPlayer object containing player reference.
   * @param playerUuid UUID of the player.
   */
  public void giveReward(AdvancedRewards.DataPlayer data, UUID playerUuid) {
    switch (getRewardType()) {
      case "item" -> giveItemReward(data);
      case "money" -> giveMoneyReward(playerUuid);
      case "command" -> giveCommandReward(data);
      default -> throw new UnsupportedOperationException("Unsupported reward type: " + getRewardType());
    }
  }

  /**
   * Give item reward to player.
   * <p>
   * Format: item:<amount>:<itemId>
   * Format: item:<amount>-<amount>:<itemId>
   *
   * @param player Player to give the reward to.
   * @see ItemStack
   */
  public void giveItemReward(AdvancedRewards.DataPlayer data) {
    String[] parts = reward.split(":");
    String amountStr = parts[1];
    String itemId = parts[2];
    int amount;
    if (amountStr.contains("-")) {
      String[] range = amountStr.split("-");
      int min = Integer.parseInt(range[0]);
      int max = Integer.parseInt(range[1]);
      amount = ThreadLocalRandom.current().nextInt(min, max + 1);
    } else {
      amount = Integer.parseInt(amountStr);
    }
    ItemStack itemStack = new ItemStack(itemId, amount);
    Player player = data.getPlayer();
    if (player == null) {
      giveDisconnectedReward(data.getPlayerRef().getUuid());
      return;
    }
    player.getInventory().getCombinedEverything().addItemStack(itemStack);
    NotificationUtil.sendNotification(
      data.getPlayerRef().getPacketHandler(),
      Message.empty(),
      Message.empty(),
      itemStack.toPacket(),
      NotificationStyle.Default
    );
  }

  /**
   * Give command reward to player.
   * <p>
   * Format: command:<command>
   * </p>
   *
   * @param dataPlayer DataPlayer object containing player reference.
   */
  private void giveCommandReward(AdvancedRewards.DataPlayer dataPlayer) {
    PlayerRef playerRef = dataPlayer.getPlayerRef();
    String command = reward.split(":", 2)[1];
    command = command.replace("%player%", playerRef.getUsername());
    HytaleServer.get().getCommandManager().handleCommand(ConsoleSender.INSTANCE, command);
  }

  /**
   * Give money reward to player.
   * <p>
   * Format: money:<economyId>:<currencyId>:<amount>
   * Format: money:<economyId>:<currencyId>:<amount>-<amount>
   * Format: money:<economyId>:<currencyId>:<amount>:<reason>
   * Format: money:<economyId>:<currencyId>:<amount>-<amount>:<reason>
   * </p>
   *
   * @param playerUuid UUID of the player.
   */
  private void giveMoneyReward(UUID playerUuid) {
    String[] parts = reward.split(":");
    String economyId = parts[1];
    String currencyId = parts[2];
    String amountStr = parts[3];
    String reason = parts.length >= 5 ? parts[4] : "Reward";
    double amount;
    if (amountStr.contains("-")) {
      String[] range = amountStr.split("-");
      double min = Double.parseDouble(range[0]);
      double max = Double.parseDouble(range[1]);
      amount = ThreadLocalRandom.current().nextDouble(min, max);
    } else {
      amount = Double.parseDouble(amountStr);
    }
    Economy economy = EconomyApi.getEconomy(economyId);
    if (economy != null) {
      economy.deposit(playerUuid, currencyId, BigDecimal.valueOf(amount), reason);
    }
  }


  /* ------------------------------------------------------------ */
  /* Disconnected rewards                                        */
  /* ------------------------------------------------------------ */

  public void giveDisconnectedReward(UUID playerUuid) {
    switch (getRewardType()) {
      case "money" -> giveMoneyReward(playerUuid);
      default -> {
        throw new UnsupportedOperationException("Disconnected rewards only support money rewards.");
      }
    }
  }
}
