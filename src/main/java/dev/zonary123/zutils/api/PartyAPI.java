package dev.zonary123.zutils.api;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import dev.zonary123.zutils.ZUtils;
import dev.zonary123.zutils.utils.party.Party;
import dev.zonary123.zutils.utils.party.PartyProProvider;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.UUID;

public class PartyAPI {
  private static Party PARTY;

  public static void registerPartyImplementation(Party partyImpl) {
    try {
      partyImpl.getLeader(UUID.randomUUID());
      PARTY = partyImpl;
      ZUtils.getLog().atInfo().log(
        "Registered party implementation: " + partyImpl.getClass().getName()
      );
    } catch (NoClassDefFoundError | NoSuchMethodError | Exception e) {
      ZUtils.getLog().atWarning().withCause(e).log(
        "Failed to register party implementation: " + partyImpl.getClass().getName()
      );
    }
  }

  /**
   * Check if a party plugin is registered.
   *
   * @return True if a party plugin is registered, false otherwise.
   */
  public static boolean isPartyPluginRegistered() {
    return PARTY != null;
  }

  /**
   * Get the leader of the party that the player is in.
   *
   * @param playerRef The PlayerRef of the player.
   * @return The UUID of the party leader, or the player's UUID if the player is not in a party.
   */
  public static UUID getLeader(PlayerRef playerRef) {
    return getLeader(playerRef.getUuid());
  }

  /**
   * Get the leader of the party that the player is in.
   *
   * @param playerUUID The UUID of the player.
   * @return The UUID of the party leader, or the player's UUID if the player is not in a party.
   */
  public static UUID getLeader(UUID playerUUID) {
    if (PARTY == null) return playerUUID;
    UUID leader = PARTY.getLeader(playerUUID);
    if (leader != null) return leader;
    return playerUUID;
  }

  @NonNull
  public static List<UUID> getMembers(PlayerRef playerRef) {
    return getMembers(playerRef.getUuid());
  }

  @NonNull
  public static List<UUID> getMembers(UUID playerUUID) {
    if (PARTY == null) return List.of(playerUUID);
    List<UUID> members = PARTY.getMembers(playerUUID);
    if (members != null) return members;
    return List.of(playerUUID);
  }

  public static void registerPartyProviders() {
    registerPartyImplementation(new PartyProProvider());
  }
}
