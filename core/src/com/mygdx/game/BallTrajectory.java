package com.mygdx.game;

import java.util.concurrent.CopyOnWriteArrayList;

public class BallTrajectory {

    public float x,y,a,b,velocity,r,Constant;

    BallTrajectory(float x,float y,float r,float velocity,float Constant){
        this.velocity = velocity;
        this.x = x;
        this.y = y;
        this.r = r;
        this.Constant = Constant;

        a = 1;
        b = 0;

    }
}
