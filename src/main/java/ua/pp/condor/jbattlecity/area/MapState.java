package ua.pp.condor.jbattlecity.area;

import java.awt.Image;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import ua.pp.condor.jbattlecity.JBattleCity;
import ua.pp.condor.jbattlecity.area.maps.IMap;
import ua.pp.condor.jbattlecity.tank.Orientation;
import ua.pp.condor.jbattlecity.tank.ProjectileState;
import ua.pp.condor.jbattlecity.tank.TankState;
import ua.pp.condor.jbattlecity.utils.Images;

public class MapState implements IMap {
	
	public static final int BLOCKS_COUNT = 13;
	public static final int BLOCK_SIZE = 4;
	public static final int BLOCK_SIZE_PIXEL = 40;
	public static final int HALF_BLOCK_SIZE_PIXEL = 20;
	
	public static final int ARRAY_SIZE = BLOCKS_COUNT * BLOCK_SIZE;
	
	private static Cell[][] currentMap = new Cell[ARRAY_SIZE][ARRAY_SIZE];
	
	private IMap map;
	
	private TankState you;
	
	private List<ProjectileState> projectiles;
	
	private Timer projectilesTimer;
	
	private boolean gameOver;
	
	public MapState(IMap map) {
		this.map = map;
		for (int x = 0; x < ARRAY_SIZE; x++) {
			for (int y = 0; y < ARRAY_SIZE; y++) {
				currentMap[x][y] = map.getCell(x, y);
			}
		}
		you = new TankState(160, 480, Orientation.UP);
		projectiles = new CopyOnWriteArrayList<ProjectileState>();
		
		Timer projectilesTimer = new Timer();
		projectilesTimer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				int delta = 5;
				
				for (int i = 0; i < projectiles.size(); i++) {
					ProjectileState ps = projectiles.get(i);
					if (ps.getX() < Images.PROJECTILE_SIZE || ps.getX() > JBattleCity.WIDTH  - Images.PROJECTILE_SIZE - delta
					 || ps.getY() < Images.PROJECTILE_SIZE || ps.getY() > JBattleCity.HEIGHT - Images.PROJECTILE_SIZE - delta) {
						ps.getParent().setHasProjectile(false);
						projectiles.remove(i);
						continue;
					}
					
					int x = 0, y = 0;
					int x1 = 0, y1 = 0;
					int x2 = 0, y2 = 0;
					int x3 = 0, y3 = 0;
					switch (ps.getOrientation()) {
			    		case UP: {
			    			int newY = ps.getY() - delta;
			    			x = ps.getX() / 10; y = newY / 10;
			    			x1 = x - 1; y1 = y;
			    			x2 = x - 2; y2 = y;
			    			x3 = x + 1; y3 = y;
			    			ps.setY(newY);
			    			break;
			    		}
			        	case RIGHT: {
			    			int newX = ps.getX() + delta;
			    			x = newX / 10; y = ps.getY() / 10;
			    			x1 = x; y1 = y - 1;
			    			x2 = x; y2 = y - 2;
			    			x3 = x; y3 = y + 1;
			        		ps.setX(newX);
			        		break;
			        	}
			        	case DOWN: {
			    			int newY = ps.getY() + delta;
			    			x = ps.getX() / 10; y = newY / 10;
			    			x1 = x - 1; y1 = y;
			    			x2 = x - 2; y2 = y;
			    			x3 = x + 1; y3 = y;
			        		ps.setY(newY);
			        		break;
			        	}
			        	case LEFT: {
			    			int newX = ps.getX() - delta;
			    			x = newX / 10; y = ps.getY() / 10;
			    			x1 = x; y1 = y - 1;
			    			x2 = x; y2 = y - 2;
			    			x3 = x; y3 = y + 1;
			        		ps.setX(newX);
			        		break;
			        	}
					}
					boolean destroyed = false;
	    			if (getCell(x, y) != Cell.empty || getCell(x1, y1) != Cell.empty) {
						ps.getParent().setHasProjectile(false);
	    				projectiles.remove(i);
	    			}
	    			if (getCell(x, y) == Cell.base || getCell(x1, y1) == Cell.base) {
	    				setGameOver(true);
	    			}
	    			if (getCell(x, y) == Cell.wall) {
	    				currentMap[x][y] = Cell.empty;
	    				destroyed = true;
	    			}
	    			if (getCell(x1, y1) == Cell.wall) {
	    				currentMap[x1][y1] = Cell.empty;
	    				destroyed = true;
	    			}
	    			if (destroyed) {
	    				if (getCell(x2, y2) == Cell.wall)
	    					currentMap[x2][y2] = Cell.empty;
	    				if (getCell(x3, y3) == Cell.wall)
	    					currentMap[x3][y3] = Cell.empty;
	    			}
				}
			}
		}, 0, 15);
	}

	public Cell getCell(int x, int y) {
		return currentMap[x][y];
	}
	
	public Image getMapImage() {
		return map.getMapImage();
	}

	public TankState getYou() {
		return you;
	}

	public void setYou(TankState you) {
		this.you = you;
	}

	public List<ProjectileState> getProjectiles() {
		return projectiles;
	}

	public void setProjectiles(List<ProjectileState> projectiles) {
		this.projectiles = projectiles;
	}
	
	public void addProjectile(ProjectileState projectile) {
		projectiles.add(projectile);
	}

	public boolean isGameOver() {
		return gameOver;
	}

	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
		projectilesTimer.cancel();
	}

}
