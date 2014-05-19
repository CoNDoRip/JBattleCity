package ua.pp.condor.jbattlecity.area.actions;

import ua.pp.condor.jbattlecity.JBattleCity;
import ua.pp.condor.jbattlecity.area.Cell;
import ua.pp.condor.jbattlecity.area.MapState;
import ua.pp.condor.jbattlecity.tank.Orientation;
import ua.pp.condor.jbattlecity.tank.ProjectileState;
import ua.pp.condor.jbattlecity.tank.TankState;

import java.util.Collection;
import java.util.Random;
import java.util.TimerTask;

public class EnemiesTimerTask extends TimerTask {

    private final MapState mapState;
    private final Cell[][] currentMap;

    public EnemiesTimerTask(MapState mapState) {
        this.mapState = mapState;
        currentMap = mapState.getCurrentMap();
    }

    Random rand = new Random();

    @Override
    public void run() {
        Collection<TankState> enemies = mapState.getEnemies();
        if (enemies.size() < 3) {
            mapState.addEnemy();
        }

        final int delta = 10;

        for (TankState enemy : enemies) {
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
                            && mapState.getCell(oldXCell, decYCell) == Cell.empty
                            && mapState.getCell(oldXCell + 1, decYCell) == Cell.empty
                            && mapState.getCell(oldXCell + 2, decYCell) == Cell.empty
                            && mapState.getCell(oldXCell + 3, decYCell) == Cell.empty) {
                        tankY -= delta;

                        synchronized (currentMap) {
                            currentMap[oldXCell][decYCell + 4] = Cell.empty;
                            currentMap[oldXCell + 1][decYCell + 4] = Cell.empty;
                            currentMap[oldXCell + 2][decYCell + 4] = Cell.empty;
                            currentMap[oldXCell + 3][decYCell + 4] = Cell.empty;

                            currentMap[oldXCell][decYCell] = Cell.tank;
                            currentMap[oldXCell + 1][decYCell] = Cell.tank;
                            currentMap[oldXCell + 2][decYCell] = Cell.tank;
                            currentMap[oldXCell + 3][decYCell] = Cell.tank;
                        }

                        moved = true;
                    }
                    break;
                }
                case RIGHT: {
                    if (tankX + delta <= JBattleCity.HEIGHT - MapState.BLOCK_SIZE_PIXEL
                            && mapState.getCell(incXCell + 3, oldYCell) == Cell.empty
                            && mapState.getCell(incXCell + 3, oldYCell + 1) == Cell.empty
                            && mapState.getCell(incXCell + 3, oldYCell + 2) == Cell.empty
                            && mapState.getCell(incXCell + 3, oldYCell + 3) == Cell.empty) {
                        tankX += delta;

                        synchronized (currentMap) {
                            currentMap[oldXCell][oldYCell] = Cell.empty;
                            currentMap[oldXCell][oldYCell + 1] = Cell.empty;
                            currentMap[oldXCell][oldYCell + 2] = Cell.empty;
                            currentMap[oldXCell][oldYCell + 3] = Cell.empty;

                            currentMap[incXCell + 3][oldYCell] = Cell.tank;
                            currentMap[incXCell + 3][oldYCell + 1] = Cell.tank;
                            currentMap[incXCell + 3][oldYCell + 2] = Cell.tank;
                            currentMap[incXCell + 3][oldYCell + 3] = Cell.tank;
                        }

                        moved = true;
                    }
                    break;
                }
                case DOWN: {
                    if (tankY + delta <= JBattleCity.WIDTH - MapState.BLOCK_SIZE_PIXEL
                            && mapState.getCell(oldXCell, incYCell + 3) == Cell.empty
                            && mapState.getCell(oldXCell + 1, incYCell + 3) == Cell.empty
                            && mapState.getCell(oldXCell + 2, incYCell + 3) == Cell.empty
                            && mapState.getCell(oldXCell + 3, incYCell + 3) == Cell.empty) {
                        tankY += delta;

                        synchronized (currentMap) {
                            currentMap[oldXCell][oldYCell] = Cell.empty;
                            currentMap[oldXCell + 1][oldYCell] = Cell.empty;
                            currentMap[oldXCell + 2][oldYCell] = Cell.empty;
                            currentMap[oldXCell + 3][oldYCell] = Cell.empty;

                            currentMap[oldXCell][incYCell + 3] = Cell.tank;
                            currentMap[oldXCell + 1][incYCell + 3] = Cell.tank;
                            currentMap[oldXCell + 2][incYCell + 3] = Cell.tank;
                            currentMap[oldXCell + 3][incYCell + 3] = Cell.tank;
                        }

                        moved = true;
                    }
                    break;
                }
                case LEFT: {
                    if (tankX - delta >= 0
                            && mapState.getCell(decXCell, oldYCell) == Cell.empty
                            && mapState.getCell(decXCell, oldYCell + 1) == Cell.empty
                            && mapState.getCell(decXCell, oldYCell + 2) == Cell.empty
                            && mapState.getCell(decXCell, oldYCell + 3) == Cell.empty) {
                        tankX -= delta;

                        synchronized (currentMap) {
                            currentMap[decXCell + 4][oldYCell] = Cell.empty;
                            currentMap[decXCell + 4][oldYCell + 1] = Cell.empty;
                            currentMap[decXCell + 4][oldYCell + 2] = Cell.empty;
                            currentMap[decXCell + 4][oldYCell + 3] = Cell.empty;

                            currentMap[decXCell][oldYCell] = Cell.tank;
                            currentMap[decXCell][oldYCell + 1] = Cell.tank;
                            currentMap[decXCell][oldYCell + 2] = Cell.tank;
                            currentMap[decXCell][oldYCell + 3] = Cell.tank;
                        }

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
                    mapState.addProjectile(ps);
                }
            }

            enemy.setX(tankX);
            enemy.setY(tankY);
        }
    }
}
