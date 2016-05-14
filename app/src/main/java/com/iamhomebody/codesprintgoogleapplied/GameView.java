package com.iamhomebody.codesprintgoogleapplied;

/**
 * Created by MarkLai on 5/14/16.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

/**
 * Created by MarkLai on 5/13/16.
 */
public class GameView extends SurfaceView implements Runnable{
    // This is our thread
    Thread gameThread = null;

    // This is new. We need a SurfaceHolder
    // When we use Paint and Canvas in a thread
    // We will see it in action in the draw method soon.
    SurfaceHolder ourHolder;

    // A boolean which we will set and unset
    // when the game is running- or not.
    volatile boolean playing;

    // Game is paused at the start
    boolean paused = true;

    // A Canvas and a Paint object
    Canvas canvas;
    Paint paint;

    // This variable tracks the game frame rate
    long fps;

    // This is used to help calculate the fps
    private long timeThisFrame;

    // The size of the screen in pixels
    int screenX;
    int screenY;

    // The player
    Player player;
    Bitmap bitmapPlayer;

    // The enemies
    ArrayList<Enemy> enemies;
    Enemy enemy;
    Bitmap bitmapEnemy;

    // The score
    int score = 0;

    // Lives
    int lives = 3;

    // These next two values can be anything you like
    // As long as the ratio doesn't distort the sprite too much
    private int frameWidth = 50;
    private int frameHeight = 50;
    // How many frames are there on the sprite sheet?
    private int frameCount = 2;
    // Start at the first frame - where else?
    private int currentFrame = 0;

    // What time was it when we last changed frames
    private long lastFrameChangeTime = 0;

    // How long should each frame last
    private int frameLengthInMilliseconds = 100;
    private Rect frameToDraw = new Rect(
            0,
            0,
            frameWidth,
            frameHeight);

    private long currentTime;

    /**
     *  When the we initialize (call new()) on gameView
     *  This special constructor method runs
     * @param context   the context of the main activity
     * @param activity  main activity
     */
    public GameView(Context context, Activity activity) {
        // The next line of code asks the
        // SurfaceView class to set up our object.
        // How kind.
        super(context);

        // Initialize ourHolder and paint objects
        ourHolder = getHolder();
        paint = new Paint();

        // Get a Display object to access screen details
        Display display = activity.getWindowManager().getDefaultDisplay();
        // Load the resolution into a Point object
        Point size = new Point();
        display.getSize(size);

        screenX = size.x;
        screenY = size.y;
        bitmapPlayer = BitmapFactory.decodeResource(this.getResources(), R.drawable.player);
        bitmapPlayer = Bitmap.createScaledBitmap(bitmapPlayer,
                frameWidth * frameCount,
                frameHeight,
                false);



        bitmapEnemy = BitmapFactory.decodeResource(this.getResources(), R.drawable.enemy);
        enemies = new ArrayList<Enemy>();
        player = new Player(screenX, screenY);
        enemies.add(new Enemy(screenX, screenY));
//        enemy = new Enemy(screenX, screenY);
        // Reset scores and lives
        score = 0;
        lives = 3;

        setFocusable(true);

        currentTime = System.currentTimeMillis();
    }

