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
        int count;
        byte[] buf = new byte[10];
        try {
            while ((count = in.read(buf, 0, 3)) >= 0) {
                //TODO
                TankState tank = null;
                if (buf[0] == Protocol.FRIEND) {
                    tank = mapState.getFriend();
                }
                if (tank != null) {
                    Orientation orientation = Orientation.values()[buf[2]];
                    switch (buf[1]) {
                        case Protocol.ORIENTATION: {
                            tank.setOrientation(orientation);
                            break;
                        }
                        case Protocol.MOVING: {
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
                }
                NetworkUtils.bzero(buf);

            }
            interrupt();
        } catch (IOException e) {}
    }
}
