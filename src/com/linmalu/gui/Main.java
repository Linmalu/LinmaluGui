package com.linmalu.gui;

import com.linmalu.library.api.LinmaluMain;

public class Main extends LinmaluMain
{
	@Override
	public void onEnable()
	{
		super.onEnable();
		registerCommand(new Main_Command());
		registerEvents(new Main_Event());
		LinmaluConfigManager.getInstance();
	}
}
