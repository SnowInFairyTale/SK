package org.test;

import java.util.ArrayList;

import loon.core.geom.Vector2f;

public class LevelSettings {

	public LevelSettings(int level) {

		this.setTowerBlockingGridCells(new java.util.ArrayList<Vector2f>());
		this.setLevelSpecificOccupiedGridCells(new java.util.ArrayList<Vector2f>());
		switch (level) {
		case 1:
			this.setStartPoint(new Vector2f(0, 9));
			this.setEndPoint(new Vector2f(0x11, 9));
			this.setBackgroundTextureFile("assets/background.png");
			this.setBackgroundWithGridTextureFile("assets/background_grid.png");
			return;

		case 2: {
			this.setStartPoint(new Vector2f(0, 15));
			this.setEndPoint(new Vector2f(0x11, 15));
			this.setBackgroundTextureFile("assets/background2.png");
			this.setBackgroundWithGridTextureFile("assets/background2_grid.png");
			this.getLevelSpecificOccupiedGridCells().add(new Vector2f(9, 4));
			this.getLevelSpecificOccupiedGridCells().add(new Vector2f(6, 11));
			return;
		}
		case 3: {
			this.setStartPoint(new Vector2f(0, 15));
			this.setEndPoint(new Vector2f(0x11, 4));
			this.setBackgroundTextureFile("assets/background3.png");
			this.setBackgroundWithGridTextureFile("assets/background3_grid.png");
			this.getLevelSpecificOccupiedGridCells().add(new Vector2f(3, 9));
			this.getLevelSpecificOccupiedGridCells().add(new Vector2f(7, 15));
			this.getTowerBlockingGridCells().add(new Vector2f(7, 7));
			this.getTowerBlockingGridCells().add(new Vector2f(7, 8));
			this.getTowerBlockingGridCells().add(new Vector2f(3, 13));
			this.getTowerBlockingGridCells().add(new Vector2f(3, 14));
			this.getTowerBlockingGridCells().add(new Vector2f(4, 13));
			this.getTowerBlockingGridCells().add(new Vector2f(4, 14));
			return;
		}
		}
	}

	private String privateBackgroundTextureFile;

	public final String getBackgroundTextureFile() {
		return privateBackgroundTextureFile;
	}

	public final void setBackgroundTextureFile(String value) {
		privateBackgroundTextureFile = value;
	}

	private String privateBackgroundWithGridTextureFile;

	public final String getBackgroundWithGridTextureFile() {
		return privateBackgroundWithGridTextureFile;
	}

	public final void setBackgroundWithGridTextureFile(String value) {
		privateBackgroundWithGridTextureFile = value;
	}

	private Vector2f privateEndPoint;

	public final Vector2f getEndPoint() {
		return privateEndPoint;
	}

	public final void setEndPoint(Vector2f value) {
		privateEndPoint = value;
	}

	private java.util.ArrayList<Vector2f> privateLevelSpecificOccupiedGridCells = new ArrayList<Vector2f>();;

	public final java.util.ArrayList<Vector2f> getLevelSpecificOccupiedGridCells() {
		return privateLevelSpecificOccupiedGridCells;
	}

	public final void setLevelSpecificOccupiedGridCells(
			java.util.ArrayList<Vector2f> value) {
		privateLevelSpecificOccupiedGridCells = value;
	}

	private Vector2f privateStartPoint;

	public final Vector2f getStartPoint() {
		return privateStartPoint;
	}

	public final void setStartPoint(Vector2f value) {
		privateStartPoint = value;
	}

	private java.util.ArrayList<Vector2f> privateTowerBlockingGridCells = new ArrayList<Vector2f>();;

	public final java.util.ArrayList<Vector2f> getTowerBlockingGridCells() {
		return privateTowerBlockingGridCells;
	}

	public final void setTowerBlockingGridCells(
			java.util.ArrayList<Vector2f> value) {
		privateTowerBlockingGridCells = value;
	}
}
