package ua.pp.condor.jbattlecity.area;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import ua.pp.condor.jbattlecity.area.maps.Stage1;

public class Area extends JPanel {

	private static final long serialVersionUID = -2993932675117489481L;

	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        MapState mapState = new MapState();
        mapState.init(new Stage1());

        Image bricks = null;
		try {
			bricks = ImageIO.read(getClass().getResourceAsStream(Stage1.IMG));
		} catch (IOException e) {
			e.printStackTrace();
		}
        g.drawImage(bricks, 0, 0, this);

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
