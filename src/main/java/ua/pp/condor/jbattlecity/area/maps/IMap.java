package ua.pp.condor.jbattlecity.area.maps;

import java.awt.Image;

import ua.pp.condor.jbattlecity.area.Cell;

public interface IMap {
	
	Cell getCell(int x, int y);
	
	Image getMapImage();

}
