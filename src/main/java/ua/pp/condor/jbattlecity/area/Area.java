package ua.pp.condor.jbattlecity.area;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.MediaTracker;
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
	
	public Area(IMap map) {
		mapState = new MapState(map);
		
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		
        new Timer(10, new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				repaint();
			}
		}).start();
        
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
        	.addKeyEventDispatcher(new KeyEventDispatcher() {
			
			public boolean dispatchKeyEvent(KeyEvent arg0) {
				final int delta = 10;
				
				int tankX = mapState.getTankX();
				int tankY = mapState.getTankY();
				
				int oldXCell =  tankX / 10;
				int incXCell = (tankX + delta) / 10;
				int decXCell = (tankX - delta) / 10;
				int oldYCell =  tankY / 10;
				int incYCell = (tankY + delta) / 10;
				int decYCell = (tankY - delta) / 10;
				
				switch(arg0.getKeyCode()) {
					case KeyEvent.VK_UP: {
						mapState.setTankOrientation(Orientation.UP);
						if (tankY - delta >= 0
								&& mapState.getCell(oldXCell, decYCell) == Cell.empty
								&& mapState.getCell(oldXCell + 3, decYCell) == Cell.empty) {
							tankY -= delta;
						}
						break;
					}
					case KeyEvent.VK_LEFT: {
						mapState.setTankOrientation(Orientation.LEFT);
						if (tankX - delta >= 0
								&& mapState.getCell(decXCell, oldYCell) == Cell.empty
								&& mapState.getCell(decXCell, oldYCell + 3) == Cell.empty) {
							tankX -= delta;
						}
						break;
					}
					case KeyEvent.VK_DOWN: {
						mapState.setTankOrientation(Orientation.DOWN);
						if (tankY + delta <= JBattleCity.WIDTH - 40
								&& mapState.getCell(oldXCell, incYCell + 3) == Cell.empty
								&& mapState.getCell(oldXCell + 3, incYCell + 3) == Cell.empty) {
							tankY += delta;
						}
						break;
					}
					case KeyEvent.VK_RIGHT: {
						mapState.setTankOrientation(Orientation.RIGHT);
						if (tankX + delta <= JBattleCity.HEIGHT - 40
								&& mapState.getCell(incXCell + 3, oldYCell) == Cell.empty
								&& mapState.getCell(incXCell + 3, oldYCell + 3) == Cell.empty) {
							tankX += delta;
						}
						break;
					}
				}
				
				mapState.setTankX(tankX);
				mapState.setTankY(tankY);
				
				return false;
			}
		});
        
        MediaTracker mt = new MediaTracker(this);
        mt.addImage(mapState.getMapImage(), 1);
        mt.addImage(Images.getYouUp(), 2);
        mt.addImage(Images.getYouRight(), 2);
        mt.addImage(Images.getYouDown(), 2);
        mt.addImage(Images.getYouLeft(), 2);
        
        try {
			mt.waitForAll();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
        if (mt.isErrorAny())
        	throw new IllegalStateException("Errors in images loading");
	}

	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(mapState.getMapImage(), 0, 0, this);

        g.setColor(Color.BLACK);
        for (int x = 0; x < MapState.ARRAY_SIZE; x++) {
			for (int y = 0; y < MapState.ARRAY_SIZE; y++) {
				if (mapState.getCell(x, y) == Cell.empty)
					g.fillRect(x * 10, y * 10, 10, 10);
			}
		}
        
        switch(mapState.getTankOrientation()) {
    		case UP:    g.drawImage(Images.getYouUp(),    mapState.getTankX(), mapState.getTankY(), this); break;
        	case RIGHT: g.drawImage(Images.getYouRight(), mapState.getTankX(), mapState.getTankY(), this); break;
        	case DOWN:  g.drawImage(Images.getYouDown(),  mapState.getTankX(), mapState.getTankY(), this); break;
        	case LEFT:  g.drawImage(Images.getYouLeft(),  mapState.getTankX(), mapState.getTankY(), this); break;
        }
    }
	
}
