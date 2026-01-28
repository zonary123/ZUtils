package dev.zonary123.zutils.api;

import dev.zonary123.zutils.models.rewards.AdvancedRewards;

import java.util.UUID;

/**
 *
 * @author Carlos Varas Alonso - 25/01/2026 1:33
 */
public class RewardApi {

  /**
   * Give advanced rewards to a player.
   *
   * @param advancedRewards AdvancedRewards object containing the rewards configuration.
   * @param playerUuid      UUID of the player to give the rewards to.
   */
  public static void giveRewards(AdvancedRewards advancedRewards, UUID playerUuid) {
    advancedRewards.giveRewards(playerUuid);
  }
}
