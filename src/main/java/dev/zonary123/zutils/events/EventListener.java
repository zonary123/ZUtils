package dev.zonary123.zutils.events;

/**
 * @author Carlos Varas Alonso - 22/08/2025 19:04
 */
public interface EventListener<T> {
  void onEvent(T data);
}


