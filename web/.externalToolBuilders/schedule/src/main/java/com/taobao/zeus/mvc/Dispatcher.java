package com.taobao.zeus.mvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.taobao.zeus.schedule.mvc.ScheduleInfoLog;

public class Dispatcher extends BaseObservable {

  public static final EventType BeforeDispatch = new EventType();

  public static final EventType AfterDispatch = new EventType();


  /**
   * Forwards an application event to the dispatcher.
   * 
   * @param event the application event
   */
  public void forwardEvent(AppEvent event) {
    dispatch(event);
  }

  /**
   * Creates and forwards an application event to the dispatcher.
   * 
   * @param eventType the application event type
   */
  public void forwardEvent(EventType eventType) {
    dispatch(eventType);
  }

  /**
   * Creates and forwards an application event to the dispatcher.
   * 
   * @param eventType the application event type
   * @param data the event data
   */
  public void forwardEvent(EventType eventType, Object data) {
    dispatch(new AppEvent(eventType, data));
  }

  /**
   * Creates and forwards an application event to the dispatcher.
   * 
   * @param eventType the application event type
   * @param data the event data
   * @param historyEvent true to mark event as a history event
   */
  public void forwardEvent(EventType eventType, Object data, boolean historyEvent) {
    AppEvent ae = new AppEvent(eventType, data);
    ae.setHistoryEvent(historyEvent);
    dispatch(ae);
  }


  private Map<String, AppEvent> history;

  private List<Controller> controllers;

  public Dispatcher() {
    controllers = new ArrayList<Controller>();
  }

  /**
   * Adds a controller.
   * 
   * @param controller the controller to be added
   */
  public void addController(Controller controller) {
    if (!controllers.contains(controller)) {
      controllers.add(controller);
    }
  }

  /**
   * Adds a listener to receive dispatch events.
   * 
   * @param listener the listener to add
   */
  public void addDispatcherListener(DispatcherListener listener) {
    addListener(BeforeDispatch, listener);
    addListener(AfterDispatch, listener);
  }


  /**
   * The dispatcher will query its controllers and pass the application event to
   * controllers that can handle the particular event type.
   * 
   * @param type the event type
   */
  public void dispatch(EventType type) {
    dispatch(new AppEvent(type));
  }

  /**
   * The dispatcher will query its controllers and pass the application event to
   * controllers that can handle the particular event type.
   * 
   * @param type the event type
   * @param data the app event data
   */
  public void dispatch(EventType type, Object data) {
    dispatch(new AppEvent(type, data));
  }

  /**
   * Returns all controllers.
   * 
   * @return the list of controllers
   */
  public List<Controller> getControllers() {
    return controllers;
  }

  /**
   * Returns the dispatcher's history cache.
   * 
   * @return the history
   */
  public Map<String, AppEvent> getHistory() {
    return history;
  }

  /**
   * Removes a controller.
   * 
   * @param controller the controller to be removed
   */
  public void removeController(Controller controller) {
	  boolean contain=controllers.contains(controller);
	  System.out.println(contain);
    controllers.remove(controller);
  }

  /**
   * Removes a previously added listener.
   * 
   * @param listener the listener to be removed
   */
  public void removeDispatcherListener(DispatcherListener listener) {
    removeListener(BeforeDispatch, listener);
    removeListener(AfterDispatch, listener);
  }

  private void dispatch(AppEvent event) {
    try {
		MvcEvent e = new MvcEvent(this, event);
		e.setAppEvent(event);
		if (fireEvent(BeforeDispatch, e)) {
		  List<Controller> copy = new ArrayList<Controller>(controllers);
		  for (Controller controller : copy) {
		    if (controller.canHandle(event)) {
		      if (!controller.initialized) {
		        controller.initialized = true;
		        controller.initialize();
		      }
		      controller.handleEvent(event);
		    }
		  }
		  fireEvent(AfterDispatch, e);
		}
	} catch (Exception e) {
		ScheduleInfoLog.error("dispatch error", e);
	}
  }

}
