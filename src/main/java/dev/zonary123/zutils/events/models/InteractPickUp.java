package dev.zonary123.zutils.events.models;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author Carlos Varas Alonso - 20/01/2026 14:42
 */
@Data
@AllArgsConstructor
public class InteractPickUp {
  private Player player;
  private PlayerRef playerRef;
  private ItemStack itemStack;


}