    /**
     * Game Engine main loop
     */
    @Override
    public void run() {
        while (playing) {

            // Capture the current time in milliseconds in startFrameTime
            long startFrameTime = System.currentTimeMillis();

            // Update the frame
            if(!paused){
                update();
            }

            // Draw the frame
            draw();

            // Calculate the fps this frame
            // We can then use the result to
            // time animations and more.
            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1) {
                fps = 1000 / timeThisFrame;
            }

        }

    }

    /**
     *  Everything that needs to be updated goes in here
     *  Movement, collision detection etc.
     */
    public void update() {
        // Move the player if required
        player.update(fps);
        for(Enemy e : enemies){
            e.update(fps);
        }

        if(System.currentTimeMillis() - currentTime > 2000){
            enemies.add(new Enemy(screenX, screenY));
            currentTime = System.currentTimeMillis();
        }

        if(lives == 0){
            paused = true;
        }
    }

    /**
     * Draw the newly updated scene
     */
    public void draw() {

        // Make sure our drawing surface is valid or we crash
        if (ourHolder.getSurface().isValid()) {
            // Lock the canvas ready to draw
            canvas = ourHolder.lockCanvas();

            // Draw the background color
            canvas.drawColor(Color.argb(255,  255, 255, 255));
            // Draw Line
            paint.setColor(Color.argb(255,  255, 10, 10));
            canvas.drawRect(0,0,50, 780, paint);
            // Choose the brush color for drawing
            paint.setColor(Color.argb(255,  255, 255, 255));

            // Draw the player
            getCurrentFrame();
            canvas.drawBitmap(bitmapPlayer, frameToDraw, player.getRect(), paint);

            for(int i = enemies.size()-1; i>=0 ; i--){
                canvas.drawBitmap(bitmapEnemy, enemies.get(i).getRect().left, enemies.get(i).getRect().top, paint);
                if(enemies.get(i).getRect().left <=50 && paused == false){
                    enemies.remove(i);
                    lives--;
                }
            }


            // Draw the lives
            paint.setColor(Color.argb(255,  249, 129, 0));
            paint.setTextSize(40);
            canvas.drawText("   Lives: " + lives, 50,50, paint);
            // Has the player lost?
            if(lives <= 0){
                paint.setTextSize(90);
                canvas.drawText("YOU HAVE LOST!", 10,screenY/2, paint);
            }
            // Draw everything to the screen
            ourHolder.unlockCanvasAndPost(canvas);
        }

    }

    /**
     * If SimpleGameEngine Activity is paused/stopped
     * shutdown our thread.
     */
    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }

    }



    /**
     *  If SimpleGameEngine Activity is started theb
     *  start our thread.
     */
    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    /**
     *  The SurfaceView class implements onTouchListener
     *  So we can override this method and detect screen touches.
     * @param motionEvent
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            // Player has touched the screen
            case MotionEvent.ACTION_DOWN:
                Log.d("TOUCH", "MotionEvent.ACTION_DOWN");
                paused = false;

                if(motionEvent.getX() > screenX / 2){
                    player.setMovementState(player.RIGHT);
                }
                else{
                    player.setMovementState(player.LEFT);
                }
                break;

            // Player has removed finger from screen
            case MotionEvent.ACTION_UP:
                player.setMovementState(player.STOPPED);
                break;
        }
        return true;
    }

    /**
     * control player action
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        paused = false;
        switch (keyCode){
            case 19:
                // Up;
                player.setMovementState(player.UP);
                break;
            case 20:
                // Down
                player.setMovementState(player.DOWN);
                break;
            case 21:
                //Left
                player.setMovementState(player.LEFT);
                break;
            case 22:
                // Right
                player.setMovementState(player.RIGHT);
                break;
            case 62:
                // Attack (space)
                // Check for enemy colliding with player
                lastFrameChangeTime = System.currentTimeMillis();
                currentFrame = 1;
                for(int i = enemies.size() - 1; i >= 0; i--){
//                    Log.d("COLLISION", "Player:"+String.valueOf(player.getRect().toString()));
//                    Log.d("COLLISION", "Player:"+String.valueOf(enemies.get(i).getRect().toString()));
                    if(RectF.intersects(player.getRect(),enemies.get(i).getRect())) {
                        enemies.remove(i);
                    }
                }
                player.setMovementState(player.STOPPED);
                break;
        }
        return true;
    }

    /**
     * enforce the player to stop when key up
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        Log.d("KEY_UP", String.valueOf(event.getAction()));
        player.setMovementState(player.STOPPED);
        return true;
    }

    /**
     * Get player attacking animation frame
     */
    public void getCurrentFrame(){

        long time  = System.currentTimeMillis();
        if ( time > lastFrameChangeTime + frameLengthInMilliseconds) {
            currentFrame = 0;
        }
        //update the left and right values of the source of
        //the next frame on the spritesheet
        frameToDraw.left = currentFrame * frameWidth;
        frameToDraw.right = frameToDraw.left + frameWidth;

    }

}

