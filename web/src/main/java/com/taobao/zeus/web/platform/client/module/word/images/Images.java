package com.taobao.zeus.web.platform.client.module.word.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public class Images {

  public interface WordImageResources extends ClientBundle {
	  
    ImageResource traffic_green();
    ImageResource traffic_red();
    ImageResource processingIcon();
    ImageResource download();

  }

  private static WordImageResources imageResources;

  public static WordImageResources getImageResources() {
    if (imageResources == null) {
      imageResources = GWT.create(WordImageResources.class);
    }
    return imageResources;
  }
}
