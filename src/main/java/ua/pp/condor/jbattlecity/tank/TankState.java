package ua.pp.condor.jbattlecity.tank;

public class TankState extends ItemState {
	
	private boolean hasProjectile;
	
	public TankState(int x, int y, Orientation orientation) {
		super(x, y, orientation);
	}

	public boolean isHasProjectile() {
		return hasProjectile;
	}

	public void setHasProjectile(boolean hasProjectile) {
		this.hasProjectile = hasProjectile;
	}

}
