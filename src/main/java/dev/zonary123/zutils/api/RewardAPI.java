package dev.zonary123.zutils.api;

import dev.zonary123.zutils.models.rewards.AdvancedRewards;
import dev.zonary123.zutils.models.rewards.Reward;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Carlos Varas Alonso - 25/01/2026 1:33
 */
public class RewardAPI {
  private static final Map<String, AdvancedRewards> TEMPLATE_REWARDS = new ConcurrentHashMap<>();
  private static final Map<String, Reward> TEMPLATE_REWARD = new ConcurrentHashMap<>();

  /**
   * Register an AdvancedRewards template.
   *
   * @param advancedRewards AdvancedRewards object to register as a template.
   */
  public static void registerAdvancedRewardsTemplate(AdvancedRewards advancedRewards) {
    if (TEMPLATE_REWARDS.containsKey(advancedRewards.getId())) {
      throw new IllegalArgumentException("AdvancedRewards with ID '%s' is already registered.".formatted(advancedRewards.getId()));
    }
    TEMPLATE_REWARDS.putIfAbsent(advancedRewards.getId(), advancedRewards);
  }

  /**
   * Register a Reward template.
   *
   * @param reward Reward object to register as a template.
   */
  public static void registerRewardsTemplate(Reward reward) {
    if (TEMPLATE_REWARD.containsKey(reward.getId()))
      throw new IllegalArgumentException("Reward with ID '%s' is already registered.".formatted(reward.getId()));
    TEMPLATE_REWARD.putIfAbsent(reward.getId(), reward);
  }


  /**
   * Give advanced rewards to a player.
   *
   * @param advancedRewards AdvancedRewards object containing the rewards configuration.
   * @param playerUuid      UUID of the player to give the rewards to.
   */
  public static void giveRewards(AdvancedRewards advancedRewards, UUID playerUuid) {
    String id = advancedRewards.getId();
    if (TEMPLATE_REWARDS.containsKey(id)) advancedRewards = TEMPLATE_REWARDS.get(id);
    advancedRewards.giveRewards(playerUuid);
  }


}
