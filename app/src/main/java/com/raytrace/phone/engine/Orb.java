package com.raytrace.phone.engine;

/** A sphere defined by center and radius. */
public final class Orb implements Renderable {
    private final Triplet center;
    private final double radius;
    private final SurfaceProperties surface;

    public Orb(Triplet center, double radius, SurfaceProperties surface) {
        this.center = center;
        this.radius = radius;
        this.surface = surface;
    }

    @Override
    public Collision testHit(Photon ray, double tMin, double tMax) {
        Triplet displacement = ray.origin.minus(center);
        double halfB = displacement.dot(ray.heading);
        double aCoeff = ray.heading.lengthSq();
        double cCoeff = displacement.lengthSq() - radius * radius;
        double discriminant = halfB * halfB - aCoeff * cCoeff;
        if (discriminant < 0) return null;

        double sqrtDisc = Math.sqrt(discriminant);
        double root = (-halfB - sqrtDisc) / aCoeff;
        if (root < tMin || root > tMax) {
            root = (-halfB + sqrtDisc) / aCoeff;
            if (root < tMin || root > tMax) return null;
        }

        Triplet hitPoint = ray.at(root);
        Triplet outNormal = hitPoint.minus(center).div(radius);
        boolean front = ray.heading.dot(outNormal) < 0;
        if (!front) outNormal = outNormal.negate();

        // spherical UV mapping
        double theta = Math.acos(-outNormal.b);
        double phi = Math.atan2(-outNormal.c, outNormal.a) + Math.PI;
        double u = phi / (2.0 * Math.PI);
        double v = theta / Math.PI;

        return new Collision(root, hitPoint, outNormal, front, surface, u, v);
    }
}
