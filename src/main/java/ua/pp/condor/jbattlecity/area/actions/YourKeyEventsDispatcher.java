package ua.pp.condor.jbattlecity.area.actions;

import ua.pp.condor.jbattlecity.JBattleCity;
import ua.pp.condor.jbattlecity.area.Cell;
import ua.pp.condor.jbattlecity.area.Constants;
import ua.pp.condor.jbattlecity.area.MapState;
import ua.pp.condor.jbattlecity.network.Protocol;
import ua.pp.condor.jbattlecity.tank.Orientation;
import ua.pp.condor.jbattlecity.tank.TankState;
import ua.pp.condor.jbattlecity.utils.Sound;

import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.OutputStream;

public class YourKeyEventsDispatcher implements KeyEventDispatcher {

    private final MapState mapState;

    private final byte[] orientationBuf;
    private final byte[] movingBuf;
    private final byte[] shootingBuf;

    private boolean changeOrientation;
    private boolean changePlace;
    private boolean doneShooting;

    public YourKeyEventsDispatcher(MapState mapState) {
        this.mapState = mapState;

        orientationBuf = new byte[Protocol.BUF_SIZE];
        orientationBuf[2] = Protocol.ORIENTATION;
        movingBuf = new byte[Protocol.BUF_SIZE];
        movingBuf[2] = Protocol.MOVING;
        shootingBuf = new byte[Protocol.BUF_SIZE];
        shootingBuf[2] = Protocol.SHOOTING;
        orientationBuf[0] = movingBuf[0] = shootingBuf[0] = Protocol.FRIEND;
    }

    public void setId(int id) {
        orientationBuf[1] = movingBuf[1] = shootingBuf[1] = (byte) id;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent arg0) {
        if (arg0.getID() == KeyEvent.KEY_PRESSED) {
            final int delta = Constants.TANK_STEP;

            TankState you = mapState.getYou();

            int tankX = you.getX();
            int tankY = you.getY();

            int oldXCell = tankX / 10;
            int incXCell = (tankX + delta) / 10;
            int decXCell = (tankX - delta) / 10;
            int oldYCell = tankY / 10;
            int incYCell = (tankY + delta) / 10;
            int decYCell = (tankY - delta) / 10;

            changeOrientation = changePlace = doneShooting = false;

            switch (arg0.getKeyCode()) {
                case KeyEvent.VK_UP: {
                    changeOrientation(you, Orientation.UP);

                    if (tankY - delta >= 0
                            && mapState.getCell(oldXCell, decYCell) == Cell.empty
                            && mapState.getCell(oldXCell + 1, decYCell) == Cell.empty
                            && mapState.getCell(oldXCell + 2, decYCell) == Cell.empty
                            && mapState.getCell(oldXCell + 3, decYCell) == Cell.empty) {
                        tankY -= delta;
                        doMoving(Orientation.UP);
                        mapState.moveTankBlock(tankX, tankY, Orientation.UP);
                    }
                    break;
                }
                case KeyEvent.VK_RIGHT: {
                    changeOrientation(you, Orientation.RIGHT);

                    if (tankX + delta <= JBattleCity.HEIGHT - MapState.BLOCK_SIZE_PIXEL
                            && mapState.getCell(incXCell + 3, oldYCell) == Cell.empty
                            && mapState.getCell(incXCell + 3, oldYCell + 1) == Cell.empty
                            && mapState.getCell(incXCell + 3, oldYCell + 2) == Cell.empty
                            && mapState.getCell(incXCell + 3, oldYCell + 3) == Cell.empty) {
                        tankX += delta;
                        doMoving(Orientation.RIGHT);
                        mapState.moveTankBlock(tankX, tankY, Orientation.RIGHT);
                    }
                    break;
                }
                case KeyEvent.VK_DOWN: {
                    changeOrientation(you, Orientation.DOWN);

                    if (tankY + delta <= JBattleCity.WIDTH - MapState.BLOCK_SIZE_PIXEL
                            && mapState.getCell(oldXCell, incYCell + 3) == Cell.empty
                            && mapState.getCell(oldXCell + 1, incYCell + 3) == Cell.empty
                            && mapState.getCell(oldXCell + 2, incYCell + 3) == Cell.empty
                            && mapState.getCell(oldXCell + 3, incYCell + 3) == Cell.empty) {
                        tankY += delta;
                        doMoving(Orientation.DOWN);
                        mapState.moveTankBlock(tankX, tankY, Orientation.DOWN);
                    }
                    break;
                }
                case KeyEvent.VK_LEFT: {
                    changeOrientation(you, Orientation.LEFT);

                    if (tankX - delta >= 0
                            && mapState.getCell(decXCell, oldYCell) == Cell.empty
                            && mapState.getCell(decXCell, oldYCell + 1) == Cell.empty
                            && mapState.getCell(decXCell, oldYCell + 2) == Cell.empty
                            && mapState.getCell(decXCell, oldYCell + 3) == Cell.empty) {
                        tankX -= delta;
                        doMoving(Orientation.LEFT);
                        mapState.moveTankBlock(tankX, tankY, Orientation.LEFT);
                    }
                    break;
                }
                case KeyEvent.VK_CAPS_LOCK: {
                    if (you.isHasProjectile()) break;
                    mapState.addProjectile(you);
                    doShooting(you.getOrientation());

                    Sound.getFire().play();
                }
            }

            you.setX(tankX);
            you.setY(tankY);

            try {
                OutputStream out = mapState.getOut();
                if (changeOrientation)
                    out.write(orientationBuf);
                if (changePlace)
                    out.write(movingBuf);
                if (doneShooting)
                    out.write(shootingBuf);
            } catch (IOException e) {
                System.out.println("Can not send info to server from YourKeyEventDispatcher: " + e.getMessage());
            }
        }

        return false;
    }

    private void changeOrientation(TankState tank, Orientation orientation) {
        if (tank.getOrientation() != orientation) {
            tank.setOrientation(orientation);
            orientationBuf[3] = (byte) orientation.ordinal();
            changeOrientation = true;
        }
    }

    private void doMoving(Orientation orientation) {
        movingBuf[3] = (byte) orientation.ordinal();
        changePlace = true;
    }

    private void doShooting(Orientation orientation) {
        shootingBuf[3] = (byte) orientation.ordinal();
        doneShooting = true;
    }

}
