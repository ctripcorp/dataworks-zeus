package com.taobao.zeus.web.platform.client.module.filemanager;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent.BeforeShowHandler;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.menu.SeparatorMenuItem;
import com.taobao.zeus.web.platform.client.module.filemanager.images.Images;

public class FileMenu implements IsWidget {

  private static final String FILE_TYPE = "fileType";
  private static final String FILE_SUFFIX="fileSuffix";

  private FileManagerPresenter fileSystemPresenter;

  private Menu fileMenu;
  private MenuItem createFolderMenuItem;
  private MenuItem createHiveMenuItem;
  private MenuItem createShellMenuItem;
  private MenuItem createTextMenuItem;
  private MenuItem collapseMenuItem;
  private MenuItem expandMenuItem;
  private MenuItem editNameMenuItem;
  private MenuItem openMenuItem;
  private MenuItem deleteMenuItem;
  private BeforeShowHandler beforeShowHandler;
  private SelectionHandler<MenuItem> createSelectionHandler;
  private SelectionHandler<MenuItem> expandSelectionHandler;
  private SelectionHandler<MenuItem> collapseSelectionHandler;
  private SelectionHandler<MenuItem> editNameSelectionHandler;
  private SelectionHandler<MenuItem> openSelectionHandler;
  private SelectionHandler<MenuItem> deleteSelectionHandler;

  public FileMenu(FileManagerPresenter fileSystemPresenter) {
    this.fileSystemPresenter = fileSystemPresenter;
  }

  public Widget asWidget() {
    getFileMenu();
    return fileMenu;
  }

  public Menu getFileMenu() {
    if (fileMenu == null) {
      fileMenu = new Menu();
      fileMenu.add(getCreateFolderMenuItem());
      fileMenu.add(new SeparatorMenuItem());
      fileMenu.add(getCreateHiveMenuItem());
      fileMenu.add(getCreateShellMenuItem());
      fileMenu.add(getCreateTextMenuItem());
      fileMenu.add(new SeparatorMenuItem());
      fileMenu.add(getExpandMenuItem());
      fileMenu.add(getCollapseMenuItem());
      fileMenu.add(new SeparatorMenuItem());
      fileMenu.add(getEditNameMenuItem());
      fileMenu.add(new SeparatorMenuItem());
      fileMenu.add(getOpenMenuItem());
      fileMenu.add(new SeparatorMenuItem());
      fileMenu.add(getDeleteMenuItem());
      fileMenu.addBeforeShowHandler(getBeforeShowHandler());
    }
    return fileMenu;
  }

  protected FileManagerPresenter getPresenter() {
    return fileSystemPresenter;
  }

  private BeforeShowHandler getBeforeShowHandler() {
    if (beforeShowHandler == null) {
      beforeShowHandler = new BeforeShowHandler() {
        @Override
        public void onBeforeShow(BeforeShowEvent event) {
          boolean isEnableCreate = getPresenter().isEnableCreate();
          boolean isEnableOpen = getPresenter().isEnableOpen();
          boolean isEnableDelete = getPresenter().isEnableDelete();
          boolean isEnableEditName = getPresenter().isEnableEditName();
          getCreateFolderMenuItem().setEnabled(isEnableCreate);
          getCreateHiveMenuItem().setEnabled(isEnableCreate);
          getCreateShellMenuItem().setEnabled(isEnableCreate);
          getCreateTextMenuItem().setEnabled(isEnableCreate);
          getExpandMenuItem().setEnabled(isEnableCreate);
          getCollapseMenuItem().setEnabled(isEnableCreate);
          getEditNameMenuItem().setEnabled(isEnableEditName);
          getOpenMenuItem().setEnabled(isEnableOpen);
          getDeleteMenuItem().setEnabled(isEnableDelete);
        }
      };
    }
    return beforeShowHandler;
  }

  private MenuItem getCollapseMenuItem() {
    if (collapseMenuItem == null) {
      collapseMenuItem = new MenuItem("收缩", getCollapseSelectionHandler());
      collapseMenuItem.setIcon(Images.getImageResources().arrow_in());
    }
    return collapseMenuItem;
  }

  private SelectionHandler<MenuItem> getCollapseSelectionHandler() {
    if (collapseSelectionHandler == null) {
      collapseSelectionHandler = new SelectionHandler<MenuItem>() {
        @Override
        public void onSelection(SelectionEvent<MenuItem> event) {
          getPresenter().onCollapse();
        }
      };
    }
    return collapseSelectionHandler;
  }

  private MenuItem getCreateShellMenuItem() {
    if (createShellMenuItem == null) {
    	createShellMenuItem = new MenuItem("新建shell", getCreateSelectionHandler());
    	createShellMenuItem.setIcon(Images.getImageResources().script_add());
    	createShellMenuItem.setData(FILE_TYPE, false);
    	createShellMenuItem.setData(FILE_SUFFIX, "sh");
    }
    return createShellMenuItem;
  }

