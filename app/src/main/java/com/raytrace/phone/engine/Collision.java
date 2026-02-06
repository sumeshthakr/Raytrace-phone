package com.raytrace.phone.engine;

/** Records details about where a Photon struck a surface. */
public final class Collision {
    public final double dist;
    public final Triplet point;
    public final Triplet outwardNormal;
    public final boolean frontHit;
    public final SurfaceProperties surface;
    public final double texU;
    public final double texV;

    public Collision(double dist, Triplet point, Triplet outwardNormal, boolean frontHit,
                     SurfaceProperties surface, double texU, double texV) {
        this.dist = dist;
        this.point = point;
        this.outwardNormal = outwardNormal;
        this.frontHit = frontHit;
        this.surface = surface;
        this.texU = texU;
        this.texV = texV;
    }
}
