package ua.pp.condor.jbattlecity.tank;

public class YouTankState extends TankState {

	public YouTankState(int x, int y, Orientation orientation) {
		super(x, y, orientation);
	}

	public TankColor getTankColor() {
		return TankColor.YELLOW;
	}

}
