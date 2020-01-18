package main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.*;

public class Main extends Canvas implements Runnable {

	private static final long serialVersionUID = -7596679599971022919L;
	
	private static final int TICKS_PER_SECOND = 60;
	private static final double NANOSECONDS_PER_TICK = 1000000000.0 / TICKS_PER_SECOND;
	private static final Color BACKGROUND_COLOR = Color.black;
	public static final int HEIGHT = 900, WIDTH = 900;
	
	private Thread thread;
	private boolean running = false;

	public static final Vector G = new Vector(0, 50);
    private static List<Particle> particles;
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("Fluid Simulator");
		Main main = new Main();
		Dimension d = new Dimension(WIDTH + 6, HEIGHT + 29);
		frame.setSize(d);
		frame.setPreferredSize(d);
		frame.setMaximumSize(d);
		frame.setMinimumSize(d);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.requestFocus();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.pack();
		frame.add(main);
		main.start();
	}
	
	private synchronized void start() {
		if(!running) {
			thread = new Thread(this);
			thread.start();
			running = true;
		}
	}
	
	private synchronized void stop() {
		if(running) {
			try {
				thread.join();
			} catch (InterruptedException e) { e.printStackTrace(); }
			running = false;
		}
	}
	
	private void init() {
		this.createBufferStrategy(3);
		this.addKeyListener(new KeyManager());

        Random r = new Random();
		particles = new ArrayList<>();

		final int numParticles = 1000;
		final double fluidArea = 100000;

		for(int i = 0; i < numParticles; i++)
			particles.add(new Particle(Fluid.WATER, Fluid.WATER.restingDensity * fluidArea / numParticles, r.nextInt((int) Math.sqrt(fluidArea)), Main.HEIGHT - 2 - r.nextInt((int) Math.sqrt(fluidArea))));
	}
	
	public void run() {
		init();
		long now, lastTime = System.nanoTime();
		double delta = 0;
		while(running) {
			now = System.nanoTime();
			delta += now - lastTime;
			lastTime = now;
			if(delta >= NANOSECONDS_PER_TICK) {
				tick(delta / 1000000000);
				delta -= NANOSECONDS_PER_TICK;
			}
			render();
		}
		stop();
	}
	
	private void tick(double dt) {
		for(Particle p : particles) p.calculateDensityAndPressure(particles);
		for(Particle p : particles) p.calculatePressureGradientAndViscousTerm();
		for(Particle p : particles) p.integratePosition(dt);
	}
	
	private void render() {
		BufferStrategy bs = this.getBufferStrategy();
		Graphics g = bs.getDrawGraphics();
		g.setColor(BACKGROUND_COLOR);
		g.fillRect(0, 0, WIDTH, HEIGHT);

		for(Particle p : particles) p.render(g);
		
		g.dispose();
		bs.show();
	}
}