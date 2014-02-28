package ua.pp.condor.jbattlecity.area;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.Timer;

import ua.pp.condor.jbattlecity.JBattleCity;
import ua.pp.condor.jbattlecity.area.maps.IMap;
import ua.pp.condor.jbattlecity.utils.Images;

public class Area extends JPanel {

	private static final long serialVersionUID = -2993932675117489481L;
	
	private MapState mapState;
	
	private int tankX = 160;
	private int tankY = 480;
	
	private Orientation or = Orientation.UP;
	
	private Image youUp;
	private Image youRight;
	private Image youDown;
	private Image youLeft;
	
	public enum Orientation {
		UP,
		RIGHT,
		DOWN,
		LEFT;
	}
	
	public Area(IMap map) {
		mapState = new MapState();
		mapState.init(map);
		
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		
        new Timer(0, new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				repaint();
			}
		}).start();
        
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
        	.addKeyEventDispatcher(new KeyEventDispatcher() {
			
			public boolean dispatchKeyEvent(KeyEvent arg0) {
				int d = 10;
				
				int oldXCell =  tankX / 10;
				int incXCell = (tankX + d) / 10;
				int decXCell = (tankX - d) / 10;
				int oldYCell =  tankY / 10;
				int incYCell = (tankY + d) / 10;
				int decYCell = (tankY - d) / 10;
				
				switch(arg0.getKeyCode()) {
					case KeyEvent.VK_UP: {
						or = Orientation.UP;
						if (tankY - d >= 0
								&& mapState.getCell(oldXCell, decYCell) == Cell.empty
								&& mapState.getCell(oldXCell + 3, decYCell) == Cell.empty) {
							tankY -= d;
						}
						break;
					}
					case KeyEvent.VK_LEFT: {
						or = Orientation.LEFT;
						if (tankX - d >= 0
								&& mapState.getCell(decXCell, oldYCell) == Cell.empty
								&& mapState.getCell(decXCell, oldYCell + 3) == Cell.empty) {
							tankX -= d;
						}
						break;
					}
					case KeyEvent.VK_DOWN: {
						or = Orientation.DOWN;
						if (tankY + d <= JBattleCity.WIDTH - 40
								&& mapState.getCell(oldXCell, incYCell + 3) == Cell.empty
								&& mapState.getCell(oldXCell + 3, incYCell + 3) == Cell.empty) {
							tankY += d;
						}
						break;
					}
					case KeyEvent.VK_RIGHT: {
						or = Orientation.RIGHT;
						if (tankX + d <= JBattleCity.HEIGHT - 40
								&& mapState.getCell(incXCell + 3, oldYCell) == Cell.empty
								&& mapState.getCell(incXCell + 3, oldYCell + 3) == Cell.empty) {
							tankX += d;
						}
						break;
					}
				}
				return false;
			}
		});
        
        youUp = Images.getImage(Images.YOU_UP);
        youRight = Images.getImage(Images.YOU_RIGHT);
        youDown = Images.getImage(Images.YOU_DOWN);
        youLeft = Images.getImage(Images.YOU_LEFT);
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
        
        switch(or) {
        	case DOWN: g.drawImage(youDown, tankX, tankY, this); break;
        	case LEFT: g.drawImage(youLeft, tankX, tankY, this); break;
        	case RIGHT: g.drawImage(youRight, tankX, tankY, this); break;
        	case UP: g.drawImage(youUp, tankX, tankY, this); break;
        }
    }
	
}
