package dev.zonary123.zutils.utils.economy;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

/**
 * Base abstract class for any economy provider.
 * <p>
 * Implementations must be thread-safe and handle their own persistence / storage logic.
 * All public API methods are asynchronous and return CompletableFutures.
 * </p>
 * Supports multi-currency, safe transfers with rollback, and audit reasons.
 */
public abstract class Economy {

  private static final Logger LOGGER = Logger.getLogger(Economy.class.getName());

  /**
   * Unique ID of this economy provider
   */
  private final String economyId;

  protected Economy(String economyId) {
    this.economyId = Objects.requireNonNull(economyId, "economyId cannot be null");
  }

  public String getEconomyId() {
    return economyId;
  }

  // =======================
  // Abstract methods to implement
  // =======================

  public abstract CompletableFuture<EconomyResult> getBalance(UUID playerId, String currencyId);

  public abstract CompletableFuture<EconomyResult> setBalance(UUID playerId, String currencyId, BigDecimal amount, String reason);

  public abstract CompletableFuture<EconomyResult> deposit(UUID playerId, String currencyId, BigDecimal amount, String reason);

  public abstract CompletableFuture<EconomyResult> withdraw(UUID playerId, String currencyId, BigDecimal amount, String reason);

  public abstract CompletableFuture<Boolean> hasBalance(UUID playerId, String currencyId, BigDecimal amount);

  public abstract String formatCurrency(String currencyId, BigDecimal amount);

  // =======================
  // Default business logic
  // =======================

  /**
   * Transfer an amount from one player to another safely.
   * <p>
   * - Rolls back if deposit fails.
   * - Handles exceptions internally and returns a failed EconomyResult on error.
   * - Validates amount > 0 and different players.
   * </p>
   */
  public CompletableFuture<EconomyResult> transfer(
    UUID fromPlayerId,
    UUID toPlayerId,
    String currencyId,
    BigDecimal amount,
    String reason
  ) {
    if (fromPlayerId.equals(toPlayerId)) return getBalance(fromPlayerId, currencyId)
      .thenApply(balance -> EconomyResult.success(balance.getBefore(), balance.getBefore(), "Self-transfer ignored"));

    if (amount == null || amount.signum() <= 0) return getBalance(fromPlayerId, currencyId)
      .thenApply(balance -> EconomyResult.fail("Invalid transfer amount", balance.getBefore()));


    Objects.requireNonNull(currencyId, "currencyId cannot be null");
    Objects.requireNonNull(reason, "reason cannot be null");

    return withdraw(fromPlayerId, currencyId, amount, reason)
      .thenCompose(withdrawResult -> {
        if (!withdrawResult.isSuccess()) return CompletableFuture.completedFuture(EconomyResult.fail(
          "Withdrawal failed: " + withdrawResult.getReason(),
          withdrawResult.getBefore()
        ));

        return deposit(toPlayerId, currencyId, amount, reason)
          .thenCompose(depositResult -> {
            if (!depositResult.isSuccess()) {
              // rollback
              return deposit(fromPlayerId, currencyId, amount, "Transfer rollback")
                .thenApply(r -> EconomyResult.fail(
                  "Deposit failed, rolled back: " + depositResult.getReason(),
                  withdrawResult.getBefore()
                ));
            }

            return CompletableFuture.completedFuture(EconomyResult.success(
              withdrawResult.getBefore(),
              withdrawResult.getBefore().subtract(amount),
              reason
            ));
          });
      })
      .handle((result, throwable) -> {
        if (throwable != null) {
          LOGGER.severe("Economy transfer failed: " + throwable);
          return EconomyResult.fail("Transfer exception occurred", BigDecimal.ZERO);
        }
        return result;
      });
  }
}
