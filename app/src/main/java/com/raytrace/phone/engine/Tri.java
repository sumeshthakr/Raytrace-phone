package com.raytrace.phone.engine;

/** A single triangle defined by three vertices. Uses Moller-Trumbore intersection. */
public final class Tri implements Renderable {
    private final Triplet v0, v1, v2;
    private final SurfaceProperties surface;

    public Tri(Triplet v0, Triplet v1, Triplet v2, SurfaceProperties surface) {
        this.v0 = v0;
        this.v1 = v1;
        this.v2 = v2;
        this.surface = surface;
    }

    @Override
    public Collision testHit(Photon ray, double tMin, double tMax) {
        Triplet edge1 = v1.minus(v0);
        Triplet edge2 = v2.minus(v0);
        Triplet pvec = ray.heading.cross(edge2);
        double det = edge1.dot(pvec);
        if (Math.abs(det) < 1e-10) return null;

        double invDet = 1.0 / det;
        Triplet tvec = ray.origin.minus(v0);
        double baryU = tvec.dot(pvec) * invDet;
        if (baryU < 0.0 || baryU > 1.0) return null;

        Triplet qvec = tvec.cross(edge1);
        double baryV = ray.heading.dot(qvec) * invDet;
        if (baryV < 0.0 || baryU + baryV > 1.0) return null;

        double t = edge2.dot(qvec) * invDet;
        if (t < tMin || t > tMax) return null;

        Triplet hitPt = ray.at(t);
        Triplet faceN = edge1.cross(edge2).unit();
        boolean front = ray.heading.dot(faceN) < 0;
        if (!front) faceN = faceN.negate();

        return new Collision(t, hitPt, faceN, front, surface, baryU, baryV);
    }
}
