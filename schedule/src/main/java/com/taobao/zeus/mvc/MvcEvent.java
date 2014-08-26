package com.taobao.zeus.mvc;


/**
 * MVC event type.
 * 
 * <p/>
 * Note: For a given event, only the fields which are appropriate will be filled
 * in. The appropriate fields for each event are documented by the event source.
 */
public class MvcEvent extends BaseEvent {

  private AppEvent appEvent;
  private Dispatcher dispatcher;
  private String name;

  /**
   * Creates a new mvc event.
   * 
   * @param d the dispatcher
   * @param ae the application event
   */
  public MvcEvent(Dispatcher d, AppEvent ae) {
    super(d);
    this.dispatcher = d;
    this.appEvent = ae;
  }

  /**
   * Returns the application event.
   * 
   * @return the application event
   */
  public AppEvent getAppEvent() {
    return appEvent;
  }

  /**
   * Returns the dispatcher.
   * 
   * @return the dispatcher
   */
  public Dispatcher getDispatcher() {
    return dispatcher;
  }

  /**
   * Returns the name.
   * 
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the application event.
   * 
   * @param appEvent the application event
   */
  public void setAppEvent(AppEvent appEvent) {
    this.appEvent = appEvent;
  }

  /**
   * Sets the dispatcher.
   * 
   * @param dispatcher the dispatcher
   */
  public void setDispatcher(Dispatcher dispatcher) {
    this.dispatcher = dispatcher;
  }

  /**
   * Sets the name.
   * 
   * @param name the name
   */
  public void setName(String name) {
    this.name = name;
  }

}
