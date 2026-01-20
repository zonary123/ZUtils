package dev.zonary123.zutils.models.validators;

import com.hypixel.hytale.server.npc.entities.NPCEntity;
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

  public boolean isValid(String entityId) {
    if (entityId == null) return false;
    return entityIds.isEmpty() || entityIds.contains(entityId) || entityIds.contains("*");
  }

  public boolean isValid(NPCEntity npcEntity) {
    if (npcEntity == null) return false;
    return isValid(npcEntity.getNPCTypeId());
  }


}
