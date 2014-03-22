package ua.pp.condor.jbattlecity;

import javax.swing.JApplet;
import javax.swing.SwingUtilities;

import ua.pp.condor.jbattlecity.area.Area;
import ua.pp.condor.jbattlecity.area.maps.Stage1;
import ua.pp.condor.jbattlecity.utils.Sound;

public class JBattleCity extends JApplet {

	private static final long serialVersionUID = -6680546851758395165L;
	
	public static final int WIDTH = 520;
	public static final int HEIGHT = 520;

	@Override
    public void init() {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {

                public void run() {
                    makeGUI();
                }
            });
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private void makeGUI() {
    	setSize(WIDTH, HEIGHT);
    	new Sound(this);
    	add(new Area(new Stage1()));
    }

}
