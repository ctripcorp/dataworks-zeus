package com.taobao.zeus.mvc;

import java.io.Serializable;

public class EventType implements Serializable {

	private static final long serialVersionUID = 1L;

	private static int count = 0;

  // needed to use FastMap for much better speed
  final String id;

  private int eventCode = -1;

  /**
   * Creates a new event type.
   */
  public EventType() {
    id = String.valueOf(count++);
  }

  /**
   * Creates a new browser based event type.
   * 
   * @param eventCode additional information about the event
   */
  public EventType(int eventCode) {
    this();
    this.eventCode = eventCode;
  }

  /**
   * Returns the event code.
   * 
   * @return the event code
   * @see Event
   */
  public int getEventCode() {
    return eventCode;
  }
}
