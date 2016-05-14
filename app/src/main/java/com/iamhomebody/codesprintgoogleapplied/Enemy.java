package com.iamhomebody.codesprintgoogleapplied;

import android.graphics.RectF;

import java.util.Random;

/**
 * Created by MarkLai on 5/14/16.
 */
public class Enemy {
    // RectF is an object that holds four coordinates - just what we need
    private RectF rect;

    // How long and high our enemy will be
    private float length;
    private float height;

    // X is the far left of the rectangle which forms our enemy
    private float x;

    // Y is the top coordinate
    private float y;

    // This will hold the pixels per second speedthat the enemy will move
    private float enemySpeed;

    /**
     * This the the constructor method
     * When we create an object from this class we will pass
     * in the screen width and height
     * @param screenX   int     device screen X-axis size
     * @param screenY   int     device screen Y-axis size
     */
    public Enemy(int screenX, int screenY){
        // 130 pixels wide and 20 pixels high
        length = 50;
        height = 50;

        // Start enemy in roughly the sceen centre
        Random random = new Random();

        y = random.nextInt((int)(screenY - height));//screenX - length;
        x = screenX- length;//screenY - height;

        rect = new RectF(x, y, x + length, y + height);

        // How fast is the enemy in pixels per second
        enemySpeed = 100;

    }

    /**
     *  This is a getter method to make the rectangle that
     *  defines our enemy available in BreakoutView class
     * @return  RectF   enemy position
     */
    public RectF getRect(){
        return rect;
    }

    /**
     *  This update method will be called from update in BreakoutView
     *  It determines if the enemy needs to move and changes the coordinates
     *  contained in rect if necessary
     * @param fps   frame per second
     */
    public void update(long fps){
        // update position
        x = x - enemySpeed / fps;

        // update collision
        rect.left = x;
        rect.right = x + length;

        rect.top = y;
        rect.bottom = y + height;
    }
}
