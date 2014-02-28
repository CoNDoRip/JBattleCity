package ua.pp.condor.jbattlecity.area;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import ua.pp.condor.jbattlecity.area.maps.IMap;

public class Area extends JPanel {

	private static final long serialVersionUID = -2993932675117489481L;
	
	private MapState mapState;
	
	public Area(IMap map) {
		mapState = new MapState();
		mapState.init(map);
	}

	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(mapState.getMapImage(), 0, 0, this);

        g.setColor(Color.BLACK);
        for (int x = 0; x < MapState.ARRAY_SIZE; x++) {
			for (int y = 0; y < MapState.ARRAY_SIZE; y++) {
				Cell cell = mapState.getCell(x, y);
				if (cell == Cell.empty)
					g.fillRect(x * 10, y * 10, 10, 10);
			}
		}
    }
	
}
