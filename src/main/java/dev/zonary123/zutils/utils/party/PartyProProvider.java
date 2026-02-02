package dev.zonary123.zutils.utils.party;

import me.tsumori.partypro.api.PartyProAPI;
import me.tsumori.partypro.api.PartySnapshot;

import java.util.List;
import java.util.UUID;

public class PartyProProvider extends Party {

  private PartySnapshot getPartyByPlayer(UUID playerUUID) {
    return PartyProAPI.getInstance().getPartyByPlayer(playerUUID);
  }

  @Override
  public UUID getLeader(UUID playerUUID) {
    var party = getPartyByPlayer(playerUUID);
    if (party == null) return playerUUID;
    return party.leader();
  }

  @Override
  public List<UUID> getMembers(UUID playerUUID) {
    var party = getPartyByPlayer(playerUUID);
    if (party == null) return List.of(playerUUID);
    return party.getAllMembers();
  }
}
