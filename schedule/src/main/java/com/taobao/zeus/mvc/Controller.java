package com.taobao.zeus.mvc;

import java.util.ArrayList;
import java.util.List;


/**
 * <code>Controllers</code> process and respond to application events.
 */
public abstract class Controller {

  protected List<Controller> children;
  protected boolean initialized;
  protected Controller parent;

  private List<EventType> supportedEvents;

  /**
   * Add a child controller.
   * 
   * @param controller the controller to added
   */
  public void addChild(Controller controller) {
    if (children == null) children = new ArrayList<Controller>();
    children.add(controller);
    controller.parent = this;
  }

  /**
   * Determines if the controller can handle the particular event. Default
   * implementation checks against registered event types then queries all child
   * controllers.
   * 
   * @param event the event
   * @return <code>true</code> if event can be handled, <code>false</code>
   *         otherwise
   */
  public boolean canHandle(AppEvent event) {
    return canHandle(event, true);
  }

  /**
   * Determines if the controller can handle the particular event. Default
   * implementation checks against registered event types then queries all child
   * controllers if bubbleUp set to true.
   * 
   * @param event the event
   * @param bubbleDown true to bubble down children controllers
   * @return <code>true</code> if event can be handled, <code>false</code>
   *         otherwise
   */
  public boolean canHandle(AppEvent event, boolean bubbleDown) {
    if (supportedEvents != null && supportedEvents.contains(event.getType())) return true;
    if (children != null && bubbleDown) {
      for (Controller c : children) {
        if (c.canHandle(event, bubbleDown)) return true;
      }
    }
    return false;
  }

  /**
   * Forwards an event to any child controllers who can handle the event.
   * 
   * @param event the event to forward
   */
  public void forwardToChild(AppEvent event) {
    if (children != null) {
      for (Controller c : children) {
        if (!c.initialized) {
          c.initialize();
          c.initialized = true;
        }
        if (c.canHandle(event)) {
          c.handleEvent(event);
        }
      }
    }
  }

  /**
   * Forward an event to a view. Ensures the view is initialized before
   * forwarding the event.
   * 
   * @param view the view to forward the event
   * @param event the event to be forwarded
   */
  public void forwardToView(View view, AppEvent event) {
    if (!view.initialized) {
      view.initialize();
      view.initialized = true;
    }
    view.handleEvent(event);
  }

  /**
   * Forward an event to a view. Ensures the view is initialized before
   * forwarding the event.
   * 
   * @param view the view to forward the event
   * @param type the event type
   * @param data the event data
   */
  public void forwardToView(View view, EventType type, Object data) {
    AppEvent e = new AppEvent(type, data);
    forwardToView(view, e);
  }

  /**
   * Processes the event.
   * 
   * @param event the current event
   */
  public abstract void handleEvent(AppEvent event);

  /**
   * Called once prior to handleEvent being called.
   */
  protected void initialize() {

  }
  
  protected void destory(){
	  
  }

  /**
   * Registers the event type.
   * 
   * @param types the event types
   */
  protected void registerEventTypes(EventType... types) {
    if (supportedEvents == null) {
      supportedEvents = new ArrayList<EventType>();
    }
    if (types != null) {
      for (EventType type : types) {
        if (!supportedEvents.contains(type)) {
          supportedEvents.add(type);
        }
      }
    }
  }

}
