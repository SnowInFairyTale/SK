package org.test;

public enum ScreenType
{
	ConfirmScreen,
	GamePausedScreen,
	GameplayScreen,
	InstructionsScreen,
	LoadingScreen,
	LoseScreen,
	MainMenuScreen,
	MonsterInfoScreen,
	SelectLevelScreen,
	TowerInfoScreen,
	WinScreen;

	public int getValue()
	{
		return this.ordinal();
	}

	public static ScreenType forValue(int value)
	{
		return values()[value];
	}
}