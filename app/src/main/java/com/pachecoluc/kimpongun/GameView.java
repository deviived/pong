package com.pachecoluc.kimpongun;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import static android.graphics.BitmapFactory.decodeResource;

public class GameView extends View implements View.OnTouchListener {

    private Paint paint;
    private MyScreen myScreen = new MyScreen(100,100);
    private float doigt=300;

    float textSize;
    Intent gameOverIntent;
    boolean fail = false;
    float acceleration;

    //SOUNDS
    private MediaPlayer opening;
    private MediaPlayer bruitage;
    //private SoundPool soundPool;
    private int soundID;
    boolean plays = false, loaded = false;


    //SCREEN ELEMENTS
    private Ennemi ennemi = new Ennemi(200, 0, 2, 0);
    private Ballon ballon = new Ballon(200, 150, 10, 8, 50, Color.BLUE);

    //BITMAP
    private Bitmap nuage;
    private Bitmap boule;
    private Bitmap bouleResized;
    private Bitmap nuageResized;
    private Bitmap nuageEnnemi;
    private Bitmap nuageResizedEnnemi;


    public GameView(Context context) {
        super(context);

        //SET RESOURCES
        paint = new Paint();
        this.setBackgroundResource(R.drawable.fond_db);

        //CLOUDS
        nuage = BitmapFactory.decodeResource(getResources(), R.drawable.nuagemagique);
        nuageResized = Bitmap.createScaledBitmap(nuage, 269, 128, false);
        nuageEnnemi = BitmapFactory.decodeResource(getResources(), R.drawable.nuage_ennemi);
        nuageResizedEnnemi = Bitmap.createScaledBitmap(nuageEnnemi, 269, 128, false);

        //BALL
        boule = BitmapFactory.decodeResource(getResources(), R.drawable.bouledudragon);
        bouleResized = Bitmap.createScaledBitmap(boule, 90, 90, false);

        //SOUNDS
        opening = MediaPlayer.create(getContext(), R.raw.opening_db);
        bruitage = MediaPlayer.create(getContext(), R.raw.sound_effect_punch);

        // Load the sounds
/*
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loaded = true;
            }
        });
        soundID = soundPool.load(getContext(), R.raw.sound_effect_punch, 1);
*/
        opening.start();
        bruitage.start();
        gameOverIntent = new Intent(getContext(), GameOver.class);
        this.setOnTouchListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //GET CANVAS SIZE
        myScreen.setWidth(canvas.getWidth());
        myScreen.setHeight(canvas.getHeight());

        //DRAW SCREEN ELEMENTS
        canvas.drawBitmap(bouleResized, ballon.getPosX()-45, ballon.getPosY()-45,paint);
        canvas.drawBitmap(nuageResized,doigt-134, myScreen.getHeight()-100, paint);
        canvas.drawBitmap(nuageResizedEnnemi, ennemi.getPosX()-134, -10, paint);

        updateDraw();
    }

    public void updateDraw(){

        //CHECK COLLISIONS
        checkCollisions(ballon);
        checkCollisionEnnemy(ballon);

        //MOVEMENTS
        ballMovement(ballon);
        iA(ennemi);

        invalidate();
    }

    public void checkCollisions(Ballon ball){
        if(ball.getPosX() < 0 || ball.getPosX() > myScreen.getWidth()-45){
            ball.setVitX(ball.getVitX()*-1);
        }
        if(ball.getPosY() < 0){
            ball.setVitY(ball.getVitY()*-1);
        }
        if((ball.getPosY() > myScreen.getHeight())&&!fail){
            fail = true;
            gameOver();
        }
        if((Math.abs(doigt - ball.getPosX()) <= 134) && (myScreen.getHeight()-(ball.getPosY()+45)<=65)){
            //soundPool.play(soundID, 100, 100, 1, 0, 1f);

            bruitage.reset();
            acceleration *= -1;
            acceleration -= 1;
            ball.setVitY(-8 + acceleration);

        }
    }

    public void ballMovement(Ballon ball){
        ball.setPosX(ball.getPosX()+ball.getVitX());
        ball.setPosY(ball.getPosY()+ball.getVitY());
    }

    public void iA(Ennemi enemy){
        float middleH = myScreen.getHeight()/2;
        float middleW = myScreen.getWidth()/2;
        if(ballon.getPosY() < middleH) {
            bruitage.pause();
            if(enemy.getPosX() < ballon.getPosX()) {
                enemy.setVitX(9);
            } else {
                enemy.setVitX(-9);
            }
        } else {
            if(enemy.getPosX() < middleW-30){
                enemy.setVitX(9);
            }
            if(enemy.getPosX() > middleW+30){
                enemy.setVitX(-9);
            }
        }
        enemy.setPosX(enemy.getPosX()+enemy.getVitX());
    }

    public void checkCollisionEnnemy(Ballon ball) {
        if((Math.abs(ennemi.getPosX() - ball.getPosX()) <= 134) && Math.abs(0-ball.getPosY()) <= 65){
            //soundPool.resume(soundID);
            //soundPool.play(soundID, 100, 100, 1, 0, 1f);
            bruitage.reset();
            acceleration *= -1;
            acceleration += 1;
            ball.setVitY(8 + acceleration);
        }
    }

    public void gameOver(){
        opening.stop();
        getContext().startActivity(gameOverIntent);
        Activity activity = (Activity) getContext();
        activity.finish();
    }

    public boolean onTouch(View v, MotionEvent m){
        //GET TOUCH XPOS
        doigt = m.getX();
        return true;
    };

    @Override
    protected void onVisibilityChanged(View GameView, int visibility) {
        super.onVisibilityChanged(GameView, visibility);
        if (visibility == View.VISIBLE) {
            //onResume called }
            opening.start();
        } else {
            opening.pause();
        }
    }

}
