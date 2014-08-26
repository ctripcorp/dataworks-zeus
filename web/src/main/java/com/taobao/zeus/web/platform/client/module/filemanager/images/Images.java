package com.taobao.zeus.web.platform.client.module.filemanager.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public class Images {

  public interface FileImageResources extends ClientBundle {
    ImageResource arrow_in();

    ImageResource arrow_out();


    ImageResource cross();


    ImageResource folder_add();

    ImageResource page_white();

    ImageResource page_white_add();

    ImageResource script();

    ImageResource script_add();

    ImageResource textfield_rename();

	ImageResource bullet_go();

  }

  private static FileImageResources imageResources;

  public static FileImageResources getImageResources() {
    if (imageResources == null) {
      imageResources = GWT.create(FileImageResources.class);
    }
    return imageResources;
  }
}
