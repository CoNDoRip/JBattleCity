package ua.pp.condor.jbattlecity.area;

import ua.pp.condor.jbattlecity.area.actions.EnemiesTimerTask;
import ua.pp.condor.jbattlecity.area.actions.ProjectilesTimerTask;
import ua.pp.condor.jbattlecity.area.actions.YourKeyEventsDispatcher;
import ua.pp.condor.jbattlecity.area.maps.IMap;
import ua.pp.condor.jbattlecity.network.Protocol;
import ua.pp.condor.jbattlecity.tank.Orientation;
import ua.pp.condor.jbattlecity.tank.ProjectileState;
import ua.pp.condor.jbattlecity.tank.TankState;
import ua.pp.condor.jbattlecity.tank.TanksFactory;
import ua.pp.condor.jbattlecity.tank.TanksFactory.EnemyPosition;
import ua.pp.condor.jbattlecity.tank.TanksFactory.PlayerPosition;
import ua.pp.condor.jbattlecity.utils.Sound;

import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.io.IOException;
import java.io.OutputStream;
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
    
    private TankState you;
    private TankState friend;
    
    private final KeyEventDispatcher yourKeyEventsDispatcher;
    
    private final Map<Integer, TankState> enemies;
    
    private int enemyId;
    
    private final Timer enemiesTimer;
    
    private final Map<Integer, ProjectileState> projectiles;
    
    private int projectileId;
    
    private final Timer projectilesTimer;
    
    private boolean gameOver;

    private OutputStream out;

    private final byte[] addEnemyBuf;
    
    public MapState(IMap map) {
        this.map = map;
        for (int x = 0; x < ARRAY_SIZE; x++) {
            for (int y = 0; y < ARRAY_SIZE; y++) {
                currentMap[x][y] = map.getCell(x, y);
            }
        }
        enemies = new ConcurrentHashMap<Integer, TankState>();
        projectiles = new ConcurrentHashMap<Integer, ProjectileState>();
        enemyId = 0;
        projectileId = 0;
        addEnemyBuf = new byte[Protocol.BUF_SIZE];
        addEnemyBuf[0] = Protocol.ENEMY;
        addEnemyBuf[1] = Protocol.ADD;
        
        yourKeyEventsDispatcher = new YourKeyEventsDispatcher(this);
        enemiesTimer = new Timer();
        projectilesTimer = new Timer();
    }

    public MapState(IMap map, OutputStream out) {
        this(map);
        this.out = out;
    }

    @Override
    public Cell getCell(int x, int y) {
        return currentMap[x][y];
    }

    @Override
    public Image getMapImage() {
        return map.getMapImage();
    }

    public TankState getYou() {
        return you;
    }

    public TankState getFriend() {
        return friend;
    }

    public Collection<ProjectileState> getProjectiles() {
        return projectiles.values();
    }
    
    public void addProjectile(TankState tank) {
        ProjectileState ps = new ProjectileState();
        ps.setOrientation(tank.getOrientation());
        switch (ps.getOrientation()) {
            case UP: {
                ps.setX(tank.getX() + HALF_BLOCK_SIZE_PIXEL);
                ps.setY(tank.getY());
                break;
            }
            case RIGHT: {
                ps.setX(tank.getX() + BLOCK_SIZE_PIXEL);
                ps.setY(tank.getY() + HALF_BLOCK_SIZE_PIXEL);
                break;
            }
            case DOWN: {
                ps.setX(tank.getX() + HALF_BLOCK_SIZE_PIXEL);
                ps.setY(tank.getY() + BLOCK_SIZE_PIXEL);
                break;
            }
            case LEFT: {
                ps.setX(tank.getX());
                ps.setY(tank.getY() + HALF_BLOCK_SIZE_PIXEL);
                break;
            }
        }
        ps.setParent(tank);
        projectiles.put(projectileId++, ps);
    }

    public Collection<TankState> getEnemies() {
        return enemies.values();
    }

    public Map<Integer, TankState> getEnemiesMap() {
        return enemies;
    }
    
    public boolean addEnemy() {
        if (enemyId > Constants.MAX_ENEMY_ID) return false;

        TankState enemy = null;
        if (isEmptyBlock(EnemyPosition.SECOND.getX(), EnemyPosition.SECOND.getY())) {
            enemy = TanksFactory.getEnemy(EnemyPosition.SECOND, this);
        } else if (isEmptyBlock(EnemyPosition.FIRST.getX(), EnemyPosition.FIRST.getY())) {
            enemy = TanksFactory.getEnemy(EnemyPosition.FIRST, this);
        } else if (isEmptyBlock(EnemyPosition.THIRD.getX(), EnemyPosition.THIRD.getY())) {
            enemy = TanksFactory.getEnemy(EnemyPosition.THIRD, this);
        }

        if (enemy != null) {
            enemies.put(enemyId, enemy);
            addEnemyBuf[1] = (byte) enemyId++;
            try {
                out.write(addEnemyBuf);
            } catch (IOException e) {
                System.out.println("Can not send info to server from MapState.addEnemy(): " + e.getMessage());
            }
            return true;
        }
        return false;
    }
    
    public boolean isEmptyBlock(int x, int y) {
        x /= 10;
        y /= 10;
        return getCell(x, y)     == Cell.empty && getCell(x + 1, y)     == Cell.empty && getCell(x + 2, y)     == Cell.empty && getCell(x + 3, y)     == Cell.empty
            && getCell(x, y + 1) == Cell.empty && getCell(x + 1, y + 1) == Cell.empty && getCell(x + 2, y + 1) == Cell.empty && getCell(x + 3, y + 1) == Cell.empty
            && getCell(x, y + 2) == Cell.empty && getCell(x + 1, y + 2) == Cell.empty && getCell(x + 2, y + 2) == Cell.empty && getCell(x + 3, y + 2) == Cell.empty
            && getCell(x, y + 3) == Cell.empty && getCell(x + 1, y + 3) == Cell.empty && getCell(x + 2, y + 3) == Cell.empty && getCell(x + 3, y + 3) == Cell.empty;
    }
    
    public void setTankBlock(int x, int y) {
        x /= 10;
        y /= 10;
        synchronized (currentMap) {
            currentMap[x][y]     = Cell.tank;    currentMap[x + 1][y]     = Cell.tank;    currentMap[x + 2][y]     = Cell.tank;    currentMap[x + 3][y]     = Cell.tank;
            currentMap[x][y + 1] = Cell.tank;    currentMap[x + 1][y + 1] = Cell.tank;    currentMap[x + 2][y + 1] = Cell.tank;    currentMap[x + 3][y + 1] = Cell.tank;
            currentMap[x][y + 2] = Cell.tank;    currentMap[x + 1][y + 2] = Cell.tank;    currentMap[x + 2][y + 2] = Cell.tank;    currentMap[x + 3][y + 2] = Cell.tank;
            currentMap[x][y + 3] = Cell.tank;    currentMap[x + 1][y + 3] = Cell.tank;    currentMap[x + 2][y + 3] = Cell.tank;    currentMap[x + 3][y + 3] = Cell.tank;
        }
    }
    
    public void removeTankBlock(int x, int y) {
        Sound.getExplosion().play();
        synchronized (currentMap) {
            currentMap[x][y]     = Cell.empty;    currentMap[x + 1][y]     = Cell.empty;    currentMap[x + 2][y]     = Cell.empty;    currentMap[x + 3][y]     = Cell.empty;
            currentMap[x][y + 1] = Cell.empty;    currentMap[x + 1][y + 1] = Cell.empty;    currentMap[x + 2][y + 1] = Cell.empty;    currentMap[x + 3][y + 1] = Cell.empty;
            currentMap[x][y + 2] = Cell.empty;    currentMap[x + 1][y + 2] = Cell.empty;    currentMap[x + 2][y + 2] = Cell.empty;    currentMap[x + 3][y + 2] = Cell.empty;
            currentMap[x][y + 3] = Cell.empty;    currentMap[x + 1][y + 3] = Cell.empty;    currentMap[x + 2][y + 3] = Cell.empty;    currentMap[x + 3][y + 3] = Cell.empty;
        }
    }
    
    public void moveTankBlock(int x, int y, Orientation orientation) {
        x /= 10;
        y /= 10;
        synchronized (currentMap) {
            switch (orientation) {
                case UP: {
                    currentMap[x]    [y] = Cell.tank;
                    currentMap[x + 1][y] = Cell.tank;
                    currentMap[x + 2][y] = Cell.tank;
                    currentMap[x + 3][y] = Cell.tank;

                    currentMap[x]    [y + 4] = Cell.empty;
                    currentMap[x + 1][y + 4] = Cell.empty;
                    currentMap[x + 2][y + 4] = Cell.empty;
                    currentMap[x + 3][y + 4] = Cell.empty;
                    break;
                }
                case RIGHT: {
                    currentMap[x + 3][y]     = Cell.tank;
                    currentMap[x + 3][y + 1] = Cell.tank;
                    currentMap[x + 3][y + 2] = Cell.tank;
                    currentMap[x + 3][y + 3] = Cell.tank;

                    currentMap[x - 1][y]     = Cell.empty;
                    currentMap[x - 1][y + 1] = Cell.empty;
                    currentMap[x - 1][y + 2] = Cell.empty;
                    currentMap[x - 1][y + 3] = Cell.empty;
                    break;
                }
                case DOWN: {
                    currentMap[x]    [y + 3] = Cell.tank;
                    currentMap[x + 1][y + 3] = Cell.tank;
                    currentMap[x + 2][y + 3] = Cell.tank;
                    currentMap[x + 3][y + 3] = Cell.tank;

                    currentMap[x]    [y - 1] = Cell.empty;
                    currentMap[x + 1][y - 1] = Cell.empty;
                    currentMap[x + 2][y - 1] = Cell.empty;
                    currentMap[x + 3][y - 1] = Cell.empty;
                    break;
                }
                case LEFT: {
                    currentMap[x][y]     = Cell.tank;
                    currentMap[x][y + 1] = Cell.tank;
                    currentMap[x][y + 2] = Cell.tank;
                    currentMap[x][y + 3] = Cell.tank;

                    currentMap[x + 4][y]     = Cell.empty;
                    currentMap[x + 4][y + 1] = Cell.empty;
                    currentMap[x + 4][y + 2] = Cell.empty;
                    currentMap[x + 4][y + 3] = Cell.empty;
                    break;
                }
            }
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
            setGameOver(true);  //FIXME
        }
    }

    public void startGame(int id) {
        you = TanksFactory.getYou(id == 1 ? PlayerPosition.FIRST : PlayerPosition.SECOND, this);
        friend = TanksFactory.getFriend(id == 1 ? PlayerPosition.SECOND : PlayerPosition.FIRST, this);

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(yourKeyEventsDispatcher);
        projectilesTimer.schedule(new ProjectilesTimerTask(this), 0, 10);
        if (id == 1) {
//            enemiesTimer.schedule(new EnemiesTimerTask(this), 1000, 40);
        }
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
        if (gameOver) {
            try {
                out.write(Protocol.GAME_OVER_BUF);
            } catch (IOException e) {
                System.out.println("Can not send info to server from MapState.setGameOver(): " + e.getMessage());
            }

            KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(yourKeyEventsDispatcher);
            enemiesTimer.cancel();
            projectilesTimer.cancel();
            Sound.getBackground().stop();
            Sound.getGameOver().play();
        }
    }

    public Cell[][] getCurrentMap() {
        return currentMap;
    }

    public Map<Integer, ProjectileState> getProjectilesMap() {
        return projectiles;
    }

    public OutputStream getOut() {
        return out;
    }

    public int getEnemyId() {
        return enemyId;
    }

}
