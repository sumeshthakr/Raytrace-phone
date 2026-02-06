package com.raytrace.phone.engine;

/** Generates viewing rays for a perspective camera with optional depth-of-field. */
public final class Viewport {
    private final Triplet eye;
    private final Triplet lowerLeftCorner;
    private final Triplet horizontal;
    private final Triplet vertical;
    private final Triplet axisU, axisV;
    private final double lensRadius;

    public Viewport(Triplet lookFrom, Triplet lookAt, Triplet worldUp,
                    double vertFovDeg, double aspect, double apertureDiameter, double focusDist) {
        lensRadius = apertureDiameter * 0.5;
        double halfHeight = Math.tan(Math.toRadians(vertFovDeg) * 0.5);
        double halfWidth = aspect * halfHeight;

        Triplet forward = lookFrom.minus(lookAt).unit();
        axisU = worldUp.cross(forward).unit();
        axisV = forward.cross(axisU);

        eye = lookFrom;
        horizontal = axisU.times(2.0 * halfWidth * focusDist);
        vertical = axisV.times(2.0 * halfHeight * focusDist);
        lowerLeftCorner = eye
            .minus(horizontal.times(0.5))
            .minus(vertical.times(0.5))
            .minus(forward.times(focusDist));
    }

    public Photon shootRay(double s, double t, java.util.Random rng) {
        Triplet offset = new Triplet(0, 0, 0);
        if (lensRadius > 1e-6) {
            Triplet diskPt = randomInUnitDisk(rng).times(lensRadius);
            offset = axisU.times(diskPt.a).plus(axisV.times(diskPt.b));
        }
        Triplet target = lowerLeftCorner.plus(horizontal.times(s)).plus(vertical.times(t));
        return new Photon(eye.plus(offset), target.minus(eye).minus(offset));
    }

    private static Triplet randomInUnitDisk(java.util.Random rng) {
        while (true) {
            double px = rng.nextDouble() * 2.0 - 1.0;
            double py = rng.nextDouble() * 2.0 - 1.0;
            if (px * px + py * py < 1.0) return new Triplet(px, py, 0);
        }
    }
}
