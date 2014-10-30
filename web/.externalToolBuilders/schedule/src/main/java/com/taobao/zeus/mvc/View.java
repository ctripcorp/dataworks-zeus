package com.taobao.zeus.mvc;


/**
 * <code>Views</code> are responsible for rendering the user interface.
 */
public abstract class View {

  protected Controller controller;
  protected boolean initialized;

  /**
   * Creates a new view instance.
   * 
   * @param controller the parent controller
   */
  public View(Controller controller) {
    this.controller = controller;
  }

  /**
   * Returns the view's controller.
   * 
   * @return the controller
   */
  public Controller getController() {
    return controller;
  }

  /**
   * Called when a view needs to pass an event to it's controller.
   * 
   * @param event the application event
   */
  protected void fireEvent(AppEvent event) {
    Controller c = controller;
    while (c != null) {
      if (c.canHandle(event, false)) {
        c.handleEvent(event);
      }
      c = c.parent;
    }
  }

  /**
   * Called when a view needs to pass an event to it's controller.
   * 
   * @param eventType the event type
   */
  protected void fireEvent(EventType eventType) {
    fireEvent(new AppEvent(eventType));
  }

  /**
   * Process the event.
   * 
   * @param event the event to be processed
   */
  protected abstract void handleEvent(AppEvent event);

  /**
   * Called once prior to handleEvent being called. Widgets should be
   * instantiated in the init method rather than the view constructor.
   */
  protected void initialize() {

  }

}
