package com.pachecoluc.kimpongun;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import static android.graphics.BitmapFactory.decodeResource;

public class GameView extends View implements View.OnTouchListener {

    private Paint paint;
    private MyScreen myScreen = new MyScreen(100,100);
    private float doigt=300;
    private Ballon ballon = new Ballon(200, 150, 5, 8, 50, Color.BLUE);
    //private Ballon ballon2 = new Ballon(200, 150, 5, 1, 30, Color.GREEN);
    //private Ballon ballon3 = new Ballon(200, 150, 8, 1, 60, Color.RED);
    float textSize;
    Intent gameOverIntent;
    boolean fail = false;
    Bitmap nuage;
    Bitmap boule;
    Bitmap bouleResized;
    Bitmap nuageResized;
    Bitmap nuageEnnemi;
    Bitmap nuageResizedEnnemi;
    MediaPlayer opening;
    private Ennemi ennemi = new Ennemi(200, 0, 2, 0);
    float ennemiPosX = 400;


    public GameView(Context context) {
        super(context);
        paint = new Paint();
        paint.setColor(Color.MAGENTA);
        this.setBackgroundResource(R.drawable.fond_db);

        nuage = BitmapFactory.decodeResource(getResources(), R.drawable.nuagemagique);
        nuageResized = Bitmap.createScaledBitmap(nuage, 296, 141, false);

        nuageEnnemi = BitmapFactory.decodeResource(getResources(), R.drawable.nuage_ennemi);
        nuageResizedEnnemi = Bitmap.createScaledBitmap(nuageEnnemi, 296, 141, false);

        opening = MediaPlayer.create(getContext(), R.raw.opening_db);
        opening.start();

        boule = BitmapFactory.decodeResource(getResources(), R.drawable.bouledudragon);
        bouleResized = Bitmap.createScaledBitmap(boule, 120, 120, false);

        gameOverIntent = new Intent(getContext(), GameOver.class);

        this.setOnTouchListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setTextSize(80);
        textSize = paint.measureText("NUAGE12");
        myScreen.setWidth(canvas.getWidth());
        myScreen.setHeight(canvas.getHeight());

        //canvas.drawCircle(ballon.getPosX(), ballon.getPosY(), ballon.getRad(), ballon.getColor());
        canvas.drawBitmap(bouleResized,ballon.getPosX(), ballon.getPosY(),paint);

        //canvas.drawText("BAR",doigt-(textSize/2), myScreen.getHeight(), paint);
        canvas.drawBitmap(nuageResized,doigt-(296/2), myScreen.getHeight()-120, paint);

        canvas.drawBitmap(nuageResizedEnnemi, ennemiPosX-148, 0, paint);
        updateDraw();
    }

    public boolean onTouch(View v, MotionEvent m){
        doigt = m.getX();
        return true;
    };

    public void updateDraw(){

        checkCollisions(ballon);
        checkCollisionEnnemy();

        ballMovement(ballon);

        //ennemiMovement(ennemi);

        ennemiPosX = iA(ennemi);

        invalidate();
    }

    public void checkCollisions(Ballon ball){
        if(ball.getPosX() < 0 || ball.getPosX() > myScreen.getWidth()-120){
            ball.setVitX(ball.getVitX()*-1);
        }
        if(ball.getPosY() < 0){
            ball.setVitY(ball.getVitY()*-1);
        }
        if((ball.getPosY() > myScreen.getHeight())&&!fail){
            fail = true;
            gameOver();
        }
        if((Math.abs(doigt - ball.getPosX()) <= (296/2)) && (myScreen.getHeight()-(ball.getPosY()+60)<=120)){
            ball.setVitY(-5);
        }
    }

    public void ballMovement(Ballon ball){
        ball.setPosX(ball.getPosX()+ball.getVitX());
        ball.setPosY(ball.getPosY()+ball.getVitY());
    }

    public float iA(Ennemi enemy){
        float middle = myScreen.getHeight()/2;
        if(ballon.getPosY() < middle) {
            if(enemy.getPosX() < ballon.getPosX()) {
                ennemiPosX+=Math.abs(ballon.getVitX())-5;
            } else {
                ennemiPosX-=Math.abs(ballon.getVitX())-5;
            }
        } else {
            ennemiPosX=middle;
        }
        return ennemiPosX;

    }

    public void ennemiMovement(Ennemi enemy){
        enemy.setPosX(enemy.getVitX());
    }

    public boolean checkCollisionEnnemy() {
        return (Math.abs(ballon.getPosX()-ennemiPosX) < 148 && (ballon.getPosY()-120 >= 35 && ballon.getPosY()-120 <= 95)) ? true:false;
    }


    public void gameOver(){
        getContext().startActivity(gameOverIntent);
        Activity activity = (Activity) getContext();
        activity.finish();
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == View.VISIBLE) {
            //onResume called }
            opening.start();
        } else {
            opening.pause();
        }
    }

}
