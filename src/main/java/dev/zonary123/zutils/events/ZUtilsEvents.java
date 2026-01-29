package dev.zonary123.zutils.events;

import dev.zonary123.zutils.events.models.*;

/**
 *
 * @author Carlos Varas Alonso - 20/01/2026 9:59
 */
public class ZUtilsEvents {
  public static final EventChannel<EventBlockBreak> BLOCK_BREAK_EVENT = new EventChannel<>();
  public static final EventChannel<EventBlockPlaced> BLOCK_PLACED_EVENT = new EventChannel<>();
  public static final EventChannel<InteractPickUp> INTERACT_PICKUP_EVENT = new EventChannel<>();
  public static final EventChannel<Craft> CRAFT_EVENT = new EventChannel<>();
  public static final EventChannel<Damage> DAMAGE_EVENT = new EventChannel<>();
  public static final EventChannel<KillEntity> KILL_ENTITY_EVENT = new EventChannel<>();
  public static final EventChannel<Chat> CHAT_EVENT = new EventChannel<>();
  public static final EventChannel<Command> COMMAND_EVENT = new EventChannel<>();
  public static final EventChannel<Travel> TRAVEL_EVENT = new EventChannel<>();
}
