package ua.pp.condor.jbattlecity.area.actions;

import ua.pp.condor.jbattlecity.JBattleCity;
import ua.pp.condor.jbattlecity.area.Cell;
import ua.pp.condor.jbattlecity.area.MapState;
import ua.pp.condor.jbattlecity.tank.ProjectileState;
import ua.pp.condor.jbattlecity.utils.Images;
import ua.pp.condor.jbattlecity.utils.Sound;

import java.util.Map;
import java.util.Set;
import java.util.TimerTask;

public class ProjectilesTimerTask extends TimerTask {

    private final MapState mapState;
    private final Cell[][] currentMap;

    public ProjectilesTimerTask(MapState mapState) {
        this.mapState = mapState;
        currentMap = mapState.getCurrentMap();
    }

    @Override
    public void run() {
        int delta = 5;

        final Map<Integer, ProjectileState> projectiles = mapState.getProjectilesMap();
        Set<Integer> projectilesIds = projectiles.keySet();

        for (Integer projectileId : projectilesIds) {
            ProjectileState ps = projectiles.get(projectileId);
            if (ps.getX() < Images.PROJECTILE_SIZE || ps.getX() > JBattleCity.WIDTH  - Images.PROJECTILE_SIZE - delta
                    || ps.getY() < Images.PROJECTILE_SIZE || ps.getY() > JBattleCity.HEIGHT - Images.PROJECTILE_SIZE - delta) {
                ps.getParent().setHasProjectile(false);
                projectiles.remove(projectileId);
                continue;
            }

            int x = 0, y = 0;
            int x1 = 0, y1 = 0;
            int x2 = 0, y2 = 0;
            int x3 = 0, y3 = 0;
            switch (ps.getOrientation()) {
                case UP: {
                    int newY = ps.getY() - delta;
                    x = ps.getX() / 10; y = newY / 10;
                    x1 = x - 1; y1 = y;
                    x2 = x - 2; y2 = y;
                    x3 = x + 1; y3 = y;
                    ps.setY(newY);
                    break;
                }
                case RIGHT: {
                    int newX = ps.getX() + delta;
                    x = newX / 10; y = ps.getY() / 10;
                    x1 = x; y1 = y - 1;
                    x2 = x; y2 = y - 2;
                    x3 = x; y3 = y + 1;
                    ps.setX(newX);
                    break;
                }
                case DOWN: {
                    int newY = ps.getY() + delta;
                    x = ps.getX() / 10; y = newY / 10;
                    x1 = x - 1; y1 = y;
                    x2 = x - 2; y2 = y;
                    x3 = x + 1; y3 = y;
                    ps.setY(newY);
                    break;
                }
                case LEFT: {
                    int newX = ps.getX() - delta;
                    x = newX / 10; y = ps.getY() / 10;
                    x1 = x; y1 = y - 1;
                    x2 = x; y2 = y - 2;
                    x3 = x; y3 = y + 1;
                    ps.setX(newX);
                    break;
                }
            }
            boolean destroyed = false;
            if (mapState.getCell(x, y) != Cell.empty || mapState.getCell(x1, y1) != Cell.empty) {
                ps.getParent().setHasProjectile(false);
                projectiles.remove(projectileId);
            }
            if (mapState.getCell(x, y) == Cell.base || mapState.getCell(x1, y1) == Cell.base) {
                mapState.setGameOver();
            }
            if (mapState.getCell(x, y) == Cell.tank || mapState.getCell(x1, y1) == Cell.tank) {
                mapState.destroyTank(x, y, x1, y1);
            }
            if (mapState.getCell(x, y) == Cell.wall) {
                currentMap[x][y] = Cell.empty;
                destroyed = true;
            }
            if (mapState.getCell(x1, y1) == Cell.wall) {
                currentMap[x1][y1] = Cell.empty;
                destroyed = true;
            }
            if (destroyed) {
                if (mapState.getCell(x2, y2) == Cell.wall)
                    currentMap[x2][y2] = Cell.empty;
                if (mapState.getCell(x3, y3) == Cell.wall)
                    currentMap[x3][y3] = Cell.empty;

                Sound.getBrick().play();
            }
        }
    }
}
