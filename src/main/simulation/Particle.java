package main.simulation;

import main.Main;
import main.math.Vec2d;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashSet;
import java.util.Random;

public class Particle {

	public static HashSet<Particle> particles = new HashSet<>();

	protected static final Random rand = new Random();

	/**Render particles at this size.*/
	private static final double PARTICLE_SIZE = 4;

	/**Acceleration due to gravity.*/
	private static final double G = -1;

	/**Fraction of velocity to keep each tick.*/
	private static final double FRICTION = 0.9;

	//private static final double TEMPERATURE_SCALE = 273;

	protected Vec2d position;
	private Vec2d velocity;
	private double temperature;
	private Color color;
	protected Fluid fluid;
	protected boolean anchored;

	public Particle(double temperature, Fluid fluid) {
		this(rand.nextDouble() * Main.WIDTH, rand.nextDouble() * Main.HEIGHT, 0, 0, temperature, fluid);
	}

	public Particle(double x, double y, double vx, double vy, double temperature, Fluid fluid) {
		position = new Vec2d(x, y);
		velocity = new Vec2d(vx, vy);
		this.temperature = temperature;
		this.fluid = fluid;
		anchored = false;
		setDisplayColor();
		particles.add(this);
	}

	public void render(Graphics g) {
		g.setColor(color);
		g.fillOval((int) (position.x - PARTICLE_SIZE / 2), Main.HEIGHT - (int) (position.y - PARTICLE_SIZE / 2) - 36, (int) PARTICLE_SIZE, (int) PARTICLE_SIZE);
	}
	
	public void tick() {
		updateTemperature();
		setDisplayColor();
		fluid = fluid.appropriateFluidForTemperature(temperature);
		velocity = velocity.add(new Vec2d(0, G)).scale(FRICTION);
		calculateInteractions();
	}
	
	private void updateTemperature() {
		int gridX = (int) position.x >> TemperatureGrid.GRID_COARSENESS;
		int gridY = (int) position.y >> TemperatureGrid.GRID_COARSENESS;

		if(gridX >= TemperatureGrid.grid.length) {
			gridX = TemperatureGrid.grid.length - 1;
		} else if(gridX < 0) gridX = 0;
		if(gridY >= TemperatureGrid.grid[0].length) {
			gridY = TemperatureGrid.grid[0].length - 1;
		} else if(gridY < 0) gridY = 0;

		double t = fluid.HT_RATE;
		double newTemp = (1 - t) * temperature + t * TemperatureGrid.grid[gridX][gridY];
		TemperatureGrid.grid[gridX][gridY] -= newTemp - temperature;
		temperature = newTemp;
	}

	public void setDisplayColor() {
		if(Main.showTempGrid) color = TemperatureGrid.getTempColor(temperature);
		else color = fluid.COLOR;
	}

	protected void calculateInteractions() {
		particles.stream().filter(p -> p != this).forEach(p -> {
			double naturalDistance = fluid.naturalDistance(p.fluid);
			double distance = p.position.subtract(position).length();
			double stress = distance - naturalDistance;

			if(stress <= fluid.maxStress(p.fluid)) {
				if(rand.nextDouble() < fluid.ELASTICITY * stress) {
					spawnParticleBetween(p, naturalDistance);
				} else influenceParticle(p, stress);
			}
		});
	}

	protected void spawnParticleBetween(Particle particle, double naturalDistance) {
		Vec2d betweenPos = particle.position.subtract(position).normalize().scale(naturalDistance).add(position);
		Particle newParticle = new Particle(betweenPos.x, betweenPos.y, velocity.x, velocity.y, temperature, fluid);
		particles.add(newParticle);
	}

	protected void influenceParticle(Particle particle, double stress) {
		if(particle.anchored) return;
		Vec2d force = position.subtract(particle.position).normalize().scale(fluid.strength(particle.fluid) * stress);
		particle.velocity = particle.velocity.add(new Vec2d(force.x, force.y));
	}
	
	public void updatePosition() {
		if(anchored) {
			velocity = Vec2d.ZERO;
		} else {
			position = position.add(velocity);

			if(position.y <= 0) {
				position = new Vec2d(position.x, -position.y);
				velocity = new Vec2d(velocity.x, -velocity.y).scale(0.5);
			}else if(position.y >= Main.HEIGHT - 1) {
				position = new Vec2d(position.x, 2 * Main.HEIGHT - 2 - position.y);
				velocity = new Vec2d(velocity.x, -velocity.y).scale(0.5);
			}
			if(position.x <= 0) {
				position = new Vec2d(-position.x, position.y);
				velocity = new Vec2d(-velocity.x, velocity.y).scale(0.5);
			}else if(position.x >= Main.WIDTH - 1) {
				position = new Vec2d(2 * Main.WIDTH - 2 - position.x, position.y);
				velocity = new Vec2d(-velocity.x, velocity.y).scale(0.5);
			}
		}
	}
}
