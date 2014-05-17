package ua.pp.condor.jbattlecity.tank;

public class ProjectileState extends ItemState {
    
    private TankState parent;

    public TankState getParent() {
        return parent;
    }

    public void setParent(TankState parent) {
        this.parent = parent;
        parent.setHasProjectile(true);
    }

}
