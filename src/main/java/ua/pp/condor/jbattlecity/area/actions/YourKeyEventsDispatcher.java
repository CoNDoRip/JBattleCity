package ua.pp.condor.jbattlecity.area.actions;

import ua.pp.condor.jbattlecity.JBattleCity;
import ua.pp.condor.jbattlecity.area.Cell;
import ua.pp.condor.jbattlecity.area.MapState;
import ua.pp.condor.jbattlecity.tank.Orientation;
import ua.pp.condor.jbattlecity.tank.ProjectileState;
import ua.pp.condor.jbattlecity.tank.YouTankState;
import ua.pp.condor.jbattlecity.utils.Sound;

import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;

public class YourKeyEventsDispatcher implements KeyEventDispatcher {

    private final MapState mapState;
    private final Cell[][] currentMap;

    public YourKeyEventsDispatcher(MapState mapState) {
        this.mapState = mapState;
        currentMap = mapState.getCurrentMap();
    }

    public boolean dispatchKeyEvent(KeyEvent arg0) {
        if (arg0.getID() == KeyEvent.KEY_PRESSED) {
            final int delta = 10;

            YouTankState you = mapState.getYou();

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
                            && mapState.getCell(oldXCell, decYCell) == Cell.empty
                            && mapState.getCell(oldXCell + 1, decYCell) == Cell.empty
                            && mapState.getCell(oldXCell + 2, decYCell) == Cell.empty
                            && mapState.getCell(oldXCell + 3, decYCell) == Cell.empty) {
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
                            && mapState.getCell(incXCell + 3, oldYCell) == Cell.empty
                            && mapState.getCell(incXCell + 3, oldYCell + 1) == Cell.empty
                            && mapState.getCell(incXCell + 3, oldYCell + 2) == Cell.empty
                            && mapState.getCell(incXCell + 3, oldYCell + 3) == Cell.empty) {
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
                            && mapState.getCell(oldXCell, incYCell + 3) == Cell.empty
                            && mapState.getCell(oldXCell + 1, incYCell + 3) == Cell.empty
                            && mapState.getCell(oldXCell + 2, incYCell + 3) == Cell.empty
                            && mapState.getCell(oldXCell + 3, incYCell + 3) == Cell.empty) {
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
                            && mapState.getCell(decXCell, oldYCell) == Cell.empty
                            && mapState.getCell(decXCell, oldYCell + 1) == Cell.empty
                            && mapState.getCell(decXCell, oldYCell + 2) == Cell.empty
                            && mapState.getCell(decXCell, oldYCell + 3) == Cell.empty) {
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
                    mapState.addProjectile(ps);

                    Sound.getFire().play();
                }
            }

            you.setX(tankX);
            you.setY(tankY);
        }

        return false;
    }
}
