package ua.pp.condor.jbattlecity.area;

import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

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
	
	private KeyEventDispatcher yourKeyEventsDispatcher;
	
	private Map<Integer, TankState> enemies;
	
	private int enemyId = 0;
	
	private Timer enemiesTimer;
	
	private Map<Integer, ProjectileState> projectiles;
	
	private int projectileId = 0;
	
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
		setTankBlock(16, 48);
		enemies = new ConcurrentHashMap<Integer, TankState>();
		projectiles = new ConcurrentHashMap<Integer, ProjectileState>();
		
		yourKeyEventsDispatcher = new KeyEventDispatcher() {
			
			public boolean dispatchKeyEvent(KeyEvent arg0) {
				if (arg0.getID() == KeyEvent.KEY_PRESSED) {
					final int delta = 10;
					
					TankState you = getYou();
					
					int tankX = you.getX();
					int tankY = you.getY();
					
					int oldXCell = tankX / 10;
					int incXCell = (tankX + delta) / 10;
					int decXCell = (tankX - delta) / 10;
					int oldYCell = tankY / 10;
					int incYCell = (tankY + delta) / 10;
					int decYCell = (tankY - delta) / 10;
					
					switch (arg0.getKeyCode()) {
						case KeyEvent.VK_UP: {
							you.setOrientation(Orientation.UP);
							if (tankY - delta >= 0
									&& getCell(oldXCell, decYCell) == Cell.empty
									&& getCell(oldXCell + 1, decYCell) == Cell.empty
									&& getCell(oldXCell + 2, decYCell) == Cell.empty
									&& getCell(oldXCell + 3, decYCell) == Cell.empty) {
								tankY -= delta;
								
								currentMap[oldXCell][decYCell] = Cell.tank;
								currentMap[oldXCell + 1][decYCell] = Cell.tank;
								currentMap[oldXCell + 2][decYCell] = Cell.tank;
								currentMap[oldXCell + 3][decYCell] = Cell.tank;
								
								currentMap[oldXCell][decYCell + 4] = Cell.empty;
								currentMap[oldXCell + 1][decYCell + 4] = Cell.empty;
								currentMap[oldXCell + 2][decYCell + 4] = Cell.empty;
								currentMap[oldXCell + 3][decYCell + 4] = Cell.empty;
							}
							break;
						}
						case KeyEvent.VK_RIGHT: {
							you.setOrientation(Orientation.RIGHT);
							if (tankX + delta <= JBattleCity.HEIGHT - MapState.BLOCK_SIZE_PIXEL
									&& getCell(incXCell + 3, oldYCell) == Cell.empty
									&& getCell(incXCell + 3, oldYCell + 1) == Cell.empty
									&& getCell(incXCell + 3, oldYCell + 2) == Cell.empty
									&& getCell(incXCell + 3, oldYCell + 3) == Cell.empty) {
								tankX += delta;
								
								currentMap[incXCell + 3][oldYCell] = Cell.tank;
								currentMap[incXCell + 3][oldYCell + 1] = Cell.tank;
								currentMap[incXCell + 3][oldYCell + 2] = Cell.tank;
								currentMap[incXCell + 3][oldYCell + 3] = Cell.tank;
								
								currentMap[oldXCell][oldYCell] = Cell.empty;
								currentMap[oldXCell][oldYCell + 1] = Cell.empty;
								currentMap[oldXCell][oldYCell + 2] = Cell.empty;
								currentMap[oldXCell][oldYCell + 3] = Cell.empty;
							}
							break;
						}
						case KeyEvent.VK_DOWN: {
							you.setOrientation(Orientation.DOWN);
							if (tankY + delta <= JBattleCity.WIDTH - MapState.BLOCK_SIZE_PIXEL
									&& getCell(oldXCell, incYCell + 3) == Cell.empty
									&& getCell(oldXCell + 1, incYCell + 3) == Cell.empty
									&& getCell(oldXCell + 2, incYCell + 3) == Cell.empty
									&& getCell(oldXCell + 3, incYCell + 3) == Cell.empty) {
								tankY += delta;
								
								currentMap[oldXCell][incYCell + 3] = Cell.tank;
								currentMap[oldXCell + 1][incYCell + 3] = Cell.tank;
								currentMap[oldXCell + 2][incYCell + 3] = Cell.tank;
								currentMap[oldXCell + 3][incYCell + 3] = Cell.tank;
								
								currentMap[oldXCell][oldYCell] = Cell.empty;
								currentMap[oldXCell + 1][oldYCell] = Cell.empty;
								currentMap[oldXCell + 2][oldYCell] = Cell.empty;
								currentMap[oldXCell + 3][oldYCell] = Cell.empty;
							}
							break;
						}
						case KeyEvent.VK_LEFT: {
							you.setOrientation(Orientation.LEFT);
							if (tankX - delta >= 0
									&& getCell(decXCell, oldYCell) == Cell.empty
									&& getCell(decXCell, oldYCell + 1) == Cell.empty
									&& getCell(decXCell, oldYCell + 2) == Cell.empty
									&& getCell(decXCell, oldYCell + 3) == Cell.empty) {
								tankX -= delta;
								
								currentMap[decXCell][oldYCell] = Cell.tank;
								currentMap[decXCell][oldYCell + 1] = Cell.tank;
								currentMap[decXCell][oldYCell + 2] = Cell.tank;
								currentMap[decXCell][oldYCell + 3] = Cell.tank;
								
								currentMap[decXCell + 4][oldYCell] = Cell.empty;
								currentMap[decXCell + 4][oldYCell + 1] = Cell.empty;
								currentMap[decXCell + 4][oldYCell + 2] = Cell.empty;
								currentMap[decXCell + 4][oldYCell + 3] = Cell.empty;
							}
							break;
						}
						case KeyEvent.VK_CAPS_LOCK: {
							if (you.isHasProjectile()) break;
							ProjectileState ps = new ProjectileState();
							switch (you.getOrientation()) {
								case UP: {
									ps.setX(you.getX() + MapState.HALF_BLOCK_SIZE_PIXEL);
									ps.setY(you.getY());
									ps.setOrientation(Orientation.UP);
									break;
								}
								case RIGHT: {
									ps.setX(you.getX() + MapState.BLOCK_SIZE_PIXEL);
									ps.setY(you.getY() + MapState.HALF_BLOCK_SIZE_PIXEL);
									ps.setOrientation(Orientation.RIGHT);
									break;
								}
								case DOWN: {
									ps.setX(you.getX() + MapState.HALF_BLOCK_SIZE_PIXEL);
									ps.setY(you.getY() + MapState.BLOCK_SIZE_PIXEL);
									ps.setOrientation(Orientation.DOWN);
									break;
								}
								case LEFT: {
									ps.setX(you.getX());
									ps.setY(you.getY() + MapState.HALF_BLOCK_SIZE_PIXEL);
									ps.setOrientation(Orientation.LEFT);
									break;
								}
							}
							ps.setParent(you);
							addProjectile(ps);
						}
					}
					
					you.setX(tankX);
					you.setY(tankY);
				}
				
				return false;
			}
		};
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(yourKeyEventsDispatcher);
		
		enemiesTimer = new Timer();
		enemiesTimer.schedule(new TimerTask() {
			
			Random rand = new Random();
			
			@Override
			public void run() {
				if (enemies.size() < 3) {
					addEnemy();
				}

				final int delta = 10;
				
				for (TankState enemy : enemies.values()) {
					int tankX = enemy.getX();
					int tankY = enemy.getY();
					
					int oldXCell = tankX / 10;
					int incXCell = (tankX + delta) / 10;
					int decXCell = (tankX - delta) / 10;
					int oldYCell = tankY / 10;
					int incYCell = (tankY + delta) / 10;
					int decYCell = (tankY - delta) / 10;
					
					boolean moved = false;
					switch (enemy.getOrientation()) {
						case UP: {
							if (tankY - delta >= 0
									&& getCell(oldXCell, decYCell) == Cell.empty
									&& getCell(oldXCell + 1, decYCell) == Cell.empty
									&& getCell(oldXCell + 2, decYCell) == Cell.empty
									&& getCell(oldXCell + 3, decYCell) == Cell.empty) {
								tankY -= delta;
								
								currentMap[oldXCell][decYCell] = Cell.tank;
								currentMap[oldXCell + 1][decYCell] = Cell.tank;
								currentMap[oldXCell + 2][decYCell] = Cell.tank;
								currentMap[oldXCell + 3][decYCell] = Cell.tank;
								
								currentMap[oldXCell][decYCell + 4] = Cell.empty;
								currentMap[oldXCell + 1][decYCell + 4] = Cell.empty;
								currentMap[oldXCell + 2][decYCell + 4] = Cell.empty;
								currentMap[oldXCell + 3][decYCell + 4] = Cell.empty;

								moved = true;
							}
							break;
						}
						case RIGHT: {
							if (tankX + delta <= JBattleCity.HEIGHT - MapState.BLOCK_SIZE_PIXEL
									&& getCell(incXCell + 3, oldYCell) == Cell.empty
									&& getCell(incXCell + 3, oldYCell + 1) == Cell.empty
									&& getCell(incXCell + 3, oldYCell + 2) == Cell.empty
									&& getCell(incXCell + 3, oldYCell + 3) == Cell.empty) {
								tankX += delta;
								
								currentMap[incXCell + 3][oldYCell] = Cell.tank;
								currentMap[incXCell + 3][oldYCell + 1] = Cell.tank;
								currentMap[incXCell + 3][oldYCell + 2] = Cell.tank;
								currentMap[incXCell + 3][oldYCell + 3] = Cell.tank;
								
								currentMap[oldXCell][oldYCell] = Cell.empty;
								currentMap[oldXCell][oldYCell + 1] = Cell.empty;
								currentMap[oldXCell][oldYCell + 2] = Cell.empty;
								currentMap[oldXCell][oldYCell + 3] = Cell.empty;

								moved = true;
							}
							break;
						}
						case DOWN: {
							if (tankY + delta <= JBattleCity.WIDTH - MapState.BLOCK_SIZE_PIXEL
									&& getCell(oldXCell, incYCell + 3) == Cell.empty
									&& getCell(oldXCell + 1, incYCell + 3) == Cell.empty
									&& getCell(oldXCell + 2, incYCell + 3) == Cell.empty
									&& getCell(oldXCell + 3, incYCell + 3) == Cell.empty) {
								tankY += delta;
								
								currentMap[oldXCell][incYCell + 3] = Cell.tank;
								currentMap[oldXCell + 1][incYCell + 3] = Cell.tank;
								currentMap[oldXCell + 2][incYCell + 3] = Cell.tank;
								currentMap[oldXCell + 3][incYCell + 3] = Cell.tank;
								
								currentMap[oldXCell][oldYCell] = Cell.empty;
								currentMap[oldXCell + 1][oldYCell] = Cell.empty;
								currentMap[oldXCell + 2][oldYCell] = Cell.empty;
								currentMap[oldXCell + 3][oldYCell] = Cell.empty;

								moved = true;
							}
							break;
						}
						case LEFT: {
							if (tankX - delta >= 0
									&& getCell(decXCell, oldYCell) == Cell.empty
									&& getCell(decXCell, oldYCell + 1) == Cell.empty
									&& getCell(decXCell, oldYCell + 2) == Cell.empty
									&& getCell(decXCell, oldYCell + 3) == Cell.empty) {
								tankX -= delta;
								
								currentMap[decXCell][oldYCell] = Cell.tank;
								currentMap[decXCell][oldYCell + 1] = Cell.tank;
								currentMap[decXCell][oldYCell + 2] = Cell.tank;
								currentMap[decXCell][oldYCell + 3] = Cell.tank;
								
								currentMap[decXCell + 4][oldYCell] = Cell.empty;
								currentMap[decXCell + 4][oldYCell + 1] = Cell.empty;
								currentMap[decXCell + 4][oldYCell + 2] = Cell.empty;
								currentMap[decXCell + 4][oldYCell + 3] = Cell.empty;

								moved = true;
							}
							break;
						}
					}
					
					if (!moved) {
						int desision = rand.nextInt(8);
						if (desision < 4) {
							enemy.setOrientation(Orientation.values()[desision]);
						} else if (!enemy.isHasProjectile()) {
							ProjectileState ps = new ProjectileState();
							switch (enemy.getOrientation()) {
								case UP: {
									ps.setX(enemy.getX() + MapState.HALF_BLOCK_SIZE_PIXEL);
									ps.setY(enemy.getY());
									ps.setOrientation(Orientation.UP);
									break;
								}
								case RIGHT: {
									ps.setX(enemy.getX() + MapState.BLOCK_SIZE_PIXEL);
									ps.setY(enemy.getY() + MapState.HALF_BLOCK_SIZE_PIXEL);
									ps.setOrientation(Orientation.RIGHT);
									break;
								}
								case DOWN: {
									ps.setX(enemy.getX() + MapState.HALF_BLOCK_SIZE_PIXEL);
									ps.setY(enemy.getY() + MapState.BLOCK_SIZE_PIXEL);
									ps.setOrientation(Orientation.DOWN);
									break;
								}
								case LEFT: {
									ps.setX(enemy.getX());
									ps.setY(enemy.getY() + MapState.HALF_BLOCK_SIZE_PIXEL);
									ps.setOrientation(Orientation.LEFT);
									break;
								}
							}
							ps.setParent(enemy);
							addProjectile(ps);
						}
					}
					
					enemy.setX(tankX);
					enemy.setY(tankY);
				}
			}
		}, 1000, 40);
		
		projectilesTimer = new Timer();
		projectilesTimer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				int delta = 5;
				
				Set<Integer> projectilesIds = projectiles.keySet();
				
				for (Integer projectileId : projectilesIds) {
					ProjectileState ps = projectiles.get(projectileId);
					if (ps.getX() < Images.PROJECTILE_SIZE || ps.getX() > JBattleCity.WIDTH  - Images.PROJECTILE_SIZE - delta
					 || ps.getY() < Images.PROJECTILE_SIZE || ps.getY() > JBattleCity.HEIGHT - Images.PROJECTILE_SIZE - delta) {
						ps.getParent().setHasProjectile(false);
						projectiles.remove(projectileId);
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
	    				projectiles.remove(projectileId);
	    			}
	    			if (getCell(x, y) == Cell.base || getCell(x1, y1) == Cell.base) {
	    				setGameOver(true);
	    			}
	    			if (getCell(x, y) == Cell.tank || getCell(x1, y1) == Cell.base) {
	    				destroyTank(x, y, x1, y1);
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
		}, 0, 10);
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

	public Collection<ProjectileState> getProjectiles() {
		return projectiles.values();
	}
	
	public void addProjectile(ProjectileState projectile) {
		projectiles.put(projectileId++, projectile);
	}

	public Collection<TankState> getEnemies() {
		return enemies.values();
	}
	
	public boolean addEnemy() {
		if (isEmptyBlock(BLOCKS_COUNT / 2 * BLOCK_SIZE, 0)) {
			TankState enemy = new TankState(BLOCKS_COUNT / 2 * BLOCK_SIZE_PIXEL, 0, Orientation.DOWN);
			enemies.put(enemyId++, enemy);
			setTankBlock(BLOCKS_COUNT / 2 * BLOCK_SIZE, 0);
			return true;
		} else if (isEmptyBlock(0, 0)) {
			TankState enemy = new TankState(0, 0, Orientation.DOWN);
			enemies.put(enemyId++, enemy);
			setTankBlock(0, 0);
			return true;
		} else if (isEmptyBlock((BLOCKS_COUNT - 1) * BLOCK_SIZE, 0)) {
			TankState enemy = new TankState((BLOCKS_COUNT - 1) * BLOCK_SIZE_PIXEL, 0, Orientation.DOWN);
			enemies.put(enemyId++, enemy);
			setTankBlock((BLOCKS_COUNT - 1) * BLOCK_SIZE, 0);
			return true;
		}
		return false;
	}
	
	public boolean isEmptyBlock(int x, int y) {
		if (getCell(x, y)     == Cell.empty && getCell(x + 1, y)     == Cell.empty && getCell(x + 2, y)     == Cell.empty && getCell(x + 3, y)     == Cell.empty
		 && getCell(x, y + 1) == Cell.empty && getCell(x + 1, y + 1) == Cell.empty && getCell(x + 2, y + 1) == Cell.empty && getCell(x + 3, y + 1) == Cell.empty
		 && getCell(x, y + 2) == Cell.empty && getCell(x + 1, y + 2) == Cell.empty && getCell(x + 2, y + 2) == Cell.empty && getCell(x + 3, y + 2) == Cell.empty
		 && getCell(x, y + 3) == Cell.empty && getCell(x + 1, y + 3) == Cell.empty && getCell(x + 2, y + 3) == Cell.empty && getCell(x + 3, y + 3) == Cell.empty) {
			return true;
		}
		return false;
	}
	
	public void setTankBlock(int x, int y) {
		synchronized (currentMap) {
			currentMap[x][y]     = Cell.tank;	currentMap[x + 1][y]     = Cell.tank;	currentMap[x + 2][y]     = Cell.tank;	currentMap[x + 3][y]     = Cell.tank;
			currentMap[x][y + 1] = Cell.tank;	currentMap[x + 1][y + 1] = Cell.tank;	currentMap[x + 2][y + 1] = Cell.tank;	currentMap[x + 3][y + 1] = Cell.tank;
			currentMap[x][y + 2] = Cell.tank;	currentMap[x + 1][y + 2] = Cell.tank;	currentMap[x + 2][y + 2] = Cell.tank;	currentMap[x + 3][y + 2] = Cell.tank;
			currentMap[x][y + 3] = Cell.tank;	currentMap[x + 1][y + 3] = Cell.tank;	currentMap[x + 2][y + 3] = Cell.tank;	currentMap[x + 3][y + 3] = Cell.tank;
		}
	}
	
	public void removeTankBlock(int x, int y) {
		synchronized (currentMap) {
			currentMap[x][y]     = Cell.empty;	currentMap[x + 1][y]     = Cell.empty;	currentMap[x + 2][y]     = Cell.empty;	currentMap[x + 3][y]     = Cell.empty;
			currentMap[x][y + 1] = Cell.empty;	currentMap[x + 1][y + 1] = Cell.empty;	currentMap[x + 2][y + 1] = Cell.empty;	currentMap[x + 3][y + 1] = Cell.empty;
			currentMap[x][y + 2] = Cell.empty;	currentMap[x + 1][y + 2] = Cell.empty;	currentMap[x + 2][y + 2] = Cell.empty;	currentMap[x + 3][y + 2] = Cell.empty;
			currentMap[x][y + 3] = Cell.empty;	currentMap[x + 1][y + 3] = Cell.empty;	currentMap[x + 2][y + 3] = Cell.empty;	currentMap[x + 3][y + 3] = Cell.empty;
		}
	}
	
	public void destroyTank(int x, int y, int x1, int y1) {
		Set<Integer> enemiesIds = enemies.keySet();
		for (Integer enemyId : enemiesIds) {
			TankState tank = enemies.get(enemyId);
			
			int tankXCell = tank.getX() / 10;
			int tankYCell = tank.getY() / 10;
			
			if (x >= tankXCell && x <= tankXCell + 3 && y >= tankYCell && y <= tankYCell + 3
				|| x1 >= tankXCell && x1 <= tankXCell + 3 && y1 >= tankYCell && y1 <= tankYCell + 3) {
				removeTankBlock(tankXCell, tankYCell);
				enemies.remove(enemyId);
			}
		}
		int tankXCell = you.getX() / 10;
		int tankYCell = you.getY() / 10;
		
		if (x >= tankXCell && x <= tankXCell + 3 && y >= tankYCell && y <= tankYCell + 3
			|| x1 >= tankXCell && x1 <= tankXCell + 3 && y1 >= tankYCell && y1 <= tankYCell + 3) {
			removeTankBlock(tankXCell, tankYCell);
			you.setOrientation(null);
			setGameOver(true);
		}
	}

	public boolean isGameOver() {
		return gameOver;
	}

	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
		KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(yourKeyEventsDispatcher);
		enemiesTimer.cancel();
		projectilesTimer.cancel();
	}

}
