package dev.zonary123.zutils.utils.economy;

import com.ecotale.api.EcotaleAPI;
import com.ecotale.economy.EconomyManager;

import java.math.BigDecimal;
import java.util.UUID;

/**
 *
 * @author Carlos Varas Alonso - 24/01/2026 23:30
 */
public class EcoTaleEconomyProvider extends Economy {
  /**
   * Constructor for Economy.
   *
   * @param economyId ID of the economy
   */
  public EcoTaleEconomyProvider(String economyId) {
    super(economyId);
  }

  @Override
  public BigDecimal getBalance(UUID playerId, String currencyId) {
    return BigDecimal.valueOf(EcotaleAPI.getBalance(playerId));
  }

  @Override
  public boolean setBalance(UUID playerId, String currencyId, BigDecimal amount, String reason) {
    EcotaleAPI.setBalance(playerId, amount.doubleValue(), reason);
    return true;
  }

  @Override
  public boolean deposit(UUID playerId, String currencyId, BigDecimal amount, String reason) {
    return EcotaleAPI.deposit(playerId, amount.doubleValue(), reason);
  }

  @Override
  public boolean withdraw(UUID playerId, String currencyId, BigDecimal amount, String reason) {
    return EcotaleAPI.withdraw(playerId, amount.doubleValue(), reason);
  }

  @Override
  public boolean hasBalance(UUID playerId, String currencyId, BigDecimal amount) {
    return EcotaleAPI.getBalance(playerId) >= amount.doubleValue();
  }

  @Override
  public String formatCurrency(String currencyId, BigDecimal amount) {
    return EcotaleAPI.format(amount.doubleValue());
  }

  @Override
  public boolean transfer(UUID fromPlayerId, UUID toPlayerId, String currencyId, BigDecimal amount, String reason) {
    return EcotaleAPI.transfer(fromPlayerId, toPlayerId, amount.doubleValue(), reason).equals(EconomyManager.TransferResult.SUCCESS);
  }
}
