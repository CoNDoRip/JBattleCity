package ua.pp.condor.jbattlecity.area.actions;

import ua.pp.condor.jbattlecity.JBattleCity;
import ua.pp.condor.jbattlecity.area.Cell;
import ua.pp.condor.jbattlecity.area.Constants;
import ua.pp.condor.jbattlecity.area.MapState;
import ua.pp.condor.jbattlecity.network.Protocol;
import ua.pp.condor.jbattlecity.tank.Orientation;
import ua.pp.condor.jbattlecity.tank.ProjectileState;
import ua.pp.condor.jbattlecity.tank.TankState;
import ua.pp.condor.jbattlecity.utils.Sound;

import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.OutputStream;

public class YourKeyEventsDispatcher implements KeyEventDispatcher {

    private final MapState mapState;
    private final Cell[][] currentMap;

    private final byte[] orientationBuf;
    private final byte[] movingBuf;

    private boolean changeOrientation;
    private boolean changePlace;

    public YourKeyEventsDispatcher(MapState mapState) {
        this.mapState = mapState;
        currentMap = mapState.getCurrentMap();

        orientationBuf = new byte[3];
        orientationBuf[1] = Protocol.ORIENTATION;
        movingBuf = new byte[3];
        movingBuf[1] = Protocol.MOVING;
        orientationBuf[0] = movingBuf[0] = Protocol.FRIEND;
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

            changeOrientation = false;
            changePlace = false;

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
                    mapState.addProjectile(ps);

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
            } catch (IOException e) {
                System.out.println("Can not send info to server in YourKeyEventDispatcher: " + e.getMessage());
            }
        }

        return false;
    }

    private void changeOrientation(TankState you, Orientation orientation) {
        if (you.getOrientation() != orientation) {
            you.setOrientation(orientation);
            orientationBuf[2] = (byte) orientation.ordinal();
            changeOrientation = true;
        }
    }

    private void doMoving(Orientation orientation) {
        movingBuf[2] = (byte) orientation.ordinal();
        changePlace = true;
    }

}
