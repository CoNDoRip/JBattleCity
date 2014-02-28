package ua.pp.condor.jbattlecity.area;

import java.awt.Image;

import ua.pp.condor.jbattlecity.area.maps.IMap;

public class MapState implements IMap {
	
	public static final int BLOCKS_COUNT = 13;
	public static final int BLOCK_SIZE = 4;
	public static final int BLOCK_SIZE_PIXEL = 40;
	
	public static final int ARRAY_SIZE = BLOCKS_COUNT * BLOCK_SIZE;
	
	private static Cell[][] currentMap = new Cell[ARRAY_SIZE][ARRAY_SIZE];
	
	private IMap map;
	
	public void init(IMap map) {
		this.map = map;
		for (int x = 0; x < ARRAY_SIZE; x++) {
			for (int y = 0; y < ARRAY_SIZE; y++) {
				currentMap[x][y] = map.getCell(x, y);
			}
		}
	}

	public Cell getCell(int x, int y) {
		return currentMap[x][y];
	}
	
	public Image getMapImage() {
		return map.getMapImage();
	}

}
