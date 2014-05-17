package ua.pp.condor.jbattlecity.utils;

import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;

import ua.pp.condor.jbattlecity.tank.Orientation;
import ua.pp.condor.jbattlecity.tank.TankColor;

public final class Images {
    
    private static final String STAGE_1 = "/img/stage_1.gif";
    
    private static final String YOU_UP    = "/img/you_up.gif";
    private static final String YOU_RIGHT = "/img/you_right.gif";
    private static final String YOU_DOWN  = "/img/you_down.gif";
    private static final String YOU_LEFT  = "/img/you_left.gif";
    
    private static final String ENEMY_UP    = "/img/enemy_up.gif";
    private static final String ENEMY_RIGHT = "/img/enemy_right.gif";
    private static final String ENEMY_DOWN  = "/img/enemy_down.gif";
    private static final String ENEMY_LEFT  = "/img/enemy_left.gif";

    private static final String FRIEND_UP    = "/img/friend_up.gif";
    private static final String FRIEND_RIGHT = "/img/friend_right.gif";
    private static final String FRIEND_DOWN  = "/img/friend_down.gif";
    private static final String FRIEND_LEFT  = "/img/friend_left.gif";
    
    private static final String PROJECTILE  = "/img/projectile.gif";
    public static final int PROJECTILE_SIZE  = 3;

    private static final String BANG_0  = "/img/bang0.gif";
    private static final String BANG_1  = "/img/bang1.gif";
    private static final String BANG_2  = "/img/bang2.gif";
    private static final String BANG_3  = "/img/bang3.gif";
    
    private static final String GAME_OVER  = "/img/game_over.gif";

    private static Image stage1;
    
    private static Image youUp;
    private static Image youRight;
    private static Image youDown;
    private static Image youLeft;
    
    private static Image enemyUp;
    private static Image enemyRight;
    private static Image enemyDown;
    private static Image enemyLeft;

    private static Image friendUp;
    private static Image friendRight;
    private static Image friendDown;
    private static Image friendLeft;
    
    private static Image projectile;

    private static Image bang0;
    private static Image bang1;
    private static Image bang2;
    private static Image bang3;
    
    private static Image gameOver;

    private Images() {
    }

    public static Image getImage(String name) {
        Image image = null;
        try {
            image = ImageIO.read(Images.class.getResourceAsStream(name));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
    
    public static Image getStage1() {
        if (stage1 == null)
            stage1 = getImage(STAGE_1);
        return stage1;
    }
    
    public static Image getYouUp() {
        if (youUp == null)
            youUp = getImage(YOU_UP);
        return youUp;
    }
    
    public static Image getYouRight() {
        if (youRight == null)
            youRight = getImage(YOU_RIGHT);
        return youRight;
    }
    
    public static Image getYouDown() {
        if (youDown == null)
            youDown = getImage(YOU_DOWN);
        return youDown;
    }
    
    public static Image getYouLeft() {
        if (youLeft == null)
            youLeft = getImage(YOU_LEFT);
        return youLeft;
    }

    public static Image getEnemyUp() {
        if (enemyUp == null)
            enemyUp = getImage(ENEMY_UP);
        return enemyUp;
    }

    public static Image getEnemyRight() {
        if (enemyRight == null)
            enemyRight = getImage(ENEMY_RIGHT);
        return enemyRight;
    }

    public static Image getEnemyDown() {
        if (enemyDown == null)
            enemyDown = getImage(ENEMY_DOWN);
        return enemyDown;
    }

    public static Image getEnemyLeft() {
        if (enemyLeft == null)
            enemyLeft = getImage(ENEMY_LEFT);
        return enemyLeft;
    }

    public static Image getFriendUp() {
        if (friendUp == null)
            friendUp = getImage(FRIEND_UP);
        return friendUp;
    }

    public static Image getFriendRight() {
        if (friendRight == null)
            friendRight = getImage(FRIEND_RIGHT);
        return friendRight;
    }

    public static Image getFriendDown() {
        if (friendDown == null)
            friendDown = getImage(FRIEND_DOWN);
        return friendDown;
    }

    public static Image getFriendLeft() {
        if (friendLeft == null)
            friendLeft = getImage(FRIEND_LEFT);
        return friendLeft;
    }

    public static Image getProjectile() {
        if (projectile == null)
            projectile = getImage(PROJECTILE);
        return projectile;
    }

    public static Image getBang(int i) {
        switch (i) {
            case 0: if (bang0 == null)
                        bang0 = getImage(BANG_0);
                    return bang0;
            case 1: if (bang1 == null)
                        bang1 = getImage(BANG_1);
                    return bang1;
            case 2: if (bang2 == null)
                        bang2 = getImage(BANG_2);
                    return bang2;
            case 3: if (bang3 == null)
                        bang3 = getImage(BANG_3);
                    return bang3;
            default: return null;
        }
    }

    public static Image getGameOver() {
        if (gameOver == null)
            gameOver = getImage(GAME_OVER);
        return gameOver;
    }
    
    public static Image getTankImage(Orientation orientation, TankColor color) {
        switch (orientation) {
            case UP: {
                switch (color) {
                    case YELLOW: return getYouUp();
                    case GREY: return getEnemyUp();
                    case GREEN: return getFriendUp();
                }
                break;
            }
            case RIGHT: {
                switch (color) {
                    case YELLOW: return getYouRight();
                    case GREY: return getEnemyRight();
                    case GREEN: return getFriendRight();
                }
                break;
            }
            case DOWN: {
                switch (color) {
                    case YELLOW: return getYouDown();
                    case GREY: return getEnemyDown();
                    case GREEN: return getFriendDown();
                }
                break;
            }
            case LEFT: {
                switch (color) {
                    case YELLOW: return getYouLeft();
                    case GREY: return getEnemyLeft();
                    case GREEN: return getFriendLeft();
                }
                break;
            }
        }
        return null;
    }

}
