package main;

import java.awt.Color;
import java.awt.Graphics;

public class Particle {

	private static final double PARTICLE_SIZE = 4;
	/**Acceleration due to gravity.*/
	private static final double G = 1;
	/**Maximum particle acceleration allowed by the simulation.*/
	private static final double MAX_ACCEL = 2;
	private static final double TEMPERATURE_SCALE = 273;
	
	private double x, y, vx, vy, temperature;
	private Color color;
	private Fluid fluid;
	
	public Particle(double x, double y) {
		this(x, y, 0, 0, 273, Fluid.WATER);
	}
	
	public Particle(double x, double y, double vx, double vy, double temperature, Fluid fluid) {
		this.x = x;
		this.y = y;
		this.vx = vx;
		this.vy = vy;
		this.temperature = temperature;
		this.fluid = fluid;
		setDisplayColor();
	}
	
	public void render(Graphics g) {
		g.setColor(color);
		g.fillOval((int) (x - PARTICLE_SIZE / 2), Main.HEIGHT - (int) (y - PARTICLE_SIZE / 2) - 36, (int) PARTICLE_SIZE, (int) PARTICLE_SIZE);
	}
	
	public void setDisplayColor() {
		if(Main.showTempGrid) color = TemperatureGrid.getTempColor(temperature);
		else color = fluid.color;
	}
	
	public void tick() {
		calculateTemperature();
		setDisplayColor();
		
		double scaledTemp = temperature / TEMPERATURE_SCALE;
		double ax = getBrownianValue(), ay = getBrownianValue();
		double replusion = fluid.replusion;
		for(Particle p : Main.particles) {
			double rSqr = distSqr(p);
			//repulsion distance increases with temperature squared
			if(p != this && rSqr < scaledTemp * scaledTemp * fluid.epsilon) {
				double rCube = Math.pow(rSqr, 1.5);
				ax -= (p.x - x) * replusion / rCube;
				ay -= (p.y - y) * replusion / rCube;
			}
		}
		//Brownian motion and repulsion force increases
		//linearly with temperature, add gravity
		ax *= scaledTemp;
		ay = ay * scaledTemp - G;
		double absAx = Math.abs(ax), absAy = Math.abs(ay);
		if(absAx > MAX_ACCEL) ax *= MAX_ACCEL / absAx;
		if(absAy > MAX_ACCEL) ay *= MAX_ACCEL / absAy;
		
		double f = fluid.friction;
		vx += ax;
		vy += ay;
		vx *= f;
		vy *= f;
	}
	
	private void calculateTemperature() {
		int gridX = (int) x >> TemperatureGrid.GRID_COARSENESS;
		int gridY = (int) y >> TemperatureGrid.GRID_COARSENESS;
		double t = fluid.rateOfHeatTransfer;
		double newTemp = (1 - t) * temperature + t * TemperatureGrid.grid[gridX][gridY];
		TemperatureGrid.grid[gridX][gridY] -= newTemp - temperature;
		temperature = newTemp;
		
		Fluid vapor = fluid.vapor;
		if(vapor != null && temperature > vapor.boilingPoint) fluid = fluid.vapor;
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
		double brown = fluid.brown;
		return brown * Main.r.nextDouble() - brown / 2;
	}
}
