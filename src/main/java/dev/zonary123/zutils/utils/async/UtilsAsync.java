package dev.zonary123.zutils.utils.async;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * UtilsAsync is a centralized manager for per-mod AsyncContexts.
 * - Each mod can have its own AsyncContext (executor + scheduler)
 * - Provides methods to create, retrieve, and shutdown contexts globally
 */
public class UtilsAsync {

  private static final Cache<String, AsyncContext> contexts = Caffeine.newBuilder()
    .build();

  /**
   * Creates a new AsyncContext for a mod if it doesn't exist yet.
   *
   * @param modId      Unique identifier for the mod
   * @param threadName Base name for the threads of this context
   *
   * @return The AsyncContext for the mod
   */
  public static AsyncContext createContext(String modId, String threadName) {
    return contexts.get(modId, id -> new AsyncContext(threadName));
  }

  /**
   * Retrieves an existing AsyncContext for a mod.
   *
   * @param modId Unique identifier for the mod
   *
   * @return The AsyncContext if it exists, otherwise null
   */
  public static AsyncContext getContext(String modId) {
    return contexts.get(modId, id -> new AsyncContext(modId + "-Worker"));
  }

  /**
   * Shuts down all AsyncContexts and clears the manager.
   * Should be called on server shutdown.
   */
  public static void shutdownAll() {
    contexts.asMap().values().forEach(AsyncContext::shutdown);
    contexts.invalidateAll();
  }
}
