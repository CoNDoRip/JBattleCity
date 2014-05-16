package ua.pp.condor.jbattlecity.tank;

public class EnemyTankState extends TankState {

	public EnemyTankState(int x, int y, Orientation orientation) {
		super(x, y, orientation);
	}

	public TankColor getTankColor() {
		return TankColor.GREY;
	}

}
