package ua.pp.condor.jbattlecity.tank;

import ua.pp.condor.jbattlecity.area.MapState;

public final class TanksFactory {

    private TanksFactory() {}

    public enum PlayerPosition {
        FIRST(160, 480),
        SECOND(320, 480);

        private final int x;
        private final int y;

        PlayerPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

    }

    public enum EnemyPosition {
        FIRST(0, 0),
        SECOND(240, 0),
        THIRD(480, 0);

        private final int x;
        private final int y;

        EnemyPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

    }

    public static TankState getYou(PlayerPosition pos, MapState mapState) {
        TankState tank = new TankState(pos.getX(), pos.getY(), Orientation.UP, TankColor.YELLOW);
        mapState.setTankBlock(pos.getX(), pos.getY());
        return tank;
    }

    public static TankState getFriend(PlayerPosition pos, MapState mapState) {
        TankState tank = new TankState(pos.getX(), pos.getY(), Orientation.UP, TankColor.GREEN);
        mapState.setTankBlock(pos.getX(), pos.getY());
        return tank;
    }

    public static TankState getEnemy(EnemyPosition pos, MapState mapState) {
        TankState tank = new TankState(pos.getX(), pos.getY(), Orientation.DOWN, TankColor.GREY);
        mapState.setTankBlock(pos.getX(), pos.getY());
        return tank;
    }

}
