package ua.pp.condor.jbattlecity.area;

import java.awt.Color;

public enum Cell {
	
	empty(Color.BLACK),
	wall(Color.RED),
	conc(Color.WHITE), //concrete
	base(Color.GRAY),
	tank(Color.GREEN);
	
	private Color color;
	
	private Cell(Color color) {
		this.color = color;
	}
	
	public Color getColor() {
		return color;
	}
	
}
