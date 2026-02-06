package com.raytrace.phone.engine;

/** Describes how a surface interacts with light. */
public final class SurfaceProperties {

    public enum Kind { DIFFUSE, METALLIC, GLASS, EMISSIVE, GLOSSY }

    public final Kind kind;
    public final Triplet tint;           // base color / albedo
    public final double fuzz;            // roughness for metallic/glossy [0..1]
    public final double ior;             // index of refraction for glass
    public final Triplet emissionColor;  // emission for emissive surfaces
    public final double emissionPower;   // brightness multiplier for emission
    public final boolean checkered;      // procedural checker pattern

    private SurfaceProperties(Kind kind, Triplet tint, double fuzz, double ior,
                              Triplet emissionColor, double emissionPower, boolean checkered) {
        this.kind = kind;
        this.tint = tint;
        this.fuzz = fuzz;
        this.ior = ior;
        this.emissionColor = emissionColor;
        this.emissionPower = emissionPower;
        this.checkered = checkered;
    }

    public static SurfaceProperties diffuse(Triplet tint) {
        return new SurfaceProperties(Kind.DIFFUSE, tint, 0, 1.0, new Triplet(0,0,0), 0, false);
    }

    public static SurfaceProperties diffuseChecked(Triplet tint) {
        return new SurfaceProperties(Kind.DIFFUSE, tint, 0, 1.0, new Triplet(0,0,0), 0, true);
    }

    public static SurfaceProperties metallic(Triplet tint, double fuzz) {
        return new SurfaceProperties(Kind.METALLIC, tint, Math.min(fuzz, 1.0), 1.0,
                                     new Triplet(0,0,0), 0, false);
    }

    public static SurfaceProperties glass(double ior) {
        return new SurfaceProperties(Kind.GLASS, new Triplet(1,1,1), 0, ior,
                                     new Triplet(0,0,0), 0, false);
    }

    public static SurfaceProperties tintedGlass(Triplet tint, double ior) {
        return new SurfaceProperties(Kind.GLASS, tint, 0, ior,
                                     new Triplet(0,0,0), 0, false);
    }

    public static SurfaceProperties emissive(Triplet color, double power) {
        return new SurfaceProperties(Kind.EMISSIVE, color, 0, 1.0, color, power, false);
    }

    public static SurfaceProperties glossy(Triplet tint, double roughness) {
        return new SurfaceProperties(Kind.GLOSSY, tint, roughness, 1.0,
                                     new Triplet(0,0,0), 0, false);
    }
}
