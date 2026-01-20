package dev.zonary123.zutils.events;

import dev.zonary123.zutils.events.models.Craft;
import dev.zonary123.zutils.events.models.EventBlockBreak;
import dev.zonary123.zutils.events.models.EventBlockPlaced;
import dev.zonary123.zutils.events.models.InteractPickUp;

/**
 *
 * @author Carlos Varas Alonso - 20/01/2026 9:59
 */
public class ZUtilsEvents {
  public static final EventChannel<EventBlockBreak> BLOCK_BREAK_EVENT = new EventChannel<>();
  public static final EventChannel<EventBlockPlaced> BLOCK_PLACED_EVENT = new EventChannel<>();
  public static final EventChannel<InteractPickUp> INTERACT_PICKUP_EVENT = new EventChannel<>();
  public static final EventChannel<Craft> CRAFT_EVENT = new EventChannel<>();
}
