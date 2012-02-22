package me.dbstudios.solusrpg.gui;

public class StatMeter {
	private RpgPlayer player;
	private Widget[] widgets = new Widget[1];

	/*
		widgets[0]  - Health meter
	*/

	public StatMeter(RpgPlayer player) {
		this.player = player;

		this.init();
	}

	private init() {
		GenericLabel label = new GenericLabel();

		label.setAuto(false);
		label.setAutoDirty(true);
		label.setVisible(true);

		label.setX(player.getMainScreen.getHealthBar().getY());
		label.setY(player.getMainScreen().getHealthBar().getY());
		label.setAnchor(player.getMainScreen().getHealthBar().getAnchor());
		label.setAlign(WidgetAnchor.TOP_RIGHT);
		label.setText(player.getHealth() + " / " + player.getMaxHealth());
		label.setHeight(12);
		label.setWidth(100);

		player.getMainScreen().getHealthBar().setVisible(false);
		player.getMainScreen().attatchWidget(player.getParentPlugin(), label);
	}

	public void update() {
		widget[0].setText(player.getHealth() + " / " + player.getMaxHealth());
	}
}