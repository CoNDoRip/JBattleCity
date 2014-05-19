package ua.pp.condor.jbattlecity.tank;

public class TankState extends ItemState {

    private final TankColor color;
    private boolean hasProjectile;
    
    public TankState(int x, int y, Orientation orientation, TankColor color) {
        super(x, y, orientation);
        this.color = color;
    }

    public TankColor getTankColor() {
        return color;
    }

    public boolean isHasProjectile() {
        return hasProjectile;
    }

    public void setHasProjectile(boolean hasProjectile) {
        this.hasProjectile = hasProjectile;
    }

}
