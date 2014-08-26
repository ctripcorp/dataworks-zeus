/**
 * Sencha GXT 3.0.0b - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.taobao.zeus.web.platform.client.module.filemanager;

import java.util.Date;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface FileModelProperties extends PropertyAccess<FileModel> {
  
  @Path("id")
  ModelKeyProvider<FileModel> key();

  ValueProvider<FileModel, Boolean> folder();

  ValueProvider<FileModel, Date> modifiedDate();

  ValueProvider<FileModel, String> name();

  //ValueProvider<FileModel, Long> size();
  
}

