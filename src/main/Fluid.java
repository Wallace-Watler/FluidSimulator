package main;

import java.awt.Color;

public enum Fluid {

	STEAM(100, 0.99, 0.05, 373, 0.5, Math.pow(30, 2), Color.white, null),
	WATER(100, 0.95, 0.01, 373, 0.5, Math.pow(30, 2), Color.blue, STEAM);
	
	/**Repulsive force constant. Akin to rigidity.*/
	public final double replusion;
	/**Force of friction, should be between 0 and 1. Lower numbers = higher friction.
	 * Akin to viscosity.*/
	public final double friction;
	/**Rate of heat transfer, should be between 0 and 1.*/
	public final double rateOfHeatTransfer;
	public final double boilingPoint;
	/**Brownian motion constant.*/
	public final double brown;
	/**Squared distance within which particles should calculate force due to other particles.
	 * Akin to compressibility.*/
	public final double epsilon;
	public final Color color;
	public final Fluid vapor;
	
	private Fluid(double r, double f, double t, double bp, double b, double e, Color c, Fluid v) {
		replusion = r;
		friction = f;
		rateOfHeatTransfer = t;
		boilingPoint = bp;
		brown = b;
		epsilon = e;
		color = c;
		vapor = v;
	}
}
