package dev.zonary123.zutils.utils.economy;

import dev.zonary123.zeconomy.api.ZEconomyApi;

import java.math.BigDecimal;
import java.util.UUID;

/**
 *
 * @author Carlos Varas Alonso - 18/01/2026 7:29
 */
public class ZEconomyProvider extends Economy {
  /**
   * Constructor for ZEconomyProvider.
   *
   * @param economyId ID of the economy
   */
  public ZEconomyProvider(String economyId) {
    super(economyId);
  }

  /**
   * Get the balance of a player for a specific currency.
   *
   * @param playerId   UUID of the player
   * @param currencyId ID of the currency
   *
   * @return the balance of the player in the specified currency
   */
  @Override public BigDecimal getBalance(UUID playerId, String currencyId) {
    return ZEconomyApi.getBalance(playerId, currencyId);
  }

  /**
   * Set the balance of a player for a specific currency.
   *
   * @param playerId   UUID of the player
   * @param currencyId ID of the currency
   * @param amount     Amount to set
   * @param reason     Reason for setting the balance
   *
   * @return true if the balance was set successfully, false otherwise
   */
  @Override public boolean setBalance(UUID playerId, String currencyId, BigDecimal amount, String reason) {
    return ZEconomyApi.setBalance(playerId, currencyId, amount, reason);
  }

  /**
   * Deposit an amount to a player's balance for a specific currency.
   *
   * @param playerId   UUID of the player
   * @param currencyId ID of the currency
   * @param amount     Amount to deposit
   * @param reason     Reason for the deposit
   *
   * @return true if the deposit was successful, false otherwise
   */
  @Override public boolean deposit(UUID playerId, String currencyId, BigDecimal amount, String reason) {
    return ZEconomyApi.deposit(playerId, currencyId, amount, reason);
  }

  /**
   * Withdraw an amount from a player's balance for a specific currency.
   *
   * @param playerId   UUID of the player
   * @param currencyId ID of the currency
   * @param amount     Amount to withdraw
   * @param reason     Reason for the withdrawal
   *
   * @return true if the withdrawal was successful, false otherwise
   */
  @Override public boolean withdraw(UUID playerId, String currencyId, BigDecimal amount, String reason) {
    return ZEconomyApi.withdraw(playerId, currencyId, amount, reason);
  }

  /**
   * Check if a player has at least a certain amount of a specific currency.
   *
   * @param playerId   UUID of the player
   * @param currencyId ID of the currency
   * @param amount     Amount to check
   *
   * @return true if the player has at least the specified amount, false otherwise
   */
  @Override public boolean hasBalance(UUID playerId, String currencyId, BigDecimal amount) {
    return ZEconomyApi.getBalance(playerId, currencyId).compareTo(amount) >= 0;
  }

  /**
   * Transfer an amount from one player to another for a specific currency.
   *
   * @param fromPlayerId UUID of the player to transfer from
   * @param toPlayerId   UUID of the player to transfer to
   * @param currencyId   ID of the currency
   * @param amount       Amount to transfer
   * @param reason       transaction reason
   *
   * @return {@code true} if the transfer was successful
   */
  @Override
  public boolean transfer(UUID fromPlayerId, UUID toPlayerId, String currencyId, BigDecimal amount, String reason) {
    return false;
  }
}
