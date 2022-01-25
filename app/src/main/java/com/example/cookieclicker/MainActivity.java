package com.example.cookieclicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FlingAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;


public class MainActivity extends AppCompatActivity {

    private AtomicLong inators;
    private long rate;

    private TextView tinators;
    private TextView trate;
    private Button b1;
    private Button b2;
    private ImageView reset;

    private ImageView image;

    private ConstraintLayout cl;
    private ArrayList<Integer> ids;
    private ScaleAnimation animation;
    private int[] offset;
    private SharedPreferences prefs;
    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cl = findViewById(R.id.a_layout);
        tinators = findViewById(R.id.a_inators);
        trate = findViewById(R.id.a_rate);
        b1 = findViewById(R.id.a_upgrade1);
        b2 = findViewById(R.id.a_upgrade2);
        image = findViewById(R.id.imageView);
        reset = findViewById(R.id.a_reset);

        prefs = getPreferences(Context.MODE_PRIVATE);
        rate = prefs.getLong("RATE",0);
        inators = new AtomicLong(prefs.getLong("INATORS",0));

        ids = new ArrayList<>();
        for(int j = 0; j<prefs.getInt("UPGRADES",0); j++){
            ImageView i = new ImageView(MainActivity.this);
            i.setId(View.generateViewId());
            ids.add(i.getId());
            i.setImageResource(R.drawable.perry);
            ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,ConstraintLayout.LayoutParams.WRAP_CONTENT);
            ConstraintSet cs = new ConstraintSet();
            i.setLayoutParams(lp);
            cl.addView(i);
            cs.clone(cl);
            cs.connect(i.getId(),ConstraintSet.TOP,cl.getId(),ConstraintSet.TOP,5);
            cs.connect(i.getId(),ConstraintSet.BOTTOM,cl.getId(),ConstraintSet.BOTTOM);
            cs.connect(i.getId(),ConstraintSet.RIGHT,cl.getId(),ConstraintSet.RIGHT);
            cs.connect(i.getId(),ConstraintSet.LEFT,cl.getId(),ConstraintSet.LEFT,-1000);
            cs.setHorizontalBias(i.getId(),0f);
            cs.setVerticalBias(i.getId(),0f);
            cs.applyTo(cl);
            SpringAnimation sa = new SpringAnimation(i, DynamicAnimation.TRANSLATION_X);
            SpringForce sp = new SpringForce(60*ids.size());
            sp.setDampingRatio(SpringForce.DAMPING_RATIO_HIGH_BOUNCY);
            sa.setSpring(sp);
            sa.start();
        }

        update();

        animation = new ScaleAnimation(1.0f,1.25f,1.0f,1.25f, Animation.RELATIVE_TO_SELF,.5f ,Animation.RELATIVE_TO_SELF,.5f);
        animation.setDuration(500);

        CountThread a = new CountThread();
        a.start();

        image.setOnTouchListener(new View.OnTouchListener() {
            float x1,x2,y1,y2;
            public boolean onTouch(View v, MotionEvent event) {
                v.startAnimation(animation);
                if(offset==null) {
                    offset = new int[2];
                    cl.getLocationOnScreen(offset);
                }
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getRawX();
                        y1 = event.getRawY()-offset[1];
                        break;
                    case MotionEvent.ACTION_UP:
                        x2 = event.getRawX();
                        y2 = event.getRawY()-offset[1];
                        createPlus((int)y1,(int)x1,(int)y2,(int)x2);

                        inators.incrementAndGet();
                        tinators.setText("Inators: "+inators + "");
                        break;
                }
                return true;
            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(inators.get()>=50*Math.pow(1.15,ids.size())) {
                    rate++;
                    inators.addAndGet((int)(-50*Math.pow(1.15,ids.size())));

                    ImageView i = new ImageView(MainActivity.this);
                    i.setId(View.generateViewId());
                    ids.add(i.getId());
                    i.setImageResource(R.drawable.perry);
                    ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,ConstraintLayout.LayoutParams.WRAP_CONTENT);
                    ConstraintSet cs = new ConstraintSet();

                    i.setLayoutParams(lp);
                    cl.addView(i);
                    cs.clone(cl);
                    cs.connect(i.getId(),ConstraintSet.TOP,cl.getId(),ConstraintSet.TOP);
                    cs.connect(i.getId(),ConstraintSet.BOTTOM,cl.getId(),ConstraintSet.BOTTOM);
                    cs.connect(i.getId(),ConstraintSet.RIGHT,cl.getId(),ConstraintSet.RIGHT);
                    cs.connect(i.getId(),ConstraintSet.LEFT,cl.getId(),ConstraintSet.LEFT,-1000);
                    cs.setHorizontalBias(i.getId(),0f);
                    cs.setVerticalBias(i.getId(),0f);
                    cs.applyTo(cl);

                    SpringAnimation sa = new SpringAnimation(i, DynamicAnimation.TRANSLATION_X);
                    SpringForce sp = new SpringForce(60*ids.size());
                    sp.setDampingRatio(SpringForce.DAMPING_RATIO_HIGH_BOUNCY);
                    sa.setSpring(sp);
                    sa.start();
                    update();
                }
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(ids.size()>0) {
                    inators.addAndGet((int)(25*Math.pow(1.15,ids.size())));
                    int id = ids.remove(ids.size()-1);
                    remove(id);
                    rate--;
                    update();
                }
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rate = 0;
                inators.lazySet(0);
                for(int id : ids) {
                    remove(id);
                }
                ids.clear();
                update();
                Toast.makeText(MainActivity.this,"Curse you Perry the Platypus!",Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void createPlus(int y1,int x1,int y2,int x2){
        TextView t = new TextView(MainActivity.this);
        t.setId(View.generateViewId());
        t.setText("+1");
        t.setTextSize(50f);
        t.setTextColor(Color.BLUE);
        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,ConstraintLayout.LayoutParams.WRAP_CONTENT);
        ConstraintSet cs = new ConstraintSet();

        t.setLayoutParams(lp);
        cl.addView(t);
        cs.clone(cl);
        cs.connect(t.getId(),ConstraintSet.TOP,cl.getId(),ConstraintSet.TOP,y2);
        cs.connect(t.getId(),ConstraintSet.BOTTOM,cl.getId(),ConstraintSet.BOTTOM);
        cs.connect(t.getId(),ConstraintSet.RIGHT,cl.getId(),ConstraintSet.RIGHT);
        cs.connect(t.getId(),ConstraintSet.LEFT,cl.getId(),ConstraintSet.LEFT,x2);
        cs.setHorizontalBias(t.getId(),0f);
        cs.setVerticalBias(t.getId(),0f);
        cs.applyTo(cl);

        animatePlus(t,y2-y1,x2-x1);
    }
    public void animatePlus(View t,int dy, int dx){
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setFillAfter(true);
        fadeOut.setDuration(1000);
        if(Math.abs(dy)<50 && Math.abs(dx)<50){
            dy = ((int)(Math.random()*200)+25)*(((int)(Math.random()*2))*2-1);
            dx = ((int)(Math.random()*100)+25)*(((int)(Math.random()*2))*2-1);
        }
        FlingAnimation yfling = new FlingAnimation(t,DynamicAnimation.TRANSLATION_Y);
        yfling.setStartVelocity(10*dy);

        FlingAnimation xfling = new FlingAnimation(t,DynamicAnimation.TRANSLATION_X);
        xfling.setStartVelocity(5*dx);

        xfling.start();
        yfling.start();
        t.startAnimation(fadeOut);
    }
    public class CountThread extends Thread {
        public void run() {
            Timer a = new Timer();
            a.schedule(new TimerTask() {
                public void run() {
                    inators.getAndAdd(rate);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            b1.setEnabled(inators.get()>=50*Math.pow(1.15,ids.size()));
                            b2.setEnabled(ids.size()>0);
                            tinators.setText("Inators: "+inators + "");
                        }
                    });
                }
            },0,1000);
        }
    }

    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("RATE",rate);
        editor.putLong("INATORS",inators.get());
        editor.putInt("UPGRADES",ids.size());
        editor.apply();
    }
    public void remove(int id){
        int random = (int)(Math.random()*90);
        RotateAnimation r = new RotateAnimation(0, random, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        r.setDuration(1000);
        r.setFillAfter(true);
        findViewById(id).setAnimation(r);
        findViewById(id).animate().translationXBy((float)(2000*Math.cos(Math.toRadians(random)))).setStartDelay(1000).translationY((float)(1000*Math.sin(Math.toRadians(random)))).setStartDelay(1000);
    }
    public void update(){
        trate.setText("Inators/s: "+rate);
        tinators.setText("Inators: "+inators + "");
        b1.setText("Buy - "+(int)(50*Math.pow(1.15,ids.size())));
        b2.setText("Sell - "+(int)(25*Math.pow(1.15,ids.size())));
    }
}