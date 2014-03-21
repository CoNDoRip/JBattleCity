package ua.pp.condor.jbattlecity.area;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.MediaTracker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.Timer;

import ua.pp.condor.jbattlecity.area.maps.IMap;
import ua.pp.condor.jbattlecity.tank.ProjectileState;
import ua.pp.condor.jbattlecity.tank.TankState;
import ua.pp.condor.jbattlecity.utils.Images;

public class Area extends JPanel {

	private static final long serialVersionUID = -2993932675117489481L;
	
	private MapState mapState;
	
	private int currentBang = 0;
	
	public Area(IMap map) {
		mapState = new MapState(map);
		
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		
        new Timer(10, new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				repaint();
			}
		}).start();
        
        MediaTracker mt = new MediaTracker(this);
        mt.addImage(mapState.getMapImage(), 1);
        mt.addImage(Images.getYouUp(), 2);
        mt.addImage(Images.getYouRight(), 2);
        mt.addImage(Images.getYouDown(), 2);
        mt.addImage(Images.getYouLeft(), 2);
        mt.addImage(Images.getEnemyUp(), 3);
        mt.addImage(Images.getEnemyRight(), 3);
        mt.addImage(Images.getEnemyDown(), 3);
        mt.addImage(Images.getEnemyLeft(), 3);
        mt.addImage(Images.getProjectile(), 4);
        mt.addImage(Images.getBang(0), 5);
        mt.addImage(Images.getBang(1), 5);
        mt.addImage(Images.getBang(2), 5);
        mt.addImage(Images.getBang(3), 5);
        mt.addImage(Images.getGameOver(), 9);
        
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
        
        TankState you = mapState.getYou();
        if (you.getOrientation() != null) {
            switch (you.getOrientation()) {
        		case UP:    g.drawImage(Images.getYouUp(),    you.getX(), you.getY(), this); break;
            	case RIGHT: g.drawImage(Images.getYouRight(), you.getX(), you.getY(), this); break;
            	case DOWN:  g.drawImage(Images.getYouDown(),  you.getX(), you.getY(), this); break;
            	case LEFT:  g.drawImage(Images.getYouLeft(),  you.getX(), you.getY(), this); break;
            	default:    
            }
        } else {
        	g.drawImage(Images.getBang(currentBang), you.getX(), you.getY(), this);
            if (currentBang < 4) currentBang++;
            else currentBang = 0;
        }
        
        for (TankState enemy : mapState.getEnemies()) {
        	switch (enemy.getOrientation()) {
	    		case UP:    g.drawImage(Images.getEnemyUp(),    enemy.getX(), enemy.getY(), this); break;
	        	case RIGHT: g.drawImage(Images.getEnemyRight(), enemy.getX(), enemy.getY(), this); break;
	        	case DOWN:  g.drawImage(Images.getEnemyDown(),  enemy.getX(), enemy.getY(), this); break;
	        	case LEFT:  g.drawImage(Images.getEnemyLeft(),  enemy.getX(), enemy.getY(), this); break;
        	}
        }
        
        for (ProjectileState ps : mapState.getProjectiles()) {
        	g.drawImage(Images.getProjectile(), ps.getX() - Images.PROJECTILE_SIZE, ps.getY() - Images.PROJECTILE_SIZE, this);
        }

		if (mapState.isGameOver())
	        g.drawImage(Images.getGameOver(), 110, 160, this);
    }
	
}
