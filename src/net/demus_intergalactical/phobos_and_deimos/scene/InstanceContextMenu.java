package net.demus_intergalactical.phobos_and_deimos.scene;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.BorderPane;
import net.demus_intergalactical.phobos_and_deimos.main.InstanceContainer;
import net.demus_intergalactical.phobos_and_deimos.main.InstancePool;
import net.demus_intergalactical.phobos_and_deimos.main.UIController;
import net.demus_intergalactical.serverman.Globals;
import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;


public class InstanceContextMenu extends ContextMenu {
	private InstanceContainer ic = null;
	
	private MenuItem miStart;
	private MenuItem miStop;
	private SeparatorMenuItem separatorMenuItem1;
	private SeparatorMenuItem separatorMenuItem2;
	private MenuItem miMoveup;
	private MenuItem miMoveDown;
	private MenuItem miOpenFolder;
	private MenuItem miDeleteInstance;
	
	public InstanceContextMenu(InstanceContainer ic, BorderPane serverContainer) {
		super();
		this.ic = ic;
		
		this.setId("instanceContextMenu");
		miStart = new MenuItem("Start server");
		miStart.setOnAction(e -> ic.getInstance().run());
		miStop = new MenuItem("Stop server");
		miStop.setOnAction(e -> ic.getInstance().stop());
		separatorMenuItem1 = new SeparatorMenuItem();
		separatorMenuItem2 = new SeparatorMenuItem();
		miMoveup = new MenuItem("Move up");
		miMoveup.setOnAction(e -> UIController.swapInstances(serverContainer, true));
		miMoveDown = new MenuItem("Move down");
		miMoveDown.setOnAction(e -> UIController.swapInstances(serverContainer, false));
		miOpenFolder = new MenuItem("Open folder");
		miOpenFolder.setOnAction(e -> {
			try {
				Desktop desktop = Desktop.getDesktop();
				desktop.open(new File(Globals.getServerManConfig().get("instances_home") + File.separator + ic.getInstance().getServerInstanceID()));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		miDeleteInstance = new MenuItem("Delete instance");
		miDeleteInstance.setOnAction(e -> {
			ConfirmWindow cw = new ConfirmWindow("Delete instance", "Are you sure you want to delete this instance?\nAll worlds, configs, etc. will be\ndeleted forever (a long time!)");
			cw.setWidth(500);

			if (!cw.waitAndGetResult()) {
				return;
			}
			UIController.removeServer(serverContainer);
			try {
				FileUtils.deleteDirectory(new File(Globals.getServerManConfig().get("instances_home") + File.separator + ic.getInstance().getServerInstanceID()));
			} catch (IOException ex) {
				System.err.println(ex.getMessage() + "Is the server still running?");
			}
		});
		
		this.getItems().addAll(miStart, miStop, separatorMenuItem1, miMoveup, miMoveDown, separatorMenuItem2, miOpenFolder, miDeleteInstance);
	}
}
