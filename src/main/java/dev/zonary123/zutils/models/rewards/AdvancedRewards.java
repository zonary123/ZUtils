package dev.zonary123.zutils.models.rewards;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.zonary123.zutils.ZUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Carlos Varas Alonso
 * 25/01/2026
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdvancedRewards {
  private String id = "";
  private boolean giveAll = false;

  /**
   * permission -> amount
   */
  private Map<String, Integer> permissionPerAmount = Map.of(
    "", 1,
    "group.vip", 2
  );

  private List<Reward> rewards = List.of(
    new Reward("item:Soil_Dirt", 1.0),
    new Reward("money:ZEconomy:coins:1:1 Coin rewards", 1.0),
    new Reward("command:give %player% Soil_Dirt 1", 1.0)
  );

  /* ------------------------------------------------------------ */
  /* Public API                                                   */
  /* ------------------------------------------------------------ */

  public void giveRewards(UUID playerUuid) {
    ZUtils.ASYNC_CONTEXT
      .supply(() -> getOnlinePlayer(playerUuid))
      .thenAccept(data -> {
        if (data == null) {
          giveDisconnectedRewards(playerUuid);
          return;
        }

        int amount = getAmountForPlayer(playerUuid);

        if (giveAll) {
          giveAllRewards(data, playerUuid);
        } else {
          giveWeightedRewards(data, playerUuid, amount);
        }
      });
  }

  /* ------------------------------------------------------------ */
  /* Reward logic                                                 */
  /* ------------------------------------------------------------ */

  private void giveAllRewards(DataPlayer data, UUID playerUuid) {
    for (Reward reward : rewards) {
      if (reward == null) continue;
      reward.giveReward(data, playerUuid);
    }
  }

  private void giveWeightedRewards(DataPlayer data, UUID playerUuid, int amount) {
    List<Reward> validRewards = new ArrayList<>();
    for (Reward reward : rewards) {
      if (reward == null) continue;
      if (reward.getWeight() > 0.0) {
        validRewards.add(reward);
      } else {
        reward.giveReward(data, playerUuid);
      }
    }
    double totalWeight = getTotalWeight(validRewards);
    for (int i = 0; i < amount; i++) {
      Reward reward = getRandomReward(validRewards, totalWeight);
      if (reward != null) {
        reward.giveReward(data, playerUuid);
      }
    }
  }

  private Reward getRandomReward(List<Reward> rewards, double totalWeight) {
    double random = ThreadLocalRandom.current().nextDouble(totalWeight);
    double current = 0.0;

    for (Reward reward : rewards) {
      current += reward.getWeight();
      if (random <= current) return reward;
    }
    return null;
  }

  /* ------------------------------------------------------------ */
  /* Player / permissions                                        */
  /* ------------------------------------------------------------ */

  @Data
  public static class DataPlayer {
    public final Player player;
    public final PlayerRef playerRef;

    public DataPlayer(Player player, PlayerRef playerRef) {
      this.player = player;
      this.playerRef = playerRef;
    }
  }

  private DataPlayer getOnlinePlayer(UUID uuid) {
    PlayerRef playerRef = Universe.get().getPlayer(uuid);
    if (playerRef == null) return null;

    Ref<EntityStore> ref = playerRef.getReference();
    if (ref == null) return null;

    Store<EntityStore> store = ref.getStore();
    Player player = store.getComponent(ref, Player.getComponentType());
    return new DataPlayer(player, playerRef);
  }

  private int getAmountForPlayer(UUID playerUuid) {
    int amount = 1;

    for (Map.Entry<String, Integer> entry : permissionPerAmount.entrySet()) {
      if (entry.getValue() <= amount) continue;
      if (entry.getKey().isEmpty() || PermissionsModule.get().hasPermission(playerUuid, entry.getKey())) {
        amount = entry.getValue();
      }
    }

    return amount;
  }

  /* ------------------------------------------------------------ */
  /* Offline rewards                                             */
  /* ------------------------------------------------------------ */

  public void giveDisconnectedRewards(UUID playerUuid) {
    for (Reward reward : rewards) {
      reward.giveDisconnectedReward(playerUuid);
    }
  }

  /* ------------------------------------------------------------ */
  /* Utils                                                        */
  /* ------------------------------------------------------------ */

  public double getTotalWeight(List<Reward> validRewards) {
    double total = 0.0;
    for (Reward reward : validRewards) {
      if (reward != null) {
        total += reward.getWeight();
      }
    }
    return total;
  }
}
