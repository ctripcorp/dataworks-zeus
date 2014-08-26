package com.taobao.zeus.web.platform.client.util.place;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.web.bindery.event.shared.EventBus;
import com.taobao.zeus.web.platform.client.app.PlatformPlace;

/**
 * Event thrown when the user has reached a new location in the app.
 */
public class PlatformPlaceChangeEvent extends GwtEvent<PlatformPlaceChangeEvent.Handler> {

  /**
   * Implemented by handlers of PlaceChangeEvent.
   */
  public interface Handler extends EventHandler {
    /**
     * Called when a {@link PlatformPlaceChangeEvent} is fired.
     *
     * @param event the {@link PlatformPlaceChangeEvent}
     */
    void onPlaceChange(PlatformPlaceChangeEvent event);
  }

  /**
   * A singleton instance of Type&lt;Handler&gt;.
   */
  public static final Type<Handler> TYPE = new Type<Handler>();

  private final PlatformPlace newPlace;
  private boolean asyncCall=false;
  private boolean logHistory=false;
  /**
   * Constructs a PlaceChangeEvent for the given {@link Place}.
   *
   * @param newPlace a {@link Place} instance
   */
  public PlatformPlaceChangeEvent(PlatformPlace newPlace) {
    this.newPlace = newPlace;
  }
  
  public PlatformPlaceChangeEvent(PlatformPlace newPlace,boolean logHistory){
	  this.newPlace=newPlace;
	  this.logHistory=logHistory;
  }

  @Override
  public Type<Handler> getAssociatedType() {
    return TYPE;
  }

  /**
   * Return the new {@link Place}.
   *
   * @return a {@link Place} instance
   */
  public PlatformPlace getNewPlace() {
    return newPlace;
  }

  @Override
  protected void dispatch(Handler handler) {
    handler.onPlaceChange(this);
  }

public boolean isAsyncCall() {
	return asyncCall;
}

public void setAsyncCall(boolean asyncCall) {
	this.asyncCall = asyncCall;
}

public boolean isLogHistory() {
	return logHistory;
}

public void setLogHistory(boolean logHistory) {
	this.logHistory = logHistory;
}
}
