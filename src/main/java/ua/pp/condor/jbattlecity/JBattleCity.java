package ua.pp.condor.jbattlecity;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JApplet;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class JBattleCity extends JApplet {

    @Override
    public void init() {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {

                @Override
                public void run() {
                    makeGUI();
                }
            });
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private void makeGUI() {
        GridBagLayout gBag = new GridBagLayout();
        setLayout(gBag);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weighty = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.NORTH;

        JLabel head = new JLabel("JBattleCity");
        gBag.setConstraints(head, gbc);
        add(head);
    }

}
