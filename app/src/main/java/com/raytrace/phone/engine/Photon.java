package com.raytrace.phone.engine;

/** A directional ray with origin and normalized heading. */
public final class Photon {
    public final Triplet origin;
    public final Triplet heading;

    public Photon(Triplet origin, Triplet heading) {
        this.origin = origin;
        this.heading = heading.unit();
    }

    public Triplet at(double dist) {
        return origin.plus(heading.times(dist));
    }
}
