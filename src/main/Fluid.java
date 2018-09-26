package main;

import java.awt.Color;

public enum Fluid {

	STEAM(300, 0.999, 0.05, 373, 1, Math.pow(30, 2), Color.white, null),
	WATER(300, 0.975, 0.01, 373, 0.1, Math.pow(20, 2), Color.blue, STEAM);
	
	/**Repulsive force constant. Akin to rigidity.*/
	private double repulsion;
	/**Force of friction, should be between 0 and 1. Lower numbers = higher friction.
	 * Akin to viscosity.*/
	private double friction;
	/**Rate of heat transfer, should be between 0 and 1.*/
	private double rateOfHeatTransfer;
	private double boilingPoint;
	/**Brownian motion constant.*/
	private double brown;
	/**Squared distance within which particles should calculate force due to other particles.
	 * Akin to compressibility.*/
	private double epsilon;
	private Color color;
	private Fluid vapor;
	
	private Fluid(double r, double f, double t, double bp, double b, double e, Color c, Fluid v) {
		repulsion = r;
		friction = f;
		rateOfHeatTransfer = t;
		boilingPoint = bp;
		brown = b;
		epsilon = e;
		color = c;
		vapor = v;
	}
	
	public double getRepulsion() {
		return repulsion;
	}
	
	public double getFriction() {
		return friction;
	}
	
	public double getRateOfHeatTransfer() {
		return rateOfHeatTransfer;
	}
	
	public double getBoilingPoint() {
		return boilingPoint;
	}
	
	public double getBrown() {
		return brown;
	}
	
	public double getEpsilon() {
		return epsilon;
	}
	
	public Color getColor() {
		return color;
	}
	
	public Fluid getVapor() {
		return vapor;
	}
}
