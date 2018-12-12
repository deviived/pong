package com.pachecoluc.kimpongun;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class GameView extends View implements View.OnTouchListener {

    private Paint paint;
    private MyScreen myScreen = new MyScreen(100,100);
    private float doigt=300;
    private Ballon ballon = new Ballon(300, 200, 10, 5, 50, Color.BLUE);
    private Ballon ballon2 = new Ballon(200, 800, 10, 5, 30, Color.GREEN);
    private Ballon ballon3 = new Ballon(400, 500, 10, 5, 60, Color.RED);
    float textSize;
    Intent gameOverIntent;

    public GameView(Context context) {
        super(context);
        paint = new Paint();
        paint.setColor(Color.MAGENTA);

        gameOverIntent = new Intent(getContext(), GameOver.class);

        this.setOnTouchListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setTextSize(80);
        textSize = paint.measureText("MACRON");
        myScreen.setWidth(canvas.getWidth());
        myScreen.setHeight(canvas.getHeight());
        canvas.drawCircle(ballon.getPosX(), ballon.getPosY(), ballon.getRad(), ballon.getColor());
        canvas.drawCircle(ballon2.getPosX(), ballon2.getPosY(), ballon2.getRad(), ballon2.getColor());
        canvas.drawCircle(ballon3.getPosX(), ballon3.getPosY(), ballon3.getRad(), ballon3.getColor());
        canvas.drawText("TRUMP",doigt-(textSize/2),myScreen.getHeight()-80, paint);
        updateDraw();
    }

    public boolean onTouch(View v, MotionEvent m){
        doigt = m.getX();
        return true;
    };

    public void updateDraw(){
        checkCollisions(ballon);
        checkCollisions(ballon2);
        checkCollisions(ballon3);
        invalidate();
    }

    public void checkCollisions(Ballon ball){
        if(ball.getPosX() < 50 || ball.getPosX() > myScreen.getWidth()-50){
            ball.setVitX(ball.getVitX()*-1);
        }
        if(ball.getPosY() < 50 || ball.getPosY() > myScreen.getHeight()){
            ball.setVitY(ball.getVitY()*-1);

        }
        if((Math.abs(doigt - ball.getPosX()) < (textSize/2)) && (Math.abs((myScreen.getHeight()-50)-ball.getPosY())<80)){
           // gameOver();
        }
        ball.setPosX(ball.getPosX()+ball.getVitX());
        ball.setPosY(ball.getPosY()+ball.getVitY());
    }

    public void gameOver(){
        getContext().startActivity(gameOverIntent);
        return;
    }
}
