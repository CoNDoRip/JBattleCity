package ua.pp.condor.jbattlecity.area.actions;

import ua.pp.condor.jbattlecity.JBattleCity;
import ua.pp.condor.jbattlecity.area.Cell;
import ua.pp.condor.jbattlecity.area.Constants;
import ua.pp.condor.jbattlecity.area.MapState;
import ua.pp.condor.jbattlecity.tank.Orientation;
import ua.pp.condor.jbattlecity.tank.TankState;

import java.util.Collection;
import java.util.Random;
import java.util.TimerTask;

public class EnemiesTimerTaskPassive extends TimerTask {

    private final MapState mapState;

    public EnemiesTimerTaskPassive(MapState mapState) {
        this.mapState = mapState;
    }

    Random rand = new Random();

    @Override
    public void run() {
        Collection<TankState> enemies = mapState.getEnemies();
        if (enemies.size() < 3) {
            mapState.addEnemy();
        }

        final int delta = Constants.TANK_STEP;

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

                        mapState.moveTankBlock(tankX, tankY, Orientation.UP);
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

                        mapState.moveTankBlock(tankX, tankY, Orientation.RIGHT);
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

                        mapState.moveTankBlock(tankX, tankY, Orientation.DOWN);
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

                        mapState.moveTankBlock(tankX, tankY, Orientation.LEFT);
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
                    mapState.addProjectile(enemy);
                }
            }

            enemy.setX(tankX);
            enemy.setY(tankY);
        }
    }
}
