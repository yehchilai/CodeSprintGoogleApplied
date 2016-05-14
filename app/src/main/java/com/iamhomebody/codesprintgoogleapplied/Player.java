package com.iamhomebody.codesprintgoogleapplied;

import android.graphics.RectF;

/**
 * Created by MarkLai on 5/14/16.
 */
public class Player {
    // RectF is an object that holds four coordinates - just what we need
    private RectF rect;

    // How long and high our player will be
    private float length;
    private float height;

    // X is the far left of the rectangle which forms our player
    private float x;

    // Y is the top coordinate
    private float y;

    // This will hold the pixels per second speedthat the player will move
    private float playerSpeed;

    // Which ways can the player move
    public final int STOPPED = 0;
    public final int LEFT = 1;
    public final int RIGHT = 2;
    public final int UP = 3;
    public final int DOWN = 4;

    // Is the player moving and in which direction
    private int playerMoving = STOPPED;

    float screenY;
    float screenX;

    /**
     *  This the the constructor method
     *  When we create an object from this class we will pass
     *  in the screen width and height
     * @param screenX
     * @param screenY
     */
    public Player(int screenX, int screenY){
        this.screenX = screenX;
        this.screenY = screenY;
        // 130 pixels wide and 20 pixels high
        length = 50;
        height = 50;

        // Start player in roughly the sceen centre
        x = screenX / 2;
        y = screenY / 2;

        rect = new RectF(x, y, x + length, y + height);

        // How fast is the player in pixels per second
        playerSpeed = 350;
    }

    /**
     *  This is a getter method to make the rectangle that
     *  defines our player available in BreakoutView class
     * @return RectF    player position
     */
    public RectF getRect(){
        return rect;
    }

    /**
     * This method will be used to change/set if the player is going left, right, up, down or nowhere
     * @param state     player moving direction (STOPPED, LEFT, RIGHT, UP, DOWN)
     */
    public void setMovementState(int state){
        playerMoving = state;
    }



    /**
     *  This update method will be called from update in BreakoutView
     *  It determines if the player needs to move and changes the coordinates
     *  contained in rect if necessary
     * @param fps   Frame per second
     */
    public void update(long fps){

        // update position
        if(playerMoving == LEFT && x > 0){
            x = x - playerSpeed / fps;
        }

        if(playerMoving == RIGHT && x < (screenX-length)){
            x = x + playerSpeed / fps;
        }

        if(playerMoving == UP && y >0){
            y = y - playerSpeed / fps;
        }

        if(playerMoving == DOWN && y < (screenY-height)){
            y = y + playerSpeed / fps;
        }

        // update collision
        rect.left = x;
        rect.right = x + length;

        rect.top = y;
        rect.bottom = y + height;
    }
}
