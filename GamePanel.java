import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class GamePanel extends JPanel implements Runnable {

	static final int GAMEWID = 1920;
	static final int GAME_HEIGHT = (int) (GAMEWID
			* (0.5555));
	static final Dimension SCREEN_SIZE = new Dimension(GAMEWID, GAME_HEIGHT);
	static final int DiamBall = 20;
	static final int PadWid = 25;
	static final int PadH = 150;
	Thread gameThread;
	Image image;
	Graphics graphics;
	Random random;
	Paddle paddle1;
	Paddle paddle2;
	Ball ball;
	Score score;

	GamePanel() {
		newPaddles();
		newBall();
		score = new Score(GAMEWID, GAME_HEIGHT);
		this.setFocusable(true);
		this.addKeyListener(new KEY());
		this.setPreferredSize(SCREEN_SIZE);

		gameThread = new Thread(this);
		gameThread.start();
	}

	public void newPaddles() {
		paddle1 = new Paddle(0, (GAME_HEIGHT / 2) - (PadH / 2), PadWid, PadH, 1);
		paddle2 = new Paddle(GAMEWID
				- PadWid, (GAME_HEIGHT / 2) - (PadH / 2), PadWid,
				PadH, 2);
	}

	public void newBall() {
		random = new Random();
		ball = new Ball((GAMEWID
				/ 2) - (DiamBall / 2), random.nextInt(GAME_HEIGHT - DiamBall),
				DiamBall, DiamBall);
	}

	public void paint(Graphics g) {
		image = createImage(getWidth(), getHeight());
		graphics = image.getGraphics();
		draw(graphics);
		g.drawImage(image, 0, 0, this);
	}

	public void draw(Graphics g) {
		paddle1.draw(g);
		paddle2.draw(g);
		ball.draw(g);
		score.draw(g);
		Toolkit.getDefaultToolkit().sync();

	}

	public void move() {
		paddle1.move();
		paddle2.move();
		ball.move();
	}

	public void checkCollision() {

		if (ball.y <= 0) {
			ball.setYDirection(-ball.yVelocity);
		}
		if (ball.y >= GAME_HEIGHT - DiamBall) {
			ball.setYDirection(-ball.yVelocity);
		}

		if (ball.intersects(paddle1)) {
			ball.velX = Math.abs(ball.velX);
			ball.velX++;
			if (ball.yVelocity > 0)
				ball.yVelocity++;
			else
				ball.yVelocity--;
			ball.setXDirection(ball.velX);
			ball.setYDirection(ball.yVelocity);
		}
		if (ball.intersects(paddle2)) {
			ball.velX = Math.abs(ball.velX);
			ball.velX++;
			if (ball.yVelocity > 0)
				ball.yVelocity++;
			else
				ball.yVelocity--;
			ball.setXDirection(-ball.velX);
			ball.setYDirection(ball.yVelocity);
		}

		if (paddle1.y <= 0)
			paddle1.y = 0;
		if (paddle1.y >= (GAME_HEIGHT - PadH))
			paddle1.y = GAME_HEIGHT - PadH;
		if (paddle2.y <= 0)
			paddle2.y = 0;
		if (paddle2.y >= (GAME_HEIGHT - PadH))
			paddle2.y = GAME_HEIGHT - PadH;

		if (ball.x <= 0) {
			score.player2++;
			newPaddles();
			newBall();
			System.out.println("Player 2: " + score.player2);
		}
		if (ball.x >= GAMEWID
				- DiamBall) {
			score.player1++;
			newPaddles();
			newBall();
			System.out.println("Player 1: " + score.player1);
		}
	}

	public void run() {
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		while (true) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			if (delta >= 1) {
				move();
				checkCollision();
				repaint();
				delta--;
			}
		}
	}

	public class KEY extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			paddle1.keyPressed(e);
			paddle2.keyPressed(e);
		}

		public void keyReleased(KeyEvent e) {
			paddle1.keyReleased(e);
			paddle2.keyReleased(e);
		}
	}
}