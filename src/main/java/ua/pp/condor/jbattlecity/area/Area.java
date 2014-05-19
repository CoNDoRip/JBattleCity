package ua.pp.condor.jbattlecity.area;

import ua.pp.condor.jbattlecity.area.maps.IMap;
import ua.pp.condor.jbattlecity.network.InputReader;
import ua.pp.condor.jbattlecity.network.Protocol;
import ua.pp.condor.jbattlecity.tank.ProjectileState;
import ua.pp.condor.jbattlecity.tank.TankState;
import ua.pp.condor.jbattlecity.utils.Images;
import ua.pp.condor.jbattlecity.utils.Sound;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.MediaTracker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.TimerTask;

public class Area extends JPanel {

    private static final long serialVersionUID = -2993932675117489481L;

    private BufferedInputStream in;
    private OutputStream out;
    
    private final MapState mapState;
    
    public Area(Socket socket, IMap map) throws IOException {
        in = new BufferedInputStream(socket.getInputStream());
        out = socket.getOutputStream();

        mapState = new MapState(map, out);
        
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        
        final Timer repaintTimer = new Timer(10, new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                repaint();
            }
        });
        
        MediaTracker mt = new MediaTracker(this);
        mt.addImage(mapState.getMapImage(), 1);
        mt.addImage(Images.getYouUp(), 2);
        mt.addImage(Images.getYouRight(), 2);
        mt.addImage(Images.getYouDown(), 2);
        mt.addImage(Images.getYouLeft(), 2);
        mt.addImage(Images.getEnemyUp(), 3);
        mt.addImage(Images.getEnemyRight(), 3);
        mt.addImage(Images.getEnemyDown(), 3);
        mt.addImage(Images.getEnemyLeft(), 3);
        mt.addImage(Images.getFriendUp(), 4);
        mt.addImage(Images.getFriendRight(), 4);
        mt.addImage(Images.getFriendDown(), 4);
        mt.addImage(Images.getFriendLeft(), 4);
        mt.addImage(Images.getProjectile(), 5);
        mt.addImage(Images.getBang(0), 6);
        mt.addImage(Images.getBang(1), 6);
        mt.addImage(Images.getBang(2), 6);
        mt.addImage(Images.getBang(3), 6);
        mt.addImage(Images.getGameOver(), 9);
        try {
            mt.waitForAll();
        } catch (InterruptedException e) {}
        if (mt.isErrorAny())
            throw new IllegalStateException("Errors in images loading");

        final byte[] buf = new byte[2];
        int count = in.read(buf);
        if (count == 2 && buf[0] == Protocol.START_GAME) {
            Sound.getGameStart().play();
            new InputReader(in, mapState).start();
            new java.util.Timer().schedule(new TimerTask() {

                @Override
                public void run() {
                    repaintTimer.start();
                    mapState.startGame(buf[1]);
                    Sound.getBackground().loop();
                }
            }, 4000);
        } else
            throw new IOException("Incorrect code from server");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(mapState.getMapImage(), 0, 0, this);

        g.setColor(Color.BLACK);
        for (int x = 0; x < MapState.ARRAY_SIZE; x++) {
            for (int y = 0; y < MapState.ARRAY_SIZE; y++) {
                if (mapState.getCell(x, y) == Cell.empty || mapState.getCell(x, y) == Cell.tank)
                    g.fillRect(x * 10, y * 10, 10, 10);
            }
        }
        
        TankState you = mapState.getYou();
        if (you != null) {
            g.drawImage(Images.getTankImage(you), you.getX(), you.getY(), this);
        }

        TankState friend = mapState.getFriend();
        if (friend != null) {
            g.drawImage(Images.getTankImage(friend), friend.getX(), friend.getY(), this);
        }
        
        for (TankState enemy : mapState.getEnemies()) {
            g.drawImage(Images.getTankImage(enemy), enemy.getX(), enemy.getY(), this);
        }
        
        for (ProjectileState ps : mapState.getProjectiles()) {
            g.drawImage(Images.getProjectile(), ps.getX() - Images.PROJECTILE_SIZE, ps.getY() - Images.PROJECTILE_SIZE, this);
        }

        if (mapState.isGameOver())
            g.drawImage(Images.getGameOver(), 110, 160, this);
    }
    
}
