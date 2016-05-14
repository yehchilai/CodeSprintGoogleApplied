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

    // The players player

    Player player;

    ArrayList<Enemy> enemys;
    Enemy enemy;
    Bitmap bitmapEnemy;

    // A ball
//    Ball ball;

    // Up to 200 bricks
//    Brick[] bricks = new Brick[200];
    int numBricks = 0;

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

    // When the we initialize (call new()) on gameView
    // This special constructor method runs
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
        Bitmap bitmapPlayer = BitmapFactory.decodeResource(this.getResources(), R.drawable.player);
        bitmapPlayer = Bitmap.createScaledBitmap(bitmapPlayer,
                frameWidth * frameCount,
                frameHeight,
                false);



        bitmapEnemy = BitmapFactory.decodeResource(this.getResources(), R.drawable.enemy);
        enemys = new ArrayList<Enemy>();
        player = new Player(screenX, screenY, bitmapPlayer);
        enemys.add(new Enemy(screenX, screenY));
//        enemy = new Enemy(screenX, screenY);
        // Create a ball
//        ball = new Ball(screenX, screenY);

        createBricksAndRestart();
        // Reset scores and lives
        score = 0;
        lives = 3;

        setFocusable(true);

        currentTime = System.currentTimeMillis();
    }

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

    // Everything that needs to be updated goes in here
    // Movement, collision detection etc.
    public void update() {
        // Move the player if required
        player.update(fps);
        for(Enemy e : enemys){
            e.update(fps);
        }

        if(System.currentTimeMillis() - currentTime > 2000){
            enemys.add(new Enemy(screenX, screenY));
            currentTime = System.currentTimeMillis();
        }

        if(lives == 0){
            paused = true;
        }
    }

    // Draw the newly updated scene
    public void draw() {

        // Make sure our drawing surface is valid or we crash
        if (ourHolder.getSurface().isValid()) {
            // Lock the canvas ready to draw
            canvas = ourHolder.lockCanvas();

            // Draw the background color
//            canvas.drawColor(Color.argb(255,  26, 128, 182));
            canvas.drawColor(Color.argb(255,  255, 255, 255));
            // Draw Line
            paint.setColor(Color.argb(255,  255, 10, 10));
            canvas.drawRect(0,0,50, 780, paint);
            // Choose the brush color for drawing
            paint.setColor(Color.argb(255,  255, 255, 255));

            // Draw the player
            getCurrentFrame();
            canvas.drawBitmap(player.getBitmapPlayer(), frameToDraw, player.getRect(), paint);

            for(int i = enemys.size()-1; i>=0 ; i--){
                canvas.drawBitmap(bitmapEnemy, enemys.get(i).getRect().left, enemys.get(i).getRect().top, paint);
//                Log.d("ENEMY",String.valueOf(enemys.get(i).getRect().top));
                if(enemys.get(i).getRect().left <=50 && paused == false){
                    enemys.remove(i);
                    lives--;
                }
            }
            // Draw the ball
//            canvas.drawRect(ball.getRect(), paint);
            // Draw the bricks
            // Change the brush color for drawing
            paint.setColor(Color.argb(255,  249, 129, 0));

            // Draw the score
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

    // If SimpleGameEngine Activity is paused/stopped
    // shutdown our thread.
    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }

    }

    // If SimpleGameEngine Activity is started theb
    // start our thread.
    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    // The SurfaceView class implements onTouchListener
    // So we can override this method and detect screen touches.
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

    public void createBricksAndRestart(){
//        int brickWidth = screenX / 8;
//        int brickHeight = screenY / 10;
//
//        // Build a wall of bricks
//        numBricks = 0;
//
//        for(int column = 0; column < 8; column ++ ){
//            for(int row = 0; row < 3; row ++ ){
//                bricks[numBricks] = new Brick(row, column, brickWidth, brickHeight);
//                numBricks ++;
//            }
//        }
//        // Put the ball back to the start
//        ball.reset(screenX, screenY);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        paused = false;

//        Log.d("KEY_DOWN", String.valueOf(event.getAction()));
//        Log.d("KEY", String.valueOf(keyCode));
//        player.setMovementState(player.UP);
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
                // Check for enemy colliding with player
                lastFrameChangeTime = System.currentTimeMillis();
                currentFrame = 1;
                for(int i = enemys.size() - 1; i >= 0; i--){
                    Log.d("COLLISION", "Player:"+String.valueOf(player.getRect().toString()));
                    Log.d("COLLISION", "Player:"+String.valueOf(enemys.get(i).getRect().toString()));
                    if(RectF.intersects(player.getRect(),enemys.get(i).getRect())) {
                        enemys.remove(i);
                    }
                }
                player.setMovementState(player.STOPPED);
                break;
        }
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent msg) {
        Log.d("KEY_UP", String.valueOf(msg.getAction()));
        player.setMovementState(player.STOPPED);
        return true;
    }

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

