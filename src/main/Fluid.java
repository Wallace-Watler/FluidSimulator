package main;

import java.awt.*;

public enum Fluid {

    WATER(0.997, 0.00089, Color.white);

    public final double restingDensity;
    public final double viscosity;
    public final Color color;

    Fluid(double restingDensity, double viscosity, Color color) {
        this.restingDensity = restingDensity;
        this.viscosity = viscosity;
        this.color = color;
    }
}
