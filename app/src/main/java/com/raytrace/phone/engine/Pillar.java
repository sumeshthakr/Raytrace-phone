package com.raytrace.phone.engine;

/** A vertical cylinder (aligned along Y axis) with finite height. */
public final class Pillar implements Renderable {
    private final Triplet baseCenter;
    private final double radius;
    private final double height;
    private final SurfaceProperties surface;

    public Pillar(Triplet baseCenter, double radius, double height, SurfaceProperties surface) {
        this.baseCenter = baseCenter;
        this.radius = radius;
        this.height = height;
        this.surface = surface;
    }

    @Override
    public Collision testHit(Photon ray, double tMin, double tMax) {
        // project onto XZ plane for lateral intersection
        double oa = ray.origin.a - baseCenter.a;
        double oc = ray.origin.c - baseCenter.c;
        double da = ray.heading.a;
        double dc = ray.heading.c;

        double quadA = da * da + dc * dc;
        double quadB = 2.0 * (oa * da + oc * dc);
        double quadC = oa * oa + oc * oc - radius * radius;
        double disc = quadB * quadB - 4.0 * quadA * quadC;
        if (disc < 0) return null;

        double sqrtDisc = Math.sqrt(disc);
        double t1 = (-quadB - sqrtDisc) / (2.0 * quadA);
        double t2 = (-quadB + sqrtDisc) / (2.0 * quadA);

        for (double t : new double[]{t1, t2}) {
            if (t < tMin || t > tMax) continue;
            Triplet pt = ray.at(t);
            double yLocal = pt.b - baseCenter.b;
            if (yLocal < 0 || yLocal > height) continue;

            Triplet outN = new Triplet(pt.a - baseCenter.a, 0, pt.c - baseCenter.c).unit();
            boolean front = ray.heading.dot(outN) < 0;
            if (!front) outN = outN.negate();

            double u = Math.atan2(pt.c - baseCenter.c, pt.a - baseCenter.a) / (2.0 * Math.PI) + 0.5;
            double v = yLocal / height;
            return new Collision(t, pt, outN, front, surface, u, v);
        }
        return null;
    }
}
