package dev.zonary123.zutils.api;

import dev.zonary123.zutils.models.EconomySelector;
import dev.zonary123.zutils.utils.economy.Economy;
import dev.zonary123.zutils.utils.economy.ZEconomyProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central API for economy management.
 * <p>
 * This class provides a static access point to interact with different economy
 * implementations and currencies.
 * </p>
 *
 * <p>
 * Supports:
 * <ul>
 *   <li>Multiple economy providers</li>
 *   <li>Multiple currencies per economy</li>
 *   <li>Thread-safe economy registration</li>
 * </ul>
 * </p>
 * <p>
 * This class is thread-safe.
 *
 * @author Carlos Varas Alonso
 * @since 18/01/2026
 */
public final class EconomyApi {
  private static final Map<String, Economy> ECONOMIES = new ConcurrentHashMap<>();

  static {
    registerEconomy(new ZEconomyProvider("ZEconomy"));
  }

  private EconomyApi() {
    // Utility class
  }

  /* -------------------------------------------------------------------------- */
  /* Registry                                                                    */
  /* -------------------------------------------------------------------------- */

  /**
   * Registers a new economy provider.
   *
   * @param economy economy implementation
   *
   * @throws IllegalStateException if an economy with the same ID is already registered
   */
  public static void registerEconomy(
    @Nonnull Economy economy
  ) {
    String economyId = economy.getEconomyId();
    if (ECONOMIES.putIfAbsent(economyId, economy) != null)
      throw new IllegalStateException("Economy already registered: " + economyId);
  }

  /**
   * Retrieves a registered economy provider.
   *
   * @param economyId economy identifier
   *
   * @return the economy provider, or {@code null} if not found
   */
  @Nullable
  public static Economy getEconomy(@Nonnull String economyId) {
    return ECONOMIES.get(economyId);
  }

  /* -------------------------------------------------------------------------- */
  /* Balance                                                                     */
  /* -------------------------------------------------------------------------- */

  /**
   * Gets the balance of a player.
   *
   * @param playerId   player UUID
   * @param economyId  economy identifier
   * @param currencyId currency identifier
   *
   * @return current player balance
   *
   * @throws IllegalArgumentException if the economy does not exist
   */
  @Nonnull
  public static BigDecimal getBalance(
    @Nonnull UUID playerId,
    @Nonnull String economyId,
    @Nonnull String currencyId
  ) {
    return getBalance(playerId, new EconomySelector(economyId, currencyId));
  }

  /**
   * Gets the balance of a player using an {@link EconomySelector}.
   *
   * @param playerId player UUID
   * @param selector economy and currency selector
   *
   * @return current player balance
   *
   * @throws IllegalArgumentException if the economy does not exist
   */
  @Nonnull
  public static BigDecimal getBalance(
    @Nonnull UUID playerId,
    @Nonnull EconomySelector selector
  ) {
    return resolveEconomy(selector).getBalance(playerId, selector.getCurrency());
  }

  /* -------------------------------------------------------------------------- */
  /* Set Balance                                                                */
  /* -------------------------------------------------------------------------- */

  /**
   * Sets the balance of a player.
   *
   * @param playerId   player UUID
   * @param economyId  economy identifier
   * @param currencyId currency identifier
   * @param amount     new balance amount
   * @param reason     transaction reason
   *
   * @return {@code true} if the balance was set successfully
   */
  public static boolean setBalance(
    @Nonnull UUID playerId,
    @Nonnull String economyId,
    @Nonnull String currencyId,
    @Nonnull BigDecimal amount,
    @Nonnull String reason
  ) {
    return setBalance(playerId, new EconomySelector(economyId, currencyId), amount, reason);
  }

  /**
   * Sets the balance of a player.
   *
   * @param playerId player UUID
   * @param selector economy and currency selector
   * @param amount   new balance amount
   * @param reason   transaction reason
   *
   * @return {@code true} if the balance was set successfully
   */
  public static boolean setBalance(
    @Nonnull UUID playerId,
    @Nonnull EconomySelector selector,
    @Nonnull BigDecimal amount,
    @Nonnull String reason
  ) {
    return resolveEconomy(selector)
      .setBalance(playerId, selector.getCurrency(), amount, reason);
  }

  /* -------------------------------------------------------------------------- */
  /* Deposit                                                                    */
  /* -------------------------------------------------------------------------- */

