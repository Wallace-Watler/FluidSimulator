package main.simulation;

import java.awt.Color;

/**
 * Each fluid contains characteristics defining how particles should behave.
 * Depending on its values, a "fluid" may not act at all like an actual fluid.
 */
public enum Fluid {

	WATER(10, 0.02, 12, 0, false, 0.01, 373, true, 0.1, new Color(64, 64, 255)),
	STEAM(100, 0.1, 20, 0, false, 0.04, 373, false, 1, new Color(255, 255, 255)),
	OIL(20, 0.02, 5, 0, false, 0.02, 450, true, 0.05, new Color(127, 64, 0)),
	GAS(100, 0.1, 20, 0, false, 0.04, 450, false, 1, new Color(255, 255, 64));

	/**Distance between particles where stress is zero. When two adjacent
	 * particles have different natural distances, their geometric mean will be used.*/
	public final double NATURAL_DIST;

	/**How strongly the particle pulls (proportional to stress) to restore its natural distance.*/
	public final double STRENGTH;

	/**Maximum distance to another particle (stress) within which particles will interact with each other.*/
	public final double MAX_STRESS;

	/**Chance to spawn a new particle proportional to stress.*/
	public final double ELASTICITY;

	/**If true, particles will stick to surfaces.*/
	public final boolean IS_STICKY;

	/**Rate of heat transfer, should be between 0 and 1.*/
	public final double HT_RATE;

	/**Temperature at which the fluid will change phases, whether that's boiling or condensing.*/
	public final double BOILING_POINT;

	/**Whether the fluid boils (true) or condenses (false).*/
	public final boolean VAPORIZES;

	/**The fluid that this fluid becomes during a phase change.*/
	private Fluid otherState;

	/**Brownian motion constant.*/
	public final double BROWN;

	/**Color to be rendered as.*/
	public final Color COLOR;

	Fluid(double nd, double s, double m, double e, boolean is, double t, double bp, boolean v, double b, Color c) {
		NATURAL_DIST = nd;
		STRENGTH = s;
		MAX_STRESS = m;
		ELASTICITY = e;
		IS_STICKY = is;
		HT_RATE = t;
		BOILING_POINT = bp;
		VAPORIZES = v;
		BROWN = b;
		COLOR = c;
	}

	public Fluid appropriateFluidForTemperature(double temp) {
		return temp > BOILING_POINT == VAPORIZES ? otherState : this;
	}

	/**The natural distance between two fluids is the geometric mean
	 * of their respective natural distances.*/
	public double naturalDistance(Fluid fluid) {
		return NATURAL_DIST != fluid.NATURAL_DIST
				? Math.sqrt(NATURAL_DIST * fluid.NATURAL_DIST)
				: NATURAL_DIST;
	}

	/**The strength between two fluids is the geometric mean
	 * of their respective strengths.*/
	public double strength(Fluid fluid) {
		return STRENGTH != fluid.STRENGTH
				? Math.sqrt(STRENGTH * fluid.STRENGTH)
				: STRENGTH;
	}

	/**The max stress between two fluids is the minimum
	 * of their respective max stresses.*/
	public double maxStress(Fluid fluid) {
		return Math.min(MAX_STRESS, fluid.MAX_STRESS);
	}

	static {
		WATER.otherState = STEAM;
		STEAM.otherState = WATER;
		OIL.otherState = GAS;
		GAS.otherState = OIL;
	}
}
