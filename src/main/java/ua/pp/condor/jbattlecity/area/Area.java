package ua.pp.condor.jbattlecity.area;

import java.awt.Graphics;

import javax.swing.JPanel;

import ua.pp.condor.jbattlecity.area.maps.Stage1;

public class Area extends JPanel {

	private static final long serialVersionUID = -2993932675117489481L;

	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        
        MapState mapState = new MapState();
        mapState.init(new Stage1());
        
        for (int x = 0; x < MapState.ARRAY_SIZE; x++) {
			for (int y = 0; y < MapState.ARRAY_SIZE; y++) {
		        g.setColor(mapState.getCell(x, y).getColor());
				g.fillRect(x * 10, y * 10, 10, 10);
			}
		}
        
    }
	
}
