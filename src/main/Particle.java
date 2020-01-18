package main;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Particle {

    private static final double RENDER_RADIUS = 2;

    public final Fluid fluid;
    public final double mass;
    private double density;
    private double pressure;
    private Vector pressureGradient;
    private Vector viscousTerm;
    private Vector velocity;
    private Vector position;
    private double interactionRadius;
    private Map<Particle, Double> particlesToInteractWith;

    public Particle(Fluid fluid, double mass, double posX, double posY) {
        this.fluid = fluid;
        this.mass = mass;
        density = fluid.restingDensity;
        pressure = 0;
        pressureGradient = Vector.ZERO;
        viscousTerm = Vector.ZERO;
        velocity = Vector.ZERO;
        position = new Vector(posX, posY);
        interactionRadius = Math.sqrt(8 * mass / (Math.PI * density));
        particlesToInteractWith = new HashMap<>();
    }

    // For creating ghost particles
    private Particle(Vector mirrorPos, Particle mirror) {
        fluid = mirror.fluid;
        mass = mirror.mass;
        density = mirror.density;
        pressure = mirror.pressure;
        velocity = Vector.ZERO;
        position = mirrorPos;
    }

    public void calculateDensityAndPressure(List<Particle> particles) {
        particlesToInteractWith.clear();
        for(Particle p : particles) {
            double distance = distanceTo(p);
            if(distance < interactionRadius) particlesToInteractWith.put(p, distance);
        }

        addGhostParticles();

        density = particlesToInteractWith.entrySet().stream()
                .mapToDouble(e -> e.getKey().mass * Math.pow(interactionRadius * interactionRadius - e.getValue() * e.getValue(), 3)).sum();
        density *= 4 / (Math.PI * Math.pow(interactionRadius, 8));
        pressure = calculatePressure();
    }

    private void addGhostParticles() {
        Map<Particle, Double> ghostParticles = new HashMap<>();

        if(position.y - interactionRadius < 0) {
            Particle mirror = new Particle(new Vector(position.x, -position.y), this);
            for(Map.Entry<Particle, Double> e : particlesToInteractWith.entrySet()) {
                Particle realParticle = e.getKey();
                double mirrorToRealParticleDist = mirror.distanceTo(realParticle);
                if(mirrorToRealParticleDist < interactionRadius)
                    ghostParticles.put(new Particle(new Vector(realParticle.position.x, -realParticle.position.y), realParticle), mirrorToRealParticleDist);
            }
        }else if(position.y + interactionRadius >= Main.HEIGHT - 1) {
            Particle mirror = new Particle(new Vector(position.x, 2 * Main.HEIGHT - 2 - position.y), this);
            for(Map.Entry<Particle, Double> e : particlesToInteractWith.entrySet()) {
                Particle realParticle = e.getKey();
                double mirrorToRealParticleDist = mirror.distanceTo(realParticle);
                if(mirrorToRealParticleDist < interactionRadius)
                    ghostParticles.put(new Particle(new Vector(realParticle.position.x, 2 * Main.HEIGHT - 2 - realParticle.position.y), realParticle), mirrorToRealParticleDist);
            }
        }

        if(position.x - interactionRadius < 0) {
            Particle mirror = new Particle(new Vector(-position.x, position.y), this);
            for(Map.Entry<Particle, Double> e : particlesToInteractWith.entrySet()) {
                Particle realParticle = e.getKey();
                double mirrorToRealParticleDist = mirror.distanceTo(realParticle);
                if(mirrorToRealParticleDist < interactionRadius)
                    ghostParticles.put(new Particle(new Vector(-realParticle.position.x, realParticle.position.y), realParticle), mirrorToRealParticleDist);
            }
        }else if(position.x + interactionRadius >= Main.WIDTH - 1) {
            Particle mirror = new Particle(new Vector(2 * Main.WIDTH - 2 - position.x, position.y), this);
            for(Map.Entry<Particle, Double> e : particlesToInteractWith.entrySet()) {
                Particle realParticle = e.getKey();
                double mirrorToRealParticleDist = mirror.distanceTo(realParticle);
                if(mirrorToRealParticleDist < interactionRadius)
                    ghostParticles.put(new Particle(new Vector(2 * Main.WIDTH - 2 - realParticle.position.x, realParticle.position.y), realParticle), mirrorToRealParticleDist);
            }
        }

        particlesToInteractWith.putAll(ghostParticles);
    }

    public void calculatePressureGradientAndViscousTerm() {
        pressureGradient = Vector.ZERO;
        viscousTerm = Vector.ZERO;
        for(Map.Entry<Particle, Double> e : particlesToInteractWith.entrySet()) {
            Particle other = e.getKey();
            double distance = e.getValue();
            if(other != this && distance != 0) {
                double radiusMinusDistance = interactionRadius - distance;
                double d = other.mass * ((pressure + other.pressure) / (2 * density * other.density)) * radiusMinusDistance * radiusMinusDistance;
                pressureGradient = pressureGradient.add(position.subtract(other.position).scale(d / distance));
                viscousTerm = viscousTerm.add(other.velocity.subtract(velocity).scale(other.mass * radiusMinusDistance / other.density));
            }
        }
        double piTimesRadiusPow5 = Math.PI * Math.pow(interactionRadius, 5);
        pressureGradient = pressureGradient.scale(-30 / piTimesRadiusPow5);
        viscousTerm = viscousTerm.scale(120 * fluid.viscosity / (7 * density * piTimesRadiusPow5));
    }

    public void integratePosition(double dt) {
        Vector acceleration = Main.G.subtract(pressureGradient).add(viscousTerm);
        velocity = velocity.add(acceleration.scale(dt));
        position = position.add(velocity.scale(dt));

        if(position.y < 0) {
            position = new Vector(position.x, 0);
            if(velocity.y < 0) velocity = new Vector(velocity.x, -0.5 * velocity.y);
        }else if(position.y >= Main.HEIGHT - 1) {
            position = new Vector(position.x, Main.HEIGHT - 2);
            if(velocity.y > 0) velocity = new Vector(velocity.x, -0.5 * velocity.y);
        }
        if(position.x < 0) {
            position = new Vector(0, position.y);
            if(velocity.x < 0) velocity = new Vector(-0.5 * velocity.x, velocity.y);
        }else if(position.x >= Main.WIDTH - 1) {
            position = new Vector(Main.WIDTH - 2, position.y);
            if(velocity.x > 0) velocity = new Vector(-0.5 * velocity.x, velocity.y);
        }
    }

    public void render(Graphics g) {
        g.setColor(fluid.color);
        g.fillOval((int) (position.x - RENDER_RADIUS), (int) (position.y - RENDER_RADIUS), (int) (2 * RENDER_RADIUS), (int) (2 * RENDER_RADIUS));
    }

    private double distanceTo(Particle other) {
        return other.position.subtract(position).length();
    }

    private double calculatePressure() {
        return 2500 * (density - fluid.restingDensity);
    }
}
