package ua.pp.condor.jbattlecity.area;

import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Random;
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
	
	private KeyEventDispatcher yourKeyEventsDispatcher;
	
	private List<TankState> enemies;
	
	private Timer enemiesTimer;
	
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
		enemies = new CopyOnWriteArrayList<TankState>();
		projectiles = new CopyOnWriteArrayList<ProjectileState>();
		
		yourKeyEventsDispatcher = new KeyEventDispatcher() {
			
			public boolean dispatchKeyEvent(KeyEvent arg0) {
				if (arg0.getID() == KeyEvent.KEY_PRESSED) {
					final int delta = 5;
					
					TankState you = getYou();
					
					int tankX = you.getX();
					int tankY = you.getY();
					
					int oldXCell = tankX / 10;
					int oldXRoundCell = Math.round(tankX / 10f);
					int incXRoundCell = Math.round((tankX + delta) / 10f);
					int decXCell = (tankX - delta) / 10;
					int oldYCell = tankY / 10;
					int oldYRoundCell = Math.round(tankY / 10f);
					int incYRoundCell = Math.round((tankY + delta) / 10f);
					int decYCell = (tankY - delta) / 10;
					
					switch (arg0.getKeyCode()) {
						case KeyEvent.VK_UP: {
							you.setOrientation(Orientation.UP);
							if (tankY - delta >= 0
									&& getCell(oldXCell, decYCell) == Cell.empty
									&& getCell(oldXRoundCell + 1, decYCell) == Cell.empty
									&& getCell(oldXRoundCell + 2, decYCell) == Cell.empty
									&& getCell(oldXRoundCell + 3, decYCell) == Cell.empty) {
								tankY -= delta;
							}
							break;
						}
						case KeyEvent.VK_DOWN: {
							you.setOrientation(Orientation.DOWN);
							if (tankY + delta <= JBattleCity.WIDTH - MapState.BLOCK_SIZE_PIXEL
									&& getCell(oldXCell, incYRoundCell + 3) == Cell.empty
									&& getCell(oldXRoundCell + 1, incYRoundCell + 3) == Cell.empty
									&& getCell(oldXRoundCell + 2, incYRoundCell + 3) == Cell.empty
									&& getCell(oldXRoundCell + 3, incYRoundCell + 3) == Cell.empty) {
								tankY += delta;
							}
							break;
						}
						case KeyEvent.VK_RIGHT: {
							you.setOrientation(Orientation.RIGHT);
							if (tankX + delta <= JBattleCity.HEIGHT - MapState.BLOCK_SIZE_PIXEL
									&& getCell(incXRoundCell + 3, oldYCell) == Cell.empty
									&& getCell(incXRoundCell + 3, oldYRoundCell + 1) == Cell.empty
									&& getCell(incXRoundCell + 3, oldYRoundCell + 2) == Cell.empty
									&& getCell(incXRoundCell + 3, oldYRoundCell + 3) == Cell.empty) {
								tankX += delta;
							}
							break;
						}
						case KeyEvent.VK_LEFT: {
							you.setOrientation(Orientation.LEFT);
							if (tankX - delta >= 0
									&& getCell(decXCell, oldYCell) == Cell.empty
									&& getCell(decXCell, oldYRoundCell + 1) == Cell.empty
									&& getCell(decXCell, oldYRoundCell + 2) == Cell.empty
									&& getCell(decXCell, oldYRoundCell + 3) == Cell.empty) {
								tankX -= delta;
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

				final int delta = 5;
				
				for (TankState enemy : enemies) {
					
					int tankX = enemy.getX();
					int tankY = enemy.getY();
					
					int oldXCell = tankX / 10;
					int oldXRoundCell = Math.round(tankX / 10f);
					int incXRoundCell = Math.round((tankX + delta) / 10f);
					int decXCell = (tankX - delta) / 10;
					int oldYCell = tankY / 10;
					int oldYRoundCell = Math.round(tankY / 10f);
					int incYRoundCell = Math.round((tankY + delta) / 10f);
					int decYCell = (tankY - delta) / 10;
					
					boolean moved = false;
					switch (enemy.getOrientation()) {
						case UP: {
							if (tankY - delta >= 0
									&& getCell(oldXCell, decYCell) == Cell.empty
									&& getCell(oldXRoundCell + 1, decYCell) == Cell.empty
									&& getCell(oldXRoundCell + 2, decYCell) == Cell.empty
									&& getCell(oldXRoundCell + 3, decYCell) == Cell.empty) {
								tankY -= delta;
								moved = true;
							}
							break;
						}
						case RIGHT: {
							if (tankX + delta <= JBattleCity.HEIGHT - MapState.BLOCK_SIZE_PIXEL
									&& getCell(incXRoundCell + 3, oldYCell) == Cell.empty
									&& getCell(incXRoundCell + 3, oldYRoundCell + 1) == Cell.empty
									&& getCell(incXRoundCell + 3, oldYRoundCell + 2) == Cell.empty
									&& getCell(incXRoundCell + 3, oldYRoundCell + 3) == Cell.empty) {
								tankX += delta;
								moved = true;
							}
							break;
						}
						case DOWN: {
							if (tankY + delta <= JBattleCity.WIDTH - MapState.BLOCK_SIZE_PIXEL
									&& getCell(oldXCell, incYRoundCell + 3) == Cell.empty
									&& getCell(oldXRoundCell + 1, incYRoundCell + 3) == Cell.empty
									&& getCell(oldXRoundCell + 2, incYRoundCell + 3) == Cell.empty
									&& getCell(oldXRoundCell + 3, incYRoundCell + 3) == Cell.empty) {
								tankY += delta;
								moved = true;
							}
							break;
						}
						case LEFT: {
							if (tankX - delta >= 0
									&& getCell(decXCell, oldYCell) == Cell.empty
									&& getCell(decXCell, oldYRoundCell + 1) == Cell.empty
									&& getCell(decXCell, oldYRoundCell + 2) == Cell.empty
									&& getCell(decXCell, oldYRoundCell + 3) == Cell.empty) {
								tankX -= delta;
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
		}, 1000, 20);
		
		projectilesTimer = new Timer();
		projectilesTimer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				int delta = 5;
				
				List<ProjectileState> projectiles = getProjectiles();
				
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
	
	public void addProjectile(ProjectileState projectile) {
		projectiles.add(projectile);
	}

	public List<TankState> getEnemies() {
		return enemies;
	}
	
	public boolean addEnemy() {
		if (isEmptyBlock(BLOCKS_COUNT / 2 * BLOCK_SIZE, 0)) {
			TankState enemy = new TankState(BLOCKS_COUNT / 2 * BLOCK_SIZE_PIXEL, 0, Orientation.DOWN);
			enemies.add(enemy);
//			setTankBlock(BLOCKS_COUNT / 2 * BLOCK_SIZE, 0);
			return true;
		} else if (isEmptyBlock(0, 0)) {
			TankState enemy = new TankState(0, 0, Orientation.DOWN);
			enemies.add(enemy);
//			setTankBlock(0, 0);
			return true;
		} else if (isEmptyBlock((BLOCKS_COUNT - 1) * BLOCK_SIZE, 0)) {
			TankState enemy = new TankState((BLOCKS_COUNT - 1) * BLOCK_SIZE_PIXEL, 0, Orientation.DOWN);
			enemies.add(enemy);
//			setTankBlock((BLOCKS_COUNT - 1) * BLOCK_SIZE, 0);
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
		currentMap[x][y]     = Cell.tank;	currentMap[x + 1][y]     = Cell.tank;	currentMap[x + 2][y]     = Cell.tank;	currentMap[x + 3][y]     = Cell.tank;
		currentMap[x][y + 1] = Cell.tank;	currentMap[x + 1][y + 1] = Cell.tank;	currentMap[x + 2][y + 1] = Cell.tank;	currentMap[x + 3][y + 1] = Cell.tank;
		currentMap[x][y + 2] = Cell.tank;	currentMap[x + 1][y + 2] = Cell.tank;	currentMap[x + 2][y + 2] = Cell.tank;	currentMap[x + 3][y + 2] = Cell.tank;
		currentMap[x][y + 3] = Cell.tank;	currentMap[x + 1][y + 3] = Cell.tank;	currentMap[x + 2][y + 3] = Cell.tank;	currentMap[x + 3][y + 3] = Cell.tank;
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
