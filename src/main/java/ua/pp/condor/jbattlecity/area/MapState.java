package ua.pp.condor.jbattlecity.area;

import ua.pp.condor.jbattlecity.area.actions.EnemiesTimerTask;
import ua.pp.condor.jbattlecity.area.actions.ProjectilesTimerTask;
import ua.pp.condor.jbattlecity.area.actions.YourKeyEventsDispatcher;
import ua.pp.condor.jbattlecity.area.maps.IMap;
import ua.pp.condor.jbattlecity.tank.EnemyTankState;
import ua.pp.condor.jbattlecity.tank.Orientation;
import ua.pp.condor.jbattlecity.tank.ProjectileState;
import ua.pp.condor.jbattlecity.tank.TankState;
import ua.pp.condor.jbattlecity.tank.YouTankState;
import ua.pp.condor.jbattlecity.utils.Sound;

import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

public class MapState implements IMap {
	
	public static final int BLOCKS_COUNT = 13;
	public static final int BLOCK_SIZE = 4;
	public static final int BLOCK_SIZE_PIXEL = 40;
	public static final int HALF_BLOCK_SIZE_PIXEL = 20;
	
	public static final int ARRAY_SIZE = BLOCKS_COUNT * BLOCK_SIZE;
	
	private final Cell[][] currentMap = new Cell[ARRAY_SIZE][ARRAY_SIZE];
	
	private final IMap map;
	
	private YouTankState you;
	
	private final KeyEventDispatcher yourKeyEventsDispatcher;
	
	private final Map<Integer, EnemyTankState> enemies;
	
	private int enemyId;
	
	private final Timer enemiesTimer;
	
	private final Map<Integer, ProjectileState> projectiles;
	
	private int projectileId;
	
	private final Timer projectilesTimer;
	
	private boolean gameOver;
	
	public MapState(IMap map) {
		this.map = map;
		for (int x = 0; x < ARRAY_SIZE; x++) {
			for (int y = 0; y < ARRAY_SIZE; y++) {
				currentMap[x][y] = map.getCell(x, y);
			}
		}
		you = new YouTankState(160, 480, Orientation.UP);
		setTankBlock(16, 48);
		enemies = new ConcurrentHashMap<Integer, EnemyTankState>();
		projectiles = new ConcurrentHashMap<Integer, ProjectileState>();
        enemyId = 0;
        projectileId = 0;
		
		yourKeyEventsDispatcher = new YourKeyEventsDispatcher(this);
		enemiesTimer = new Timer();
		projectilesTimer = new Timer();
	}

    @Override
	public Cell getCell(int x, int y) {
		return currentMap[x][y];
	}

    @Override
	public Image getMapImage() {
		return map.getMapImage();
	}

	public YouTankState getYou() {
		return you;
	}

	public void setYou(YouTankState you) {
		this.you = you;
	}

	public Collection<ProjectileState> getProjectiles() {
		return projectiles.values();
	}
	
	public void addProjectile(ProjectileState projectile) {
		projectiles.put(projectileId++, projectile);
	}

	public Collection<EnemyTankState> getEnemies() {
		return enemies.values();
	}
	
	public boolean addEnemy() {
		if (isEmptyBlock(BLOCKS_COUNT / 2 * BLOCK_SIZE, 0)) {
			EnemyTankState enemy = new EnemyTankState(BLOCKS_COUNT / 2 * BLOCK_SIZE_PIXEL, 0, Orientation.DOWN);
			enemies.put(enemyId++, enemy);
			setTankBlock(BLOCKS_COUNT / 2 * BLOCK_SIZE, 0);
			return true;
		} else if (isEmptyBlock(0, 0)) {
			EnemyTankState enemy = new EnemyTankState(0, 0, Orientation.DOWN);
			enemies.put(enemyId++, enemy);
			setTankBlock(0, 0);
			return true;
		} else if (isEmptyBlock((BLOCKS_COUNT - 1) * BLOCK_SIZE, 0)) {
			EnemyTankState enemy = new EnemyTankState((BLOCKS_COUNT - 1) * BLOCK_SIZE_PIXEL, 0, Orientation.DOWN);
			enemies.put(enemyId++, enemy);
			setTankBlock((BLOCKS_COUNT - 1) * BLOCK_SIZE, 0);
			return true;
		}
		return false;
	}
	
	public boolean isEmptyBlock(int x, int y) {
        return getCell(x, y)     == Cell.empty && getCell(x + 1, y)     == Cell.empty && getCell(x + 2, y)     == Cell.empty && getCell(x + 3, y)     == Cell.empty
            && getCell(x, y + 1) == Cell.empty && getCell(x + 1, y + 1) == Cell.empty && getCell(x + 2, y + 1) == Cell.empty && getCell(x + 3, y + 1) == Cell.empty
            && getCell(x, y + 2) == Cell.empty && getCell(x + 1, y + 2) == Cell.empty && getCell(x + 2, y + 2) == Cell.empty && getCell(x + 3, y + 2) == Cell.empty
            && getCell(x, y + 3) == Cell.empty && getCell(x + 1, y + 3) == Cell.empty && getCell(x + 2, y + 3) == Cell.empty && getCell(x + 3, y + 3) == Cell.empty;
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
		Sound.getExplosion().play();
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

    public void startGame() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(yourKeyEventsDispatcher);
        enemiesTimer.schedule(new EnemiesTimerTask(this), 1000, 40);
        projectilesTimer.schedule(new ProjectilesTimerTask(this), 0, 10);
    }

	public boolean isGameOver() {
		return gameOver;
	}

	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
		KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(yourKeyEventsDispatcher);
		enemiesTimer.cancel();
		projectilesTimer.cancel();
		Sound.getBackground().stop();
		Sound.getGameOver().play();
	}

    public Cell[][] getCurrentMap() {
        return currentMap;
    }

    public Map<Integer, ProjectileState> getProjectilesMap() {
        return projectiles;
    }

}
