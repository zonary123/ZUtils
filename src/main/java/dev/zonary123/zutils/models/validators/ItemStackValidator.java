package dev.zonary123.zutils.models.validators;

import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import dev.zonary123.zutils.ZUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NonNull;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Carlos Varas Alonso - 20/01/2026 11:01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemStackValidator {
  private Set<String> itemIds = new HashSet<>(
    Set.of("*")
  );


  /**
   * Check if the given item ID is valid according to the validator's criteria.
   *
   * @param itemId The item ID to validate.
   *
   * @return True if the item ID is valid, false otherwise.
   */
  public boolean isValid(@NonNull String itemId) {
    return ValidatorUtil.match(itemId, itemIds);
  }

  /**
   * Check if the given ItemStack is valid according to the validator's criteria.
   *
   * @param itemStack The ItemStack to validate.
   *
   * @return True if the ItemStack is valid, false otherwise.
   */
  public boolean isValid(@NonNull ItemStack itemStack) {
    try {
      var categories = itemStack.getItem().getCategories();
      if (categories != null) {
        if (ZUtils.getConfig().isDebug()) {
          ZUtils.getLog().atInfo().log(
            "ItemStackValidator: Validating item stack %s with categories %s against item IDs %s",
            itemStack,
            categories,
            itemIds
          );
        }
        for (String category : categories) {
          if (ValidatorUtil.match(category, itemIds)) return true;
        }
      }
      return isValid(itemStack.getItemId());
    } catch (Exception e) {
      if (ZUtils.getConfig().isDebug()) {
        ZUtils.getLog().atWarning().withCause(e).log(
          "ItemStackValidator: Failed to validate item stack %s",
          itemStack
        );
      }
      return false;
    }
  }

  /**
   * Check if the given BlockType is valid according to the validator's criteria.
   *
   * @param blockType The BlockType to validate.
   *
   * @return True if the BlockType is valid, false otherwise.
   */
  public boolean isValid(@NonNull BlockType blockType) {
    Item item = blockType.getItem();
    if (item == null) return false;
    return isValid(item.getId());
  }
}
