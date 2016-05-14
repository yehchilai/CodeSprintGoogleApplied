package com.iamhomebody.codesprintgoogleapplied;

import android.graphics.Bitmap;
import android.graphics.RectF;

/**
 * Created by MarkLai on 5/14/16.
 */
public class Player {
    // RectF is an object that holds four coordinates - just what we need
    private RectF rect;

    // How long and high our paddle will be
    private float length;
    private float height;

    // X is the far left of the rectangle which forms our paddle
    private float x;

    // Y is the top coordinate
    private float y;

    // This will hold the pixels per second speedthat the paddle will move
    private float playerSpeed;

    // Which ways can the paddle move
    public final int STOPPED = 0;
    public final int LEFT = 1;
    public final int RIGHT = 2;
    public final int UP = 3;
    public final int DOWN = 4;


    // Is the paddle moving and in which direction
    private int playerMoving = STOPPED;

    // Declare an object of type Bitmap
    Bitmap bitmapPlayer;

    float screenY;
    float screenX;
    // This the the constructor method
    // When we create an object from this class we will pass
    // in the screen width and height
    public Player(int screenX, int screenY, Bitmap player){
        this.screenX = screenX;
        this.screenY = screenY;
        // 130 pixels wide and 20 pixels high
        length = 50;
        height = 50;

        // Start paddle in roughly the sceen centre
        x = screenX / 2;
        y = screenY / 2;

        rect = new RectF(x, y, x + length, y + height);

        // How fast is the paddle in pixels per second
        playerSpeed = 350;

        bitmapPlayer = player;
    }

    // This is a getter method to make the rectangle that
    // defines our paddle available in BreakoutView class
    public RectF getRect(){
        return rect;
    }

    public Bitmap getBitmapPlayer(){ return bitmapPlayer;}

    // This method will be used to change/set if the paddle is going left, right or nowhere
    public void setMovementState(int state){
        playerMoving = state;
    }

    // This update method will be called from update in BreakoutView
    // It determines if the paddle needs to move and changes the coordinates
    // contained in rect if necessary
    public void update(long fps){
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

        rect.left = x;
        rect.right = x + length;

        rect.top = y;
        rect.bottom = y + height;
    }
}
