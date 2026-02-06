package com.raytrace.phone.engine;

/** An axis-aligned box defined by two corner points. */
public final class Slab implements Renderable {
    private final Triplet lo;
    private final Triplet hi;
    private final SurfaceProperties surface;

    public Slab(Triplet corner1, Triplet corner2, SurfaceProperties surface) {
        this.lo = new Triplet(
            Math.min(corner1.a, corner2.a),
            Math.min(corner1.b, corner2.b),
            Math.min(corner1.c, corner2.c)
        );
        this.hi = new Triplet(
            Math.max(corner1.a, corner2.a),
            Math.max(corner1.b, corner2.b),
            Math.max(corner1.c, corner2.c)
        );
        this.surface = surface;
    }

    @Override
    public Collision testHit(Photon ray, double tMin, double tMax) {
        double entryT = tMin;
        double exitT = tMax;
        int hitAxis = -1;
        boolean hitLow = true;

        double[] oVals = {ray.origin.a, ray.origin.b, ray.origin.c};
        double[] dVals = {ray.heading.a, ray.heading.b, ray.heading.c};
        double[] loVals = {lo.a, lo.b, lo.c};
        double[] hiVals = {hi.a, hi.b, hi.c};

        for (int axis = 0; axis < 3; axis++) {
            if (Math.abs(dVals[axis]) < 1e-12) {
                if (oVals[axis] < loVals[axis] || oVals[axis] > hiVals[axis]) return null;
            } else {
                double invD = 1.0 / dVals[axis];
                double near = (loVals[axis] - oVals[axis]) * invD;
                double far  = (hiVals[axis] - oVals[axis]) * invD;
                boolean nearIsLow = true;
                if (near > far) { double tmp = near; near = far; far = tmp; nearIsLow = false; }
                if (near > entryT) { entryT = near; hitAxis = axis; hitLow = nearIsLow; }
                if (far < exitT) exitT = far;
                if (entryT > exitT) return null;
            }
        }

        if (entryT < tMin || entryT > tMax) return null;
        Triplet pt = ray.at(entryT);
        double[] normalArr = {0, 0, 0};
        normalArr[hitAxis] = hitLow ? -1.0 : 1.0;
        Triplet outN = new Triplet(normalArr[0], normalArr[1], normalArr[2]);
        boolean front = ray.heading.dot(outN) < 0;
        if (!front) outN = outN.negate();

        return new Collision(entryT, pt, outN, front, surface, 0, 0);
    }
}
