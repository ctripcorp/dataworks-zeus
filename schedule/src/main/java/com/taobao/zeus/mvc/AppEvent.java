package com.taobao.zeus.mvc;

import java.util.HashMap;
import java.util.Map;


/**
 * <code>AppEvents</code> are used to pass messages between
 * <code>Controllers</code> and <code>Views</code>. All events have a specific
 * type which are used to identify the event. Typically, applications will
 * define all application events in a constants class.
 */
public class AppEvent extends BaseEvent {

  private Object data;
  private Map<String, Object> dataMap;
  private boolean historyEvent;
  private String token;

  /**
   * Creates a new application event.
   * 
   * @param type the event type
   */
  public AppEvent(EventType type) {
    super(type);
  }

  /**
   * Creates a new application event.
   * 
   * @param type the event type
   * @param data the data
   */
  public AppEvent(EventType type, Object data) {
    super(type);
    this.data = data;
  }

  /**
   * Creates a new application event.
   * 
   * @param type the event type
   * @param data the event data
   * @param token the history token
   */
  public AppEvent(EventType type, Object data, String token) {
    this(type, data);
    this.token = token;
    historyEvent = true;
  }

  /**
   * Returns the application specified data.
   * 
   * @param <X> the data type
   * @return the data
   */
  @SuppressWarnings("unchecked")
  public <X> X getData() {
    return (X) data;
  }

  /**
   * Returns the application defined property for the given name, or
   * <code>null</code> if it has not been set.
   * 
   * @param key the name of the property
   * @return the value or <code>null</code> if it has not been set
   */
  @SuppressWarnings("unchecked")
  public <X> X getData(String key) {
    if (dataMap == null) return null;
    return (X) dataMap.get(key);
  }

  /**
   * Returns the history token.
   * 
   * @return the history token
   */
  public String getToken() {
    return token;
  }

  /**
   * Returns true if the event is a history event.
   * 
   * @return true for a history event
   */
  public boolean isHistoryEvent() {
    return historyEvent;
  }

  /**
   * Sets the application defined data.
   * 
   * @param data the data
   */
  public void setData(Object data) {
    this.data = data;
  }

  /**
   * Sets the application defined property with the given name.
   * 
   * @param key the name of the property
   * @param data the new value for the property
   */
  public void setData(String key, Object data) {
    if (dataMap == null) dataMap = new HashMap<String,Object>();
    dataMap.put(key, data);
  }

  /**
   * True to create a history item for this event when passed through the
   * dispatcher (defaults to false).
   * 
   * @param historyEvent true for a history event
   */
  public void setHistoryEvent(boolean historyEvent) {
    this.historyEvent = historyEvent;
  }

  /**
   * The optional history token (defaults to null). If null, a token will be
   * generated for the event.
   * 
   * @param token the history token
   */
  public void setToken(String token) {
    this.token = token;
  }

  public String toString() {
    return "Event Type: " + getType();
  }
}
