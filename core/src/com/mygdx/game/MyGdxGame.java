package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import java.awt.font.ShapeGraphicAttribute;
import java.util.Random;

import javax.naming.Context;

import sun.rmi.runtime.Log;

import static java.lang.Math.acos;
import static java.lang.Math.atan;
import static java.lang.Math.atan2;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.sqrt;
import static java.lang.Math.tan;
import static java.lang.Math.toDegrees;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	ShapeRenderer shapeRenderer;
	float radius;
	float circleX;
	float circleY;
	float rotate;
	BallTrajectory[] ball;
	float ballX;
	float ballY;
	float factor;
	float sliderSpeed;
	float radiusOfBall;
	Random random;
	Texture play;
	Texture howToPlay;
	Texture gameOver;
	Texture help;
	Texture newHighScore;
	int gameStatus;
	int collision;
	int colCount;
	float textButtonHeight;
	float textButtonWidth;
	float textButtonX;
	float sliderWidth;
	float buttonHeight;
	ParticleEffect backgroundEffect;
	Rectangle howToPlayButton;
	int highScore;
	int score;
	Preferences preferences;
	BitmapFont font;
	int c;
	int tailing;

	@Override
	public void create () {
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		radius = Gdx.graphics.getWidth()*0.40f;
		circleX = Gdx.graphics.getWidth()/2;
		circleY = Gdx.graphics.getHeight()*0.95f-circleX;
		sliderWidth = Gdx.graphics.getWidth()*0.03f;
		radiusOfBall = 20;
		buttonHeight = Gdx.graphics.getHeight() * 0.8f - (float) (2 * radius);
		sliderSpeed = 3;
		ball = new BallTrajectory[10];
		ball[0] = new BallTrajectory(circleX, circleY + radius / 2, radiusOfBall, (2 * radius * sliderSpeed * 0.9f) / 180, circleX);
		factor = (ball[0].velocity)/(float)Math.sqrt(ball[0].a*ball[0].a+ball[0].b*ball[0].b);
		for(int i=1;i<10;++i) {
			ball[i] = new BallTrajectory(ball[0].x+i*2*factor*ball[0].b, ball[0].y+i*2*factor*ball[0].a, ball[i-1].r*0.93f, (2 * radius * sliderSpeed * 0.9f) / 180, circleX);
		}
		ballX = ball[0].x;
		ballY = ball[0].y;
		rotate = 0;

		random = new Random();

		gameStatus = 0;
		collision = 0;
		colCount = 0;

		howToPlay = new Texture("howtoplay.png");
		gameOver = new Texture("gameover.png");
		play = new Texture("play.png");
		help = new Texture("help.png");
		newHighScore = new Texture("highscore.png");

		textButtonHeight = ((float)play.getHeight()/play.getWidth())*Gdx.graphics.getWidth();
		textButtonWidth = Gdx.graphics.getWidth()*0.9f;
		textButtonX = Gdx.graphics.getWidth()*0.05f;

		backgroundEffect = new ParticleEffect();
		backgroundEffect.load(Gdx.files.internal("explosion.p"),Gdx.files.internal(""));
		backgroundEffect.setPosition(Gdx.graphics.getWidth()/2,Gdx.graphics.getHeight()/2);
		backgroundEffect.start();
		backgroundEffect.scaleEffect(4);

		batch.disableBlending();

		howToPlayButton = new Rectangle(textButtonX,Gdx.graphics.getHeight()*0.45f,textButtonWidth,textButtonHeight);


		preferences = Gdx.app.getPreferences("MyPreferences");

		highScore = preferences.getInteger("highscore");
		Gdx.app.log("ok: ",highScore+"");
		score = 0;

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(3);

		c=0;

	}

	public void gameOver(){
		if(score>highScore){
			highScore = score;
			preferences.putInteger("highscore",highScore).flush();
		}
		score = 0;
		rotate = 0;
		ball[0] = new BallTrajectory(circleX, circleY + radius / 2, radiusOfBall, (2 * radius * sliderSpeed * 0.9f) / 180, circleX);
		for(int i=1;i<10;++i) {
			ball[i] = new BallTrajectory(circleX, circleY + radius / 2, ball[i-1].r*0.93f, (2 * radius * sliderSpeed * 0.9f) / 180, circleX);
		}
        ballX = ball[0].x;
        ballY = ball[0].y;
        factor = (ball[0].velocity)/(float)Math.sqrt(ball[0].a*ball[0].a+ball[0].b*ball[0].b);
	}

	public void showScore(){
		batch.end();
		batch.begin();
		batch.enableBlending();
		font.draw(batch,"Score: "+score+"\nHighest Score: "+highScore,0,Gdx.graphics.getHeight());
		batch.disableBlending();
	}

	public void reflectBall(){
		++score;
		int sign=1;
		float slope = -ball[0].a/ball[0].b;
		if(ball[0].b==0) {
			slope = (float) 60;
			if(ball[0].a>=0)
				slope = -slope;
		}

		float slope2 = (float)(circleY-ball[0].y)/(circleX-ball[0].x);
		if(circleX-ball[0].x==0) {
			slope2 = (float) 60;
			if(circleY-ball[0].y<0)
				slope2 = -slope2;
		}

		float angle = (float)atan((slope-slope2)/(1+slope*slope2));
		float referenceLineAngle = (float)atan(-slope2);
		float newSlope;

		float r = random.nextFloat()*0.3f;
		r = r-0.15f;

		angle += r;

		if(angle>=1.0||angle<=-1.)
			angle = angle/2;

		newSlope = (float)tan(referenceLineAngle-angle);

		ball[0].b = 1;
		ball[0].a = -newSlope;
		ball[0].Constant = -newSlope*ball[0].x+ball[0].y;

		float testX,testY;
		testY = ball[0].y - 3*factor*ball[0].a;
		testX = ball[0].x - 3*factor*ball[0].b;

		if((testX-circleX)*(testX-circleX)+(testY-circleY)*(testY-circleY)>=(radius)*(radius)){
				ball[0].velocity = -ball[0].velocity;
        }

		factor = (ball[0].velocity)/(float)Math.sqrt(ball[0].a*ball[0].a+ball[0].b*ball[0].b);

		collision = 1;

	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0,0,0,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();

		backgroundEffect.draw(batch,Gdx.graphics.getDeltaTime());

		if(gameStatus==0){

			if(Gdx.input.isTouched()){
				if(howToPlayButton.contains(Gdx.input.getX(),Gdx.input.getY())){
					gameStatus = 3;
				} else {
					gameStatus = 1;
					c=0;
				}
			}
			if(c>=50) {
			    if(c==60)
				    c=0;
			    else
			        ++c;
			}
			else {
                ++c;
                batch.draw(play, textButtonX, 0, textButtonWidth, textButtonHeight);
            }
			batch.draw(howToPlay,textButtonX,Gdx.graphics.getHeight()*0.45f,textButtonWidth,textButtonHeight);

		}
		else if(gameStatus==1) {

			if ((circleX - ball[0].x) * (circleX - ball[0].x) + (circleY - ball[0].y) * (circleY - ball[0].y) >= (radius) * (radius)) {
				rotate = rotate % 360;
				if (rotate < 0)
					rotate += 360;

				float dot, det, angle, minAngle, maxAngle;
				dot = (ball[0].x - circleX) * (radius);
				det = (ball[0].y - circleY) * (radius);
				angle = (float) atan2(det, dot);
				angle = angle * 180 / 3.14f;

				if (angle < 0)
					angle += 360;

				minAngle = rotate;
				maxAngle = (rotate + 30) % 360;

				if (maxAngle < minAngle) {
					if ((angle >= minAngle && angle < 360) || (angle >= 0 && angle <= maxAngle)) {
						reflectBall();
					}
					else{
						gameStatus = 2;
					}
				} else if (angle >= minAngle && angle <= maxAngle) {
					reflectBall();
				}
				else
					gameStatus = 2;

				if(gameStatus==2) {
					gameOver();
					collision = 1;
				}
			}

			if(c==2) {
				for (int i = 9; i >= 1; --i) {
					ball[i].x = ball[i-1].x;
					ball[i].y = ball[i-1].y;
				}
				c=0;
			}
			else
				++c;

			Gdx.gl.glEnable(GL20.GL_BLEND);
			Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
			shapeRenderer.setColor(Color.YELLOW);
			shapeRenderer.arc(circleX,circleY,radius + sliderWidth,rotate, 30);
			shapeRenderer.setColor(Color.GOLDENROD);
			shapeRenderer.circle(circleX, (float) circleY,radius);
			shapeRenderer.setColor(Color.BLACK);
			shapeRenderer.circle(circleX,circleY, radius-10);
			shapeRenderer.setColor(Color.CYAN);
				for (int i = 0; i < 10; ++i) {
					shapeRenderer.setColor(0, 255, 255, (1 - i / 10f));
					shapeRenderer.circle(ball[i].x, ball[i].y, ball[i].r);
				}
			if (Gdx.input.isTouched()) {
				if (Gdx.input.getX() < Gdx.graphics.getWidth() / 2) {
					rotate -= sliderSpeed;
					shapeRenderer.setColor(new Color(255, 255, 255, 0.5f));
					shapeRenderer.rect(0, 0, (float) circleX, buttonHeight);
				}
				else {
					rotate += sliderSpeed;
					shapeRenderer.setColor(new Color(255,255,255,0.5f));
					shapeRenderer.rect((float)circleX,0,(float)circleX,buttonHeight);
				}
			}

			shapeRenderer.end();
			Gdx.gl.glDisable(GL20.GL_BLEND);

			showScore();

			ballY = ball[0].y - factor * ball[0].a;
			ballX = ball[0].x - factor * ball[0].b;

			ball[0].x = ballX;
			ball[0].y = ballY;


		}
		else if(gameStatus==2){
			showScore();
			if(collision==30) {
				if (Gdx.input.isTouched()) {
					gameStatus = 1;
					collision = 0;
				} else if (score > highScore) {
					batch.draw(newHighScore, textButtonX, Gdx.graphics.getHeight() * 0.45f - 2 * textButtonHeight, textButtonWidth, textButtonHeight);
				}
				batch.draw(gameOver, textButtonX, Gdx.graphics.getHeight() * 0.45f, textButtonWidth, textButtonHeight);
			}
			else
				++collision;
		}
		else if(gameStatus==3){
			batch.draw(help,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
			if(Gdx.input.isTouched()){
				gameStatus = 0;
			}
		}

		batch.end();
	}



	@Override
	public void dispose () {
		batch.dispose();
	}
}
