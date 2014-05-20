package ua.pp.condor.jbattlecity;

public class Protocol {

    public static final int BUF_SIZE = 4;

    public static final byte GAME_OVER = 0;
    public static final byte START_GAME = 1;

    public static final byte FRIEND = 10;
    public static final byte ENEMY = 11;

    public static final byte ORIENTATION = 100;
    public static final byte MOVING = 101;
    public static final byte SHOOTING = 102;
    public static final byte ADD = 103;

    public static final byte[] GAME_OVER_BUF = { GAME_OVER, 0, 0, 0 };

}
