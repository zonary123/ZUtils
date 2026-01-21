package dev.zonary123.zutils.models.validators;

import com.hypixel.hytale.server.npc.entities.NPCEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

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

  public boolean isValid(@NonNull String entityId) {
    return ValidatorUtil.match(entityId, entityIds);
  }

  public boolean isValid(@NonNull NPCEntity npcEntity) {
    return isValid(npcEntity.getNPCTypeId());
  }


}
