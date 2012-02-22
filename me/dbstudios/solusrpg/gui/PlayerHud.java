package me.dbstudios.solusrpg.gui;

import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.gui.WidgetAnchor;

import me.dbstudios.solusrpg.SolusRpg;
import me.dbstudios.solusrpg.sys.RpgPlayer;

public class PlayerHud {
	private RpgPlayer player;
	private SolusRpg common;
	private Widget[] widgets = new Widget[3];
	
	public PlayerHud(RpgPlayer player) {
		this.player = player;
		this.common = player.getParentPlugin();
		this.init();
	}
	
	private void init() {
		GenericLabel label = new GenericLabel();
		
		label.setVisible(false);
		label.setAuto(false);
		label.setAnchor(WidgetAnchor.TOP_RIGHT);
		
		label.setText("Class: " + player.getClassName());
		label.setWidth(GenericLabel.getStringWidth(label.getText()));
		label.setHeight(GenericLabel.getStringHeight(label.getText()));
		label.setX(-150);
		label.setY(5);
		
		widgets[0] = label.copy();
		
		label.setText("Health: " + player.getHealth() + "/" + player.getMaxHealth());
		label.setWidth(GenericLabel.getStringWidth(label.getText()));
		label.setHeight(GenericLabel.getStringHeight(label.getText()));
		label.setX(-150);
		label.setY(10 + label.getHeight());
		
		widgets[1] = label.copy();
		
		label.setText("Effects: " + player.getEffects());
		label.setWidth(GenericLabel.getStringWidth(label.getText()));
		label.setHeight(GenericLabel.getStringHeight(label.getText()));
		label.setX(-150);
		label.setY(15 + (label.getHeight() * 2));
		
		widgets[2] = label.copy();
	}
	
	public void attatch() {
		for (Widget widget : widgets) {
			if (widget != null) {
				player.getMainScreen().attachWidget(common, widget);
			}
		}
	}
	
	public void detatch() {
		for (Widget widget : widgets) {
			if (widget != null) {
				player.getMainScreen().removeWidget(widget);
			}
		}
	}
	
	public void show() {
		for (Widget widget : widgets) {
			if (widget != null) {
				widget.setVisible(true);
			}
		}
	}
	
	public void hide() {
		for (Widget widget : widgets) {
			if (widget != null) {
				widget.setVisible(false);
			}
		}
	}
	
	public void refresh() {
		boolean wasVisible = widgets[0] != null ? widgets[0].isVisible() : false;
		
		this.detatch();
		
		for (int i = 0; i <= widgets.length - 1; i++) {
			widgets[i] = null;
		}
		
		this.init();
		
		if (wasVisible) {
			this.show();
		} else {
			this.hide();
		}
		
		this.attatch();
	}
}