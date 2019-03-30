package main;

import main.simulation.Fluid;
import main.simulation.Particle;
import main.simulation.TemperatureGrid;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.util.Random;
import java.util.stream.IntStream;

import javax.swing.*;

public class Main extends Canvas implements Runnable {

	private static final long serialVersionUID = -7596679599971022919L;
	
	public static final int TICKS_PER_SECOND = 60;
	public static final double NANOSECONDS_PER_TICK = 1000000000 / TICKS_PER_SECOND;
	public static final Color BACKGROUND_COLOR = Color.black;
	public static final int HEIGHT = 1024, WIDTH = 1024;
	public static int numberOfParticles = 1000;
	public static boolean showTempGrid = true;
	
	private Thread thread;
	private boolean running = false;
	
	public static Random r;
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("Fluid Simulator");
		Main main = new Main();
		Dimension d = new Dimension(WIDTH, HEIGHT);
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
		
		r = new Random();
		
		TemperatureGrid.init();
		
		IntStream
			.range(0, numberOfParticles / 2)
			.parallel()
			.forEach(i -> new Particle(300, Fluid.WATER));
		IntStream
				.range(0, numberOfParticles / 2)
				.parallel()
				.forEach(i -> new Particle(300, Fluid.OIL));
	}
	
	public void run() {
		init();
		long now, delta = 0, lastTime = System.nanoTime();
		while(running) {
			now = System.nanoTime();
			delta += now - lastTime;
			lastTime = now;
			if(delta >= NANOSECONDS_PER_TICK) {
				tick();
				delta -= NANOSECONDS_PER_TICK;
			}
			render();
		}
		stop();
	}
	
	private void tick() {
		Particle.particles.stream().parallel().forEach(Particle::tick);
		Particle.particles.stream().parallel().forEach(Particle::updatePosition);
		
		TemperatureGrid.tick();
	}
	
	private void render() {
		BufferStrategy bs = this.getBufferStrategy();
		Graphics g = bs.getDrawGraphics();
		g.setColor(BACKGROUND_COLOR);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		if(showTempGrid) TemperatureGrid.render(g);
		for(Particle p : Particle.particles) p.render(g);
		
		g.dispose();
		bs.show();
	}

	public static void toggleHeatDisplay() {
		showTempGrid = !showTempGrid;
		Particle.particles.stream().parallel().forEach(Particle::setDisplayColor);
	}
}