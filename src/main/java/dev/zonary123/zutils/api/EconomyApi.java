package dev.zonary123.zutils.api;

import dev.zonary123.zutils.ZUtils;
import dev.zonary123.zutils.models.EconomySelector;
import dev.zonary123.zutils.utils.economy.Economy;
import dev.zonary123.zutils.utils.economy.EconomyResult;
import dev.zonary123.zutils.utils.economy.providers.EcoTaleEconomyProvider;
import dev.zonary123.zutils.utils.economy.providers.EconomySystemProvider;
import dev.zonary123.zutils.utils.economy.providers.ZEconomyProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central API for economy management.
 * Provides static access to different economy implementations and currencies.
 *
 * @author Carlos
 */
public final class EconomyApi {

  private static final Map<String, Economy> ECONOMIES = new ConcurrentHashMap<>();
  private static Economy DEFAULT_ECONOMY;

  static {
    registerEconomy(new ZEconomyProvider("ZEconomy"));
    registerEconomy(new EcoTaleEconomyProvider("EcoTale"));
    registerEconomy(new EconomySystemProvider("EconomySystem"));
  }

  private EconomyApi() {
  }

  /* -------------------------------------------------------------------------- */
  /* Registry                                                                    */
  /* -------------------------------------------------------------------------- */

  public static void registerEconomy(@Nonnull Economy economy) {
    String economyId = economy.getEconomyId();
    if (ECONOMIES.putIfAbsent(economyId, economy) != null) {
      ZUtils.getLog().atWarning().log("Economy with ID '%s' is already registered.".formatted(economyId));
    }

    try {
      // test registration
      economy.getBalance(UUID.randomUUID(), "TEST_CURRENCY");
      ZUtils.getLog().atInfo().log("Economy '%s' registered successfully.".formatted(economyId));
    } catch (Throwable e) {
      ZUtils.getLog().atWarning().log("Economy '%s' registration test failed: %s".formatted(economyId, e.getMessage()));
      ECONOMIES.remove(economyId);
    }
  }

  @Nullable
  public static Economy getEconomy(@Nonnull String economyId) {
    if (ECONOMIES.size() == 1) return ECONOMIES.values().iterator().next();
    Economy economy = ECONOMIES.get(economyId);
    if (economy == null) {
      if (ZUtils.getConfig().isDebug()) {
        ZUtils.getLog().atWarning().log("Economy with ID '%s' not found. Using default economy.".formatted(economyId));
      }
      economy = ECONOMIES.values().iterator().next();
    }
    return economy;
  }

  @Nonnull
  public static Map<String, Economy> getEconomies() {
    return ECONOMIES;
  }

  /* -------------------------------------------------------------------------- */
  /* Balance & Transactions                                                      */
  /* -------------------------------------------------------------------------- */

  @Nonnull
  public static CompletableFuture<EconomyResult> getBalance(@Nonnull UUID playerId, @Nonnull String economyId, @Nonnull String currencyId) {
    return getBalance(playerId, new EconomySelector(economyId, currencyId));
  }

  @Nonnull
  public static CompletableFuture<EconomyResult> getBalance(@Nonnull UUID playerId, @Nonnull EconomySelector selector) {
    return resolveEconomy(selector).getBalance(playerId, selector.getCurrency());
  }

  @Nonnull
  public static CompletableFuture<EconomyResult> setBalance(@Nonnull UUID playerId, @Nonnull String economyId, @Nonnull String currencyId, @Nonnull BigDecimal amount, @Nonnull String reason) {
    return setBalance(playerId, new EconomySelector(economyId, currencyId), amount, reason);
  }

  @Nonnull
  public static CompletableFuture<EconomyResult> setBalance(@Nonnull UUID playerId, @Nonnull EconomySelector selector, @Nonnull BigDecimal amount, @Nonnull String reason) {
    return resolveEconomy(selector).setBalance(playerId, selector.getCurrency(), amount, reason);
  }

  @Nonnull
  public static CompletableFuture<EconomyResult> deposit(@Nonnull UUID playerId, @Nonnull String economyId, @Nonnull String currencyId, @Nonnull BigDecimal amount, @Nonnull String reason) {
    return deposit(playerId, new EconomySelector(economyId, currencyId), amount, reason);
  }

  @Nonnull
  public static CompletableFuture<EconomyResult> deposit(@Nonnull UUID playerId, @Nonnull EconomySelector selector, @Nonnull BigDecimal amount, @Nonnull String reason) {
    return resolveEconomy(selector).deposit(playerId, selector.getCurrency(), amount, reason);
  }

  @Nonnull
  public static CompletableFuture<EconomyResult> withdraw(@Nonnull UUID playerId, @Nonnull String economyId, @Nonnull String currencyId, @Nonnull BigDecimal amount, @Nonnull String reason) {
    return withdraw(playerId, new EconomySelector(economyId, currencyId), amount, reason);
  }

  @Nonnull
  public static CompletableFuture<EconomyResult> withdraw(@Nonnull UUID playerId, @Nonnull EconomySelector selector, @Nonnull BigDecimal amount, @Nonnull String reason) {
    return resolveEconomy(selector).withdraw(playerId, selector.getCurrency(), amount, reason);
  }

  public static CompletableFuture<Boolean> hasBalance(@Nonnull UUID playerId, @Nonnull String economyId, @Nonnull String currencyId, @Nonnull BigDecimal amount) {
    return hasBalance(playerId, new EconomySelector(economyId, currencyId), amount);
  }

  public static CompletableFuture<Boolean> hasBalance(@Nonnull UUID playerId, @Nonnull EconomySelector selector, @Nonnull BigDecimal amount) {
    return resolveEconomy(selector).hasBalance(playerId, selector.getCurrency(), amount);
  }

  @Nonnull
  public static CompletableFuture<EconomyResult> transfer(@Nonnull UUID fromPlayerId, @Nonnull UUID toPlayerId, @Nonnull EconomySelector selector, @Nonnull BigDecimal amount, @Nonnull String reason) {
    return resolveEconomy(selector).transfer(fromPlayerId, toPlayerId, selector.getCurrency(), amount, reason);
  }

  /* -------------------------------------------------------------------------- */
  /* Internal                                                                    */
  /* -------------------------------------------------------------------------- */

  @Nonnull
  private static Economy resolveEconomy(@Nonnull EconomySelector selector) {
    Economy economy = getEconomy(selector.getEconomy());
    if (economy == null) throw new IllegalArgumentException("Economy not found: " + selector.getEconomy());
    return economy;
  }
}
