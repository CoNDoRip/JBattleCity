package ua.pp.condor.jbattlecity.area.actions;

import ua.pp.condor.jbattlecity.JBattleCity;
import ua.pp.condor.jbattlecity.area.Cell;
import ua.pp.condor.jbattlecity.area.Constants;
import ua.pp.condor.jbattlecity.area.MapState;
import ua.pp.condor.jbattlecity.network.Protocol;
import ua.pp.condor.jbattlecity.tank.Orientation;
import ua.pp.condor.jbattlecity.tank.TankState;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TimerTask;

public class EnemiesTimerTask extends TimerTask {

    private final MapState mapState;

    private final byte[] orientationBuf;
    private final byte[] movingBuf;
    private final byte[] shootingBuf;

    private boolean changeOrientation;
    private boolean changePlace;
    private boolean doneShooting;

    public EnemiesTimerTask(MapState mapState) {
        this.mapState = mapState;

        orientationBuf = new byte[Protocol.BUF_SIZE];
        orientationBuf[2] = Protocol.ORIENTATION;
        movingBuf = new byte[Protocol.BUF_SIZE];
        movingBuf[2] = Protocol.MOVING;
        shootingBuf = new byte[Protocol.BUF_SIZE];
        shootingBuf[2] = Protocol.SHOOTING;
        orientationBuf[0] = movingBuf[0] = shootingBuf[0] = Protocol.ENEMY;
    }

    Random rand = new Random();

    @Override
    public void run() {
        Map<Integer, TankState> enemiesMap = mapState.getEnemiesMap();
        if (mapState.getEnemyId() > Constants.MAX_ENEMY_ID && enemiesMap.isEmpty()) {
            mapState.setGameOver();
        } else if (enemiesMap.size() < 3) {
            mapState.addEnemy();
        }


        final int delta = Constants.TANK_STEP;

        Set<Integer> enemyIds = enemiesMap.keySet();
        for (Integer enemyId : enemyIds) {
            final TankState enemy = enemiesMap.get(enemyId);

            int tankX = enemy.getX();
            int tankY = enemy.getY();

            int oldXCell = tankX / 10;
            int incXCell = (tankX + delta) / 10;
            int decXCell = (tankX - delta) / 10;
            int oldYCell = tankY / 10;
            int incYCell = (tankY + delta) / 10;
            int decYCell = (tankY - delta) / 10;

            changeOrientation = changePlace = doneShooting = false;
            orientationBuf[1] = movingBuf[1] = shootingBuf[1] = enemyId.byteValue();

            switch (enemy.getOrientation()) {
                case UP: {
                    if (tankY - delta >= 0
                            && mapState.getCell(oldXCell, decYCell) == Cell.empty
                            && mapState.getCell(oldXCell + 1, decYCell) == Cell.empty
                            && mapState.getCell(oldXCell + 2, decYCell) == Cell.empty
                            && mapState.getCell(oldXCell + 3, decYCell) == Cell.empty) {
                        tankY -= delta;

                        doMoving(tankX, tankY, Orientation.UP);
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

                        doMoving(tankX, tankY, Orientation.RIGHT);
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

                        doMoving(tankX, tankY, Orientation.DOWN);
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

                        doMoving(tankX, tankY, Orientation.LEFT);
                    }
                    break;
                }
            }

            if (!changePlace) {
                int desision = rand.nextInt(8);
                if (desision < 4) {
                    changeOrientation(enemy, Orientation.values()[desision]);
                } else if (!enemy.isHasProjectile()) {
                    mapState.addProjectile(enemy);
                    doShooting(enemy.getOrientation());
                }
            }

            enemy.setX(tankX);
            enemy.setY(tankY);

            try {
                OutputStream out = mapState.getOut();
                if (changeOrientation)
                    out.write(orientationBuf);
                if (changePlace)
                    out.write(movingBuf);
                if (doneShooting)
                    out.write(shootingBuf);
            } catch (IOException e) {
                System.out.println("Can not send info to server from EnemiesTimerTask: " + e.getMessage());
            }
        }
    }

    private void changeOrientation(TankState tank, Orientation orientation) {
        if (tank.getOrientation() != orientation) {
            tank.setOrientation(orientation);
            orientationBuf[3] = (byte) orientation.ordinal();
            changeOrientation = true;
        }
    }

    private void doMoving(int tankX, int tankY, Orientation orientation) {
        mapState.moveTankBlock(tankX, tankY, orientation);
        movingBuf[3] = (byte) orientation.ordinal();
        changePlace = true;
    }

    private void doShooting(Orientation orientation) {
        shootingBuf[3] = (byte) orientation.ordinal();
        doneShooting = true;
    }

}
