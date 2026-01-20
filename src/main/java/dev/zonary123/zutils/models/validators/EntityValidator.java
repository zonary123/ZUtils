package dev.zonary123.zutils.models.validators;

import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Carlos Varas Alonso - 20/01/2026 11:01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntityValidator {
  private Set<String> entityIds = new HashSet<>(
    Set.of("*")
  );

  public boolean isValid(String itemId) {
    if (itemId == null) return false;
    return entityIds.isEmpty() || entityIds.contains(itemId) || entityIds.contains("*");
  }

  public boolean isValid(ItemStack itemStack) {
    if (itemStack == null) return false;
    return isValid(itemStack.getItemId());
  }

  public boolean isValid(BlockType blockType) {
    if (blockType == null) return false;
    Item item = blockType.getItem();
    if (item == null) return false;
    return isValid(item.getId());
  }
}
