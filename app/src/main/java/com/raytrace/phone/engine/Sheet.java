package com.raytrace.phone.engine;

/** An infinite flat plane defined by a point on it and its normal direction. */
public final class Sheet implements Renderable {
    private final Triplet anchor;
    private final Triplet normal;
    private final SurfaceProperties surface;

    public Sheet(Triplet anchor, Triplet normal, SurfaceProperties surface) {
        this.anchor = anchor;
        this.normal = normal.unit();
        this.surface = surface;
    }

    @Override
    public Collision testHit(Photon ray, double tMin, double tMax) {
        double denom = normal.dot(ray.heading);
        if (Math.abs(denom) < 1e-10) return null;
        double t = anchor.minus(ray.origin).dot(normal) / denom;
        if (t < tMin || t > tMax) return null;

        Triplet hitPoint = ray.at(t);
        boolean front = denom < 0;
        Triplet faceNormal = front ? normal : normal.negate();

        // planar UV mapping based on two tangent axes
        double u = hitPoint.a * 0.25;
        double v = hitPoint.c * 0.25;
        u = u - Math.floor(u);
        v = v - Math.floor(v);

        return new Collision(t, hitPoint, faceNormal, front, surface, u, v);
    }
}
