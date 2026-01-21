package dev.zonary123.zutils.models.validators;

import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.inventory.ItemStack;
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

  public boolean isValid(@NonNull String itemId) {
    return ValidatorUtil.match(itemId, itemIds);
  }

  public boolean isValid(@NonNull ItemStack itemStack) {
    return isValid(itemStack.getItemId());
  }

  public boolean isValid(@NonNull BlockType blockType) {
    Item item = blockType.getItem();
    if (item == null) return false;
    return isValid(item.getId());
  }
}
