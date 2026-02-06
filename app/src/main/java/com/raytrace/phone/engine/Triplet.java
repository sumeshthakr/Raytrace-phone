package com.raytrace.phone.engine;

/**
 * Represents a 3-component value used for positions, directions, and colors.
 * All arithmetic is immutable - each operation returns a fresh instance.
 */
public final class Triplet {
    public final double a, b, c;

    public Triplet(double a, double b, double c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public Triplet plus(Triplet o)  { return new Triplet(a + o.a, b + o.b, c + o.c); }
    public Triplet minus(Triplet o) { return new Triplet(a - o.a, b - o.b, c - o.c); }
    public Triplet times(double s)  { return new Triplet(a * s, b * s, c * s); }
    public Triplet mul(Triplet o)   { return new Triplet(a * o.a, b * o.b, c * o.c); }
    public Triplet div(double s)    { return new Triplet(a / s, b / s, c / s); }
    public Triplet negate()         { return new Triplet(-a, -b, -c); }

    public double dot(Triplet o)    { return a * o.a + b * o.b + c * o.c; }
    public double lengthSq()        { return dot(this); }
    public double length()          { return Math.sqrt(lengthSq()); }

    public Triplet unit() {
        double len = length();
        if (len < 1e-14) return new Triplet(0, 0, 0);
        return div(len);
    }

    public Triplet cross(Triplet o) {
        return new Triplet(
            b * o.c - c * o.b,
            c * o.a - a * o.c,
            a * o.b - b * o.a
        );
    }

    public Triplet reflect(Triplet normal) {
        return this.minus(normal.times(2.0 * this.dot(normal)));
    }

    /** Attempt refraction via Snell's law; returns null on total internal reflection. */
    public Triplet refract(Triplet normal, double etaRatio) {
        double cosIncident = negate().dot(normal);
        double sinSqTransmit = etaRatio * etaRatio * (1.0 - cosIncident * cosIncident);
        if (sinSqTransmit > 1.0) return null; // total internal reflection
        double cosTransmit = Math.sqrt(1.0 - sinSqTransmit);
        return this.times(etaRatio).plus(normal.times(etaRatio * cosIncident - cosTransmit));
    }

    /** Schlick approximation for Fresnel reflectance */
    public static double schlickReflectance(double cosAngle, double refIdx) {
        double r0 = (1.0 - refIdx) / (1.0 + refIdx);
        r0 = r0 * r0;
        double oneMinusCos = 1.0 - cosAngle;
        return r0 + (1.0 - r0) * oneMinusCos * oneMinusCos * oneMinusCos * oneMinusCos * oneMinusCos;
    }

    public Triplet clampChannels(double lo, double hi) {
        return new Triplet(
            Math.max(lo, Math.min(hi, a)),
            Math.max(lo, Math.min(hi, b)),
            Math.max(lo, Math.min(hi, c))
        );
    }

    public int toPackedARGB() {
        Triplet clamped = clampChannels(0.0, 1.0);
        int ri = (int)(clamped.a * 255.0 + 0.5);
        int gi = (int)(clamped.b * 255.0 + 0.5);
        int bi = (int)(clamped.c * 255.0 + 0.5);
        return 0xFF000000 | (ri << 16) | (gi << 8) | bi;
    }

    @Override
    public String toString() {
        return String.format("(%f, %f, %f)", a, b, c);
    }
}
