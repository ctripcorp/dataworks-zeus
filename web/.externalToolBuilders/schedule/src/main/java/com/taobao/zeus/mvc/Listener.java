package com.taobao.zeus.mvc;

import java.util.EventListener;

/**
 * Interface for objects that are notified of GXT events.
 * 
 * <pre>
    Button btn = new Button();
    btn.addListener(Events.Select, new Listener&lt;ButtonEvent>() {
      public void handleEvent(ButtonEvent be) {
        Button button = be.button;
      }
    });
 * </pre>
 * @param <E> the base event type
 */
public interface Listener<E extends BaseEvent> extends EventListener {

  /**
   * Sent when an event that the listener has registered for occurs.
   * 
   * @param be the event which occurred
   */
  public void handleEvent(E be);

}
