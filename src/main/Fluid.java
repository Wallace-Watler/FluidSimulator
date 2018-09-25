package main;

import java.awt.Color;

public enum Fluid {

	STEAM(100, 0.95, 0.1, 373, 0.5, Math.pow(30, 2), Color.white, null),
	WATER(100, 0.95, 0.1, 373, 0.5, Math.pow(30, 2), Color.blue, STEAM);
	
	/**Repulsive force constant. Akin to rigidity.*/
	private final double replusion;
	/**Force of friction, should be between 0 and 1. Lower numbers = higher friction.
	 * Akin to viscosity.*/
	private final double friction;
	/**Rate of heat transfer, should be between 0 and 1.*/
	private final double rateOfHeatTransfer;
	private final double boilingPoint;
	/**Brownian motion constant.*/
	private final double brown;
	/**Squared distance within which particles should calculate force due to other particles.
	 * Akin to compressibility.*/
	private final double epsilon;
	private final Color color;
	private final Fluid vapor;
	
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
