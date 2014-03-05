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

public class MapState implements IMap {
	
	public static final int BLOCKS_COUNT = 13;
	public static final int BLOCK_SIZE = 4;
	public static final int BLOCK_SIZE_PIXEL = 40;
	
	public static final int ARRAY_SIZE = BLOCKS_COUNT * BLOCK_SIZE;
	
	private static Cell[][] currentMap = new Cell[ARRAY_SIZE][ARRAY_SIZE];
	
	private IMap map;
	
	private TankState you;
	
	private List<ProjectileState> projectiles;
	
	public MapState(IMap map) {
		this.map = map;
		for (int x = 0; x < ARRAY_SIZE; x++) {
			for (int y = 0; y < ARRAY_SIZE; y++) {
				currentMap[x][y] = map.getCell(x, y);
			}
		}
		you = new TankState(160, 480, Orientation.UP);
		projectiles = new CopyOnWriteArrayList<ProjectileState>();
		
		Timer t = new Timer();
		t.schedule(new TimerTask() {
			
			@Override
			public void run() {
				int delta = 4;
				
				for (int i = 0; i < projectiles.size(); i++) {
					ProjectileState ps = projectiles.get(i);
					if (ps.getX() < 0 || ps.getX() > JBattleCity.WIDTH
					 || ps.getY() < 0 || ps.getY() > JBattleCity.HEIGHT) {
						projectiles.remove(i);
						continue;
					}
					
					switch (ps.getOrientation()) {
			    		case UP:    ps.setY(ps.getY() - delta); break;
			        	case RIGHT: ps.setX(ps.getX() + delta); break;
			        	case DOWN:  ps.setY(ps.getY() + delta); break;
			        	case LEFT:  ps.setX(ps.getX() - delta); break;
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

}
