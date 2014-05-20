package ua.pp.condor.jbattlecity.network;

import ua.pp.condor.jbattlecity.area.Constants;
import ua.pp.condor.jbattlecity.area.MapState;
import ua.pp.condor.jbattlecity.tank.Orientation;
import ua.pp.condor.jbattlecity.tank.TankState;

import java.io.BufferedInputStream;
import java.io.IOException;

public class InputReader extends Thread {

    private final BufferedInputStream in;
    private final MapState mapState;

    public InputReader(BufferedInputStream in, MapState mapState) {
        this.in = in;
        this.mapState = mapState;
    }

    @Override
    public void run() {
        byte[] buf = new byte[Protocol.BUF_SIZE];
        try {
            while (in.read(buf, 0, Protocol.BUF_SIZE) >= 0) {
                TankState tank = null;
                if (buf[0] == Protocol.FRIEND) {
                    tank = mapState.getFriend();
                } else if (buf[0] == Protocol.ENEMY) {
                    int enemyId = buf[1];
                    tank = mapState.getEnemy(enemyId);
                } else if (buf[0] == Protocol.GAME_OVER) {
                    mapState.setGameOver(true);
                    interrupt();
                    break;
                }
                if (tank != null) {
                    Orientation orientation = Orientation.values()[buf[3]];
                    switch (buf[2]) {
                        case Protocol.ORIENTATION: {
                            tank.setOrientation(orientation);
                            break;
                        }
                        case Protocol.MOVING: { //TODO refactor this
                            switch (orientation) {
                                case UP:    {
                                    tank.setY(tank.getY() - Constants.TANK_STEP);
                                    mapState.moveTankBlock(tank.getX(), tank.getY(), Orientation.UP);
                                    break;
                                }
                                case RIGHT: {
                                    tank.setX(tank.getX() + Constants.TANK_STEP);
                                    mapState.moveTankBlock(tank.getX(), tank.getY(), Orientation.RIGHT);
                                    break;
                                }
                                case DOWN:  {
                                    tank.setY(tank.getY() + Constants.TANK_STEP);
                                    mapState.moveTankBlock(tank.getX(), tank.getY(), Orientation.DOWN);
                                    break;
                                }
                                case LEFT:  {
                                    tank.setX(tank.getX() - Constants.TANK_STEP);
                                    mapState.moveTankBlock(tank.getX(), tank.getY(), Orientation.LEFT);
                                    break;
                                }
                            }
                            break;
                        }
                        case Protocol.SHOOTING: {
                            mapState.addProjectile(tank);
                            break;
                        }
                        default: {
                            System.out.print("Unsupported operation: " + buf.toString());
                            break;
                        }
                    }
                } else if (buf[0] == Protocol.ENEMY && buf[2] == Protocol.ADD) {
                    int enemyId = buf[1];
                    mapState.addEnemy(enemyId);
                }
                NetworkUtils.bzero(buf);
            }
            interrupt();
        } catch (IOException e) {
            System.out.print("I/O Error in InputReader: " + e);
        }
    }
}
