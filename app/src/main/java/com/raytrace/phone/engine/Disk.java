package com.raytrace.phone.engine;

/** A finite circular disk in 3D space. */
public final class Disk implements Renderable {
    private final Triplet center;
    private final Triplet normal;
    private final double outerRadius;
    private final SurfaceProperties surface;

    public Disk(Triplet center, Triplet normal, double outerRadius, SurfaceProperties surface) {
        this.center = center;
        this.normal = normal.unit();
        this.outerRadius = outerRadius;
        this.surface = surface;
    }

    @Override
    public Collision testHit(Photon ray, double tMin, double tMax) {
        double denom = normal.dot(ray.heading);
        if (Math.abs(denom) < 1e-10) return null;
        double t = center.minus(ray.origin).dot(normal) / denom;
        if (t < tMin || t > tMax) return null;

        Triplet hitPt = ray.at(t);
        double distFromCenter = hitPt.minus(center).length();
        if (distFromCenter > outerRadius) return null;

        boolean front = denom < 0;
        Triplet faceN = front ? normal : normal.negate();

        double u = distFromCenter / outerRadius;
        double v = Math.atan2(hitPt.c - center.c, hitPt.a - center.a) / (2.0 * Math.PI) + 0.5;
        return new Collision(t, hitPt, faceN, front, surface, u, v);
    }
}