  private MenuItem getCreateHiveMenuItem() {
    if (createHiveMenuItem == null) {
    	createHiveMenuItem = new MenuItem("新建Hive", getCreateSelectionHandler());
    	createHiveMenuItem.setIcon(Images.getImageResources().script_add());
    	createHiveMenuItem.setData(FILE_TYPE, false);
    	createHiveMenuItem.setData(FILE_SUFFIX, "hive");
    }
    return createHiveMenuItem;
  }
  private MenuItem getCreateTextMenuItem() {
		if(createTextMenuItem==null){
			createTextMenuItem=new MenuItem("新建文件",getCreateSelectionHandler());
			createTextMenuItem.setIcon(Images.getImageResources().page_white_add());
			createTextMenuItem.setData(FILE_TYPE, false);
			createTextMenuItem.setData(FILE_SUFFIX, "");
		}
		return createTextMenuItem;
	}
  private MenuItem getCreateFolderMenuItem() {
    if (createFolderMenuItem == null) {
      createFolderMenuItem = new MenuItem("新建文件夹", getCreateSelectionHandler());
      createFolderMenuItem.setIcon(Images.getImageResources().folder_add());
      createFolderMenuItem.setData(FILE_TYPE, true);
      createFolderMenuItem.setData(FILE_SUFFIX, "");
    }
    return createFolderMenuItem;
  }


  private SelectionHandler<MenuItem> getCreateSelectionHandler() {
    if (createSelectionHandler == null) {
      createSelectionHandler = new SelectionHandler<MenuItem>() {
        @Override
        public void onSelection(SelectionEvent<MenuItem> event) {
        	Boolean folder = event.getSelectedItem().<Boolean> getData(FILE_TYPE);
          String suffix=event.getSelectedItem().getData(FILE_SUFFIX);
          getPresenter().onCreate(folder,suffix);
        }
      };
    }
    return createSelectionHandler;
  }


  private MenuItem getDeleteMenuItem() {
    if (deleteMenuItem == null) {
      deleteMenuItem = new MenuItem("删除", getDeleteSelectionHandler());
      deleteMenuItem.setIcon(Images.getImageResources().cross());
    }
    return deleteMenuItem;
  }

  private SelectionHandler<MenuItem> getDeleteSelectionHandler() {
    if (deleteSelectionHandler == null) {
      deleteSelectionHandler = new SelectionHandler<MenuItem>() {
        @Override
        public void onSelection(SelectionEvent<MenuItem> event) {
          getPresenter().onDelete();
        }
      };
    }
    return deleteSelectionHandler;
  }

  private MenuItem getEditNameMenuItem() {
    if (editNameMenuItem == null) {
      editNameMenuItem = new MenuItem("重命名", getEditNameSelectionHandler());
      editNameMenuItem.setIcon(Images.getImageResources().textfield_rename());
    }
    return editNameMenuItem;
  }

  private SelectionHandler<MenuItem> getEditNameSelectionHandler() {
    if (editNameSelectionHandler == null) {
      editNameSelectionHandler = new SelectionHandler<MenuItem>() {
        @Override
        public void onSelection(SelectionEvent<MenuItem> event) {
          getPresenter().onEditName();
        }
      };
    }
    return editNameSelectionHandler;
  }

  private MenuItem getExpandMenuItem() {
    if (expandMenuItem == null) {
      expandMenuItem = new MenuItem("展开", getExpandSelectionHandler());
      expandMenuItem.setIcon(Images.getImageResources().arrow_out());
    }
    return expandMenuItem;
  }

  private SelectionHandler<MenuItem> getExpandSelectionHandler() {
    if (expandSelectionHandler == null) {
      expandSelectionHandler = new SelectionHandler<MenuItem>() {
        @Override
        public void onSelection(SelectionEvent<MenuItem> event) {
          getPresenter().onExpand();
        }
      };
    }
    return expandSelectionHandler;
  }

  private MenuItem getOpenMenuItem() {
    if (openMenuItem == null) {
      openMenuItem = new MenuItem("打开", getOpenSelectionHandler());
      openMenuItem.setIcon(Images.getImageResources().bullet_go());
    }
    return openMenuItem;
  }

  private SelectionHandler<MenuItem> getOpenSelectionHandler() {
    if (openSelectionHandler == null) {
      openSelectionHandler = new SelectionHandler<MenuItem>() {
        @Override
        public void onSelection(SelectionEvent<MenuItem> event) {
          getPresenter().onOpen();
        }
      };
    }
    return openSelectionHandler;
  }



}
