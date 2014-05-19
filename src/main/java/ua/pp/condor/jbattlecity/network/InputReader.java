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
                if (buf[0] == Protocol.FRIEND) {
                    TankState friend = mapState.getFriend();
                    Orientation orientation = Orientation.values()[buf[2]];
                    switch (buf[1]) {
                        case Protocol.ORIENTATION: {
                            friend.setOrientation(orientation);
                            break;
                        }
                        case Protocol.MOVING: {
                            switch (orientation) {
                                case UP:    {
                                    friend.setY(friend.getY() - Constants.TANK_STEP);
                                    mapState.moveTankBlock(friend.getX(), friend.getY(), Orientation.UP);
                                    break;
                                }
                                case RIGHT: {
                                    friend.setX(friend.getX() + Constants.TANK_STEP);
                                    mapState.moveTankBlock(friend.getX(), friend.getY(), Orientation.RIGHT);
                                    break;
                                }
                                case DOWN:  {
                                    friend.setY(friend.getY() + Constants.TANK_STEP);
                                    mapState.moveTankBlock(friend.getX(), friend.getY(), Orientation.DOWN);
                                    break;
                                }
                                case LEFT:  {
                                    friend.setX(friend.getX() - Constants.TANK_STEP);
                                    mapState.moveTankBlock(friend.getX(), friend.getY(), Orientation.LEFT);
                                    break;
                                }
                            }
                            break;
                        }
                        case Protocol.SHOOTING: {
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
