package com.taobao.zeus.mvc;

import java.util.List;

/**
 * Abstract base class for objects that register listeners and fire events.
 * 
 * <pre>
    Observable observable = new BaseObservable();
    observable.addListener(Events.Select, new Listener&lt;BaseEvent>() {
      public void handleEvent(BaseEvent be) {

      }
    });
    observable.fireEvent(Events.Select, new BaseEvent()); 
 * </pre>
 * 
 * @see Listener
 * @see BaseEvent
 */
public interface Observable {

  /**
   * Adds a listener bound by the given event type.
   * 
   * @param eventType the eventType
   * @param listener the listener to be added
   */
  public void addListener(EventType eventType, Listener<? extends BaseEvent> listener);

  /**
   * Fires an event.
   * 
   * @param eventType eventType the event type
   * @param be the base event
   * @return false if any listeners cancel the event.
   */
  public boolean fireEvent(EventType eventType, BaseEvent be);

  /**
   * Returns a list of listeners registered for the given event type. The
   * returned list may be modified.
   * 
   * @param eventType the event type
   * @return the list of listeners
   */
  public List<Listener<? extends BaseEvent>> getListeners(EventType eventType);
  
  /**
   * Returns true if the observable has any listeners.
   * 
   * @return true for any listeners
   */
  public boolean hasListeners();
  
  /**
   * Returns true if the observable has listeners for the given event type.
   * 
   * @param eventType the event type
   * @return true for 1 or more listeners with the given event type
   */
  public boolean hasListeners(EventType eventType);

  /**
   * Removes all listeners.
   */
  public void removeAllListeners();

  /**
   * Removes a listener.
   * 
   * @param eventType the event type
   * @param listener the listener to be removed
   */
  public void removeListener(EventType eventType, Listener<? extends BaseEvent> listener);

}
