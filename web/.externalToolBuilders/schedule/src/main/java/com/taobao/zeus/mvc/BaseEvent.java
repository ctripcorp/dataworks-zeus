package com.taobao.zeus.mvc;

/**
 * Base class for all GXT events.
 * 
 * <p />
 * Note: For a given event, only the fields which are appropriate will be filled
 * in. The appropriate fields for each event are documented by the event source.
 */
public class BaseEvent {

  private boolean cancelled;
  private Object source;
  private EventType type;

  public BaseEvent(EventType type) {
    this.type = type;
  }

  /**
   * Returns true if the operation is being cancelled.
   * 
   * @return true if cancelled
   */
  public boolean isCancelled() {
    return cancelled;
  }

  /**
   * True to cancel the current operation (defaults to false). Only applies to
   * events that can be cancelled as defined by the object firing the event.
   * Canceling an operation does not stop all listeners from being notified of
   * the event.
   * 
   * @param cancelled true to cancel
   */
  public void setCancelled(boolean cancelled) {
    this.cancelled = cancelled;
  }

  /**
   * Creates a new base event instance.
   * 
   * @param source the source object
   */
  public BaseEvent(Object source) {
    this.source = source;
  }

  /**
   * Returns the object that fired the event.
   * 
   * @return the object that fired the event
   */
  public Object getSource() {
    return source;
  }

  /**
   * Returns the event's event type.
   * 
   * @return the event type
   */
  public EventType getType() {
    return type;
  }

  /**
   * Sets the object that fired the event.
   * 
   * @param source the source object
   */
  public void setSource(Object source) {
    this.source = source;
  }

  /**
   * Sets the event's event type.
   * 
   * @param type the event type
   */
  public void setType(EventType type) {
    this.type = type;
  }

}
