package ua.pp.condor.jbattlecity.tank;

public enum TankColor {
	
	YELLOW,
	GREEN,
	GREY;
	
	public TankColor getNext() {
		switch (this) {
			case YELLOW: return TankColor.GREEN;
			case GREEN: return TankColor.GREY;
			case GREY: return TankColor.YELLOW;
			default: return null;
		}
	}

}
