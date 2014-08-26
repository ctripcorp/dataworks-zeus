package com.taobao.zeus.web.platform.client.util;

import com.google.gwt.user.client.ui.HasWidgets;

/**
 * Base interface for "mini applications" or "desklets" that are displayed on
 * the desktop. It defines the basic capability required by all mini application
 * presenters so that they can be displayed and managed by the desktop.
 */
public interface Presenter {

  /**
   * Adds the view managed by this presenter to the user interface.
   * 
   * @param hasWidgets the user interface
   */
  void go(HasWidgets hasWidgets);
  
  PlatformContext getPlatformContext();

}
