package dev.zonary123.zutils.utils.async;

import java.util.HashMap;
import java.util.Map;

/**
 * UtilsAsync is a centralized manager for per-mod AsyncContexts.
 * - Each mod can have its own AsyncContext (executor + scheduler)
 * - Provides methods to create, retrieve, and shutdown contexts globally
 */
public class UtilsAsync {

  private static final Map<String, AsyncContext> contexts = new HashMap<>();

  /**
   * Creates a new AsyncContext for a mod if it doesn't exist yet.
   *
   * @param modId      Unique identifier for the mod
   * @param threadName Base name for the threads of this context
   *
   * @return The AsyncContext for the mod
   */
  public static AsyncContext createContext(String modId, String threadName) {
    return contexts.computeIfAbsent(modId, id -> new AsyncContext(threadName));
  }

  /**
   * Retrieves an existing AsyncContext for a mod.
   *
   * @param modId Unique identifier for the mod
   *
   * @return The AsyncContext if it exists, otherwise null
   */
  public static AsyncContext getContext(String modId) {
    return contexts.get(modId);
  }

  /**
   * Shuts down all AsyncContexts and clears the manager.
   * Should be called on server shutdown.
   */
  public static void shutdownAll() {
    contexts.values().forEach(AsyncContext::shutdown);
    contexts.clear();
  }
}
