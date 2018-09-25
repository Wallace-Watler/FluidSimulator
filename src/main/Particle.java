package main;

import java.awt.Color;
import java.awt.Graphics;

public class Particle {

	private static final double PARTICLE_SIZE = 4;
	/**Acceleration due to gravity.*/
	private static final double G = 1;
	/**Repulsive force constant. Akin to rigidity.*/
	private static final double C = 100;
	/**Force of friction, should be between 0 and 1. Lower numbers = higher friction.
	 * Akin to viscosity.*/
	private static final double F = 0.95;
	/**Rate of heat transfer, should be between 0 and 1.*/
	private static final double T = 0.05;
	/**Brownian motion constant.*/
	private static final double BROWN = 0.5;
	/**Squared distance within which particles should calculate force due to other particles.
	 * Akin to compressibility.*/
	private static final double EPSILON = Math.pow(30, 2);
	/**Maximum particle acceleration allowed by the simulation.*/
	private static final double MAX_ACCEL = 2;
	private static final double TEMPERATURE_SCALE = 273;
	
	private double x, y, vx, vy, temperature;
	private Color color;
	
	public Particle(double x, double y) {
		this(x, y, 0, 0, 273);
	}
	
	public Particle(double x, double y, double vx, double vy, double temperature) {
		this.x = x;
		this.y = y;
		this.vx = vx;
		this.vy = vy;
		this.temperature = temperature;
		color = TemperatureGrid.getTempColor(temperature);
	}
	
	public void render(Graphics g) {
		g.setColor(color);
		g.fillOval((int) (x - PARTICLE_SIZE / 2), Main.HEIGHT - (int) (y - PARTICLE_SIZE / 2) - 36, (int) PARTICLE_SIZE, (int) PARTICLE_SIZE);
	}
	
	public void tick() {
		int gridX = (int) x >> TemperatureGrid.GRID_COARSENESS;
		int gridY = (int) y >> TemperatureGrid.GRID_COARSENESS;
		double newTemp = (1 - T) * temperature + T * TemperatureGrid.grid[gridX][gridY];
		TemperatureGrid.grid[gridX][gridY] -= newTemp - temperature;
		temperature = newTemp;
		color = TemperatureGrid.getTempColor(temperature);
		double scaledTemp = temperature / TEMPERATURE_SCALE;
		
		double ax = getBrownianValue(), ay = getBrownianValue();
		for(Particle p : Main.particles) {
			double rSqr = distSqr(p);
			if(p != this && rSqr < scaledTemp * scaledTemp * EPSILON) {
				double rCube = Math.pow(rSqr, 1.5);
				ax -= (p.x - x) * C / rCube;
				ay -= (p.y - y) * C / rCube;
			}
		}
		ax *= scaledTemp;
		ay = ay * scaledTemp - G;
		double absAx = Math.abs(ax), absAy = Math.abs(ay);
		if(absAx > MAX_ACCEL) ax *= MAX_ACCEL / absAx;
		if(absAy > MAX_ACCEL) ay *= MAX_ACCEL / absAy;
		
		vx += ax;
		vy += ay;
		vx *= F;
		vy *= F;
	}
	
	public void updatePosition() {
		x += vx;
		y += vy;
		
		if(y <= 0) {
			y = -y;
			vy = -vy * 0.5;
		}else if(y >= Main.HEIGHT - 1) {
			y = 2 * Main.HEIGHT - 2 - y;
			vy = -vy * 0.5;
		}
		if(x <= 0) {
			x = -x;
			vx = -vx * 0.5;
		}else if(x >= Main.WIDTH - 1) {
			x = 2 * Main.WIDTH - 2 - x;
			vx = -vx * 0.5;
		}
	}
	
	private double distSqr(Particle p) {
		return Math.pow(p.x - x, 2) + Math.pow(p.y - y, 2);
	}
	
	private double getBrownianValue() {
		return BROWN * Main.r.nextDouble() - BROWN / 2;
	}
}