  /**
   * Deposits money into a player's account.
   *
   * @param playerId   player UUID
   * @param economyId  economy identifier
   * @param currencyId currency identifier
   * @param amount     amount to deposit (must be positive)
   * @param reason     transaction reason
   *
   * @return {@code true} if the deposit was successful
   */
  public static boolean deposit(
    @Nonnull UUID playerId,
    @Nonnull String economyId,
    @Nonnull String currencyId,
    @Nonnull BigDecimal amount,
    @Nonnull String reason
  ) {
    return deposit(playerId, new EconomySelector(economyId, currencyId), amount, reason);
  }

  /**
   * Deposits money into a player's account.
   *
   * @param playerId player UUID
   * @param selector economy and currency selector
   * @param amount   amount to deposit (must be positive)
   * @param reason   transaction reason
   *
   * @return {@code true} if the deposit was successful
   */
  public static boolean deposit(
    @Nonnull UUID playerId,
    @Nonnull EconomySelector selector,
    @Nonnull BigDecimal amount,
    @Nonnull String reason
  ) {
    return resolveEconomy(selector)
      .deposit(playerId, selector.getCurrency(), amount, reason);
  }

  /* -------------------------------------------------------------------------- */
  /* Withdraw                                                                    */
  /* -------------------------------------------------------------------------- */

  /**
   * Withdraws money from a player's account.
   *
   * @param playerId   player UUID
   * @param economyId  economy identifier
   * @param currencyId currency identifier
   * @param amount     amount to withdraw (must be positive)
   * @param reason     transaction reason
   *
   * @return {@code true} if the withdrawal was successful
   */
  public static boolean withdraw(
    @Nonnull UUID playerId,
    @Nonnull String economyId,
    @Nonnull String currencyId,
    @Nonnull BigDecimal amount,
    @Nonnull String reason
  ) {
    return withdraw(playerId, new EconomySelector(economyId, currencyId), amount, reason);
  }

  /**
   * Withdraws money from a player's account.
   *
   * @param playerId player UUID
   * @param selector economy and currency selector
   * @param amount   amount to withdraw (must be positive)
   * @param reason   transaction reason
   *
   * @return {@code true} if the withdrawal was successful
   */
  public static boolean withdraw(
    @Nonnull UUID playerId,
    @Nonnull EconomySelector selector,
    @Nonnull BigDecimal amount,
    @Nonnull String reason
  ) {
    return resolveEconomy(selector)
      .withdraw(playerId, selector.getCurrency(), amount, reason);
  }

  /* -------------------------------------------------------------------------- */
  /* Checks                                                                      */
  /* -------------------------------------------------------------------------- */

  /**
   * Checks whether a player has at least the specified amount.
   *
   * @param playerId   player UUID
   * @param economyId  economy identifier
   * @param currencyId currency identifier
   * @param amount     minimum amount
   *
   * @return {@code true} if the player has enough balance
   */
  public static boolean hasBalance(
    @Nonnull UUID playerId,
    @Nonnull String economyId,
    @Nonnull String currencyId,
    @Nonnull BigDecimal amount
  ) {
    return hasBalance(playerId, new EconomySelector(economyId, currencyId), amount);
  }

  /**
   * Checks whether a player has at least the specified amount.
   *
   * @param playerId player UUID
   * @param selector economy and currency selector
   * @param amount   minimum amount
   *
   * @return {@code true} if the player has enough balance
   */
  public static boolean hasBalance(
    @Nonnull UUID playerId,
    @Nonnull EconomySelector selector,
    @Nonnull BigDecimal amount
  ) {
    return resolveEconomy(selector)
      .hasBalance(playerId, selector.getCurrency(), amount);
  }

  /* -------------------------------------------------------------------------- */
  /* Internal                                                                    */
  /* -------------------------------------------------------------------------- */

  /**
   * Resolves an economy from a selector.
   *
   * @param selector economy selector
   *
   * @return resolved economy
   *
   * @throws IllegalArgumentException if the economy does not exist
   */
  @Nonnull
  private static Economy resolveEconomy(@Nonnull EconomySelector selector) {
    Economy economy = ECONOMIES.get(selector.getEconomy());
    if (economy == null) throw new IllegalArgumentException("Economy not found: " + selector.getEconomy());
    return economy;
  }
}
