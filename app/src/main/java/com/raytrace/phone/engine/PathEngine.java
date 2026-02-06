package com.raytrace.phone.engine;

import android.graphics.Bitmap;
import java.util.Random;

/**
 * PathEngine traces photons through the scene, bouncing off surfaces
 * according to their material properties, and accumulates color.
 */
public final class PathEngine {

    private final ObjectGroup world;
    private final RenderConfig cfg;
    private final Random rng = new Random(42);

    public PathEngine(ObjectGroup world, RenderConfig cfg) {
        this.world = world;
        this.cfg = cfg;
    }

    /** Render a full frame into a Bitmap. Calls progressCallback with 0..100. */
    public Bitmap renderFrame(int pixW, int pixH, Viewport camera,
                              Triplet skyTop, Triplet skyBot,
                              Triplet ambient,
                              ProgressCallback progressCb) {
        Bitmap output = Bitmap.createBitmap(pixW, pixH, Bitmap.Config.ARGB_8888);
        int totalRows = pixH;
        int[] rowPixels = new int[pixW];

        for (int row = 0; row < pixH; row++) {
            for (int col = 0; col < pixW; col++) {
                Triplet accumulated = new Triplet(0, 0, 0);
                int spp = cfg.enableAA ? Math.max(cfg.samplesPerPixel, 2) : cfg.samplesPerPixel;
                for (int s = 0; s < spp; s++) {
                    double jitterU = cfg.enableAA ? rng.nextDouble() : 0.5;
                    double jitterV = cfg.enableAA ? rng.nextDouble() : 0.5;
                    double u = (col + jitterU) / (double) pixW;
                    double v = 1.0 - (row + jitterV) / (double) pixH;
                    Photon viewRay = camera.shootRay(u, v, rng);
                    accumulated = accumulated.plus(tracePhoton(viewRay, cfg.maxBounces, skyTop, skyBot, ambient));
                }
                accumulated = accumulated.div(spp).times(cfg.brightnessMultiplier);
                // gamma correct (pow 1/2.2)
                accumulated = new Triplet(
                    Math.pow(Math.max(0, accumulated.a), 1.0 / 2.2),
                    Math.pow(Math.max(0, accumulated.b), 1.0 / 2.2),
                    Math.pow(Math.max(0, accumulated.c), 1.0 / 2.2)
                );
                rowPixels[col] = accumulated.toPackedARGB();
            }
            output.setPixels(rowPixels, 0, pixW, row, 0, pixW, 1);
            if (progressCb != null && row % 8 == 0) {
                progressCb.onProgress((int)(100.0 * row / totalRows));
            }
        }
        if (progressCb != null) progressCb.onProgress(100);
        return output;
    }

    private Triplet tracePhoton(Photon ray, int bouncesLeft,
                                Triplet skyTop, Triplet skyBot, Triplet ambient) {
        if (bouncesLeft <= 0) return ambient;

        Collision hit = world.testHit(ray, 0.001, 1e12);
        if (hit == null) return skyGradient(ray, skyTop, skyBot);

        SurfaceProperties mat = hit.surface;

        // Emissive surfaces glow
        if (mat.kind == SurfaceProperties.Kind.EMISSIVE) {
            return mat.emissionColor.times(mat.emissionPower);
        }

        Triplet baseColor = resolveColor(mat, hit);

        // Shadow check
        double shadowFactor = 1.0;
        if (cfg.enableShadows) {
            Triplet lightDir = new Triplet(0.6, 0.9, -0.4).unit();
            Photon shadowRay = new Photon(hit.point.plus(hit.outwardNormal.times(0.002)), lightDir);
            if (cfg.enableSoftShadows) {
                int shadowSamples = 4;
                int blocked = 0;
                for (int si = 0; si < shadowSamples; si++) {
                    Triplet jitter = randomOnHemisphere(hit.outwardNormal).times(0.15);
                    Photon jitteredShadow = new Photon(shadowRay.origin, lightDir.plus(jitter));
                    if (world.testHit(jitteredShadow, 0.001, 1e12) != null) blocked++;
                }
                shadowFactor = 1.0 - 0.7 * ((double) blocked / shadowSamples);
            } else {
                if (world.testHit(shadowRay, 0.001, 1e12) != null) shadowFactor = 0.3;
            }
        }

        // Ambient occlusion
        double aoFactor = 1.0;
        if (cfg.enableAO) {
            int aoSamples = 5;
            int occluded = 0;
            for (int ai = 0; ai < aoSamples; ai++) {
                Triplet aoDir = randomOnHemisphere(hit.outwardNormal);
                Photon aoRay = new Photon(hit.point.plus(hit.outwardNormal.times(0.002)), aoDir);
                if (world.testHit(aoRay, 0.001, 2.0) != null) occluded++;
            }
            aoFactor = 1.0 - 0.6 * ((double) occluded / aoSamples);
        }

        switch (mat.kind) {
            case METALLIC:
                if (!cfg.enableReflections) {
                    return directLighting(hit, baseColor, shadowFactor, aoFactor, ambient);
                }
                Triplet reflDir = ray.heading.reflect(hit.outwardNormal);
                if (mat.fuzz > 0) {
                    reflDir = reflDir.plus(randomOnHemisphere(hit.outwardNormal).times(mat.fuzz)).unit();
                }
                Photon reflRay = new Photon(hit.point.plus(hit.outwardNormal.times(0.002)), reflDir);
                Triplet reflColor = tracePhoton(reflRay, bouncesLeft - 1, skyTop, skyBot, ambient);
                return baseColor.mul(reflColor).times(shadowFactor * aoFactor);

            case GLASS:
                if (!cfg.enableRefractions) {
                    return directLighting(hit, baseColor, shadowFactor, aoFactor, ambient);
                }
                return handleGlass(ray, hit, baseColor, bouncesLeft, skyTop, skyBot, ambient);

            case GLOSSY:
                Triplet glossRef = ray.heading.reflect(hit.outwardNormal);
                glossRef = glossRef.plus(randomOnHemisphere(hit.outwardNormal).times(mat.fuzz)).unit();
                Photon glossRay = new Photon(hit.point.plus(hit.outwardNormal.times(0.002)), glossRef);
                Triplet glossBounce = tracePhoton(glossRay, bouncesLeft - 1, skyTop, skyBot, ambient);
                Triplet diffPart = directLighting(hit, baseColor, shadowFactor, aoFactor, ambient);
                double blendGloss = 1.0 - mat.fuzz;
                return diffPart.times(mat.fuzz).plus(glossBounce.mul(baseColor).times(blendGloss));

            default: // DIFFUSE
                Triplet scattered = randomOnHemisphere(hit.outwardNormal);
                Photon diffRay = new Photon(hit.point.plus(hit.outwardNormal.times(0.002)), scattered);
                Triplet indirectLight = tracePhoton(diffRay, bouncesLeft - 1, skyTop, skyBot, ambient);
                Triplet direct = directLighting(hit, baseColor, shadowFactor, aoFactor, ambient);
                return direct.times(0.6).plus(indirectLight.mul(baseColor).times(0.4));
        }
    }

    private Triplet handleGlass(Photon ray, Collision hit, Triplet baseColor,
                                int bouncesLeft, Triplet skyTop, Triplet skyBot, Triplet ambient) {
        double eta = hit.frontHit ? (1.0 / hit.surface.ior) : hit.surface.ior;
        Triplet unitDir = ray.heading.unit();
        double cosTheta = Math.min(unitDir.negate().dot(hit.outwardNormal), 1.0);
        double fresnelChance = Triplet.schlickReflectance(cosTheta, hit.surface.ior);

        Triplet refractedDir = unitDir.refract(hit.outwardNormal, eta);
        Photon nextRay;
        if (refractedDir == null || rng.nextDouble() < fresnelChance) {
            Triplet reflD = unitDir.reflect(hit.outwardNormal);
            nextRay = new Photon(hit.point.plus(hit.outwardNormal.times(0.002)), reflD);
        } else {
            nextRay = new Photon(hit.point.minus(hit.outwardNormal.times(0.002)), refractedDir);
        }
        return baseColor.mul(tracePhoton(nextRay, bouncesLeft - 1, skyTop, skyBot, ambient));
    }

    private Triplet directLighting(Collision hit, Triplet baseColor,
                                   double shadowFactor, double aoFactor, Triplet ambient) {
        Triplet lightDir = new Triplet(0.6, 0.9, -0.4).unit();
        double diffuseIntensity = Math.max(0, hit.outwardNormal.dot(lightDir));
        Triplet lightContrib = baseColor.times(diffuseIntensity * shadowFactor * aoFactor);
        return lightContrib.plus(ambient.mul(baseColor));
    }

    private Triplet resolveColor(SurfaceProperties mat, Collision hit) {
        if (mat.checkered && cfg.enableTextures) {
            double scale = 4.0;
            boolean patternA = ((int)(Math.floor(hit.point.a * scale)
                                    + Math.floor(hit.point.c * scale))) % 2 == 0;
            return patternA ? mat.tint : mat.tint.times(0.15);
        }
        return mat.tint;
    }

    private Triplet randomOnHemisphere(Triplet normal) {
        Triplet p = randomInUnitSphere();
        return p.dot(normal) > 0 ? p : p.negate();
    }

    private Triplet randomInUnitSphere() {
        while (true) {
            double rx = rng.nextDouble() * 2.0 - 1.0;
            double ry = rng.nextDouble() * 2.0 - 1.0;
            double rz = rng.nextDouble() * 2.0 - 1.0;
            if (rx * rx + ry * ry + rz * rz < 1.0) {
                return new Triplet(rx, ry, rz).unit();
            }
        }
    }

    private Triplet skyGradient(Photon ray, Triplet top, Triplet bot) {
        double blend = 0.5 * (ray.heading.unit().b + 1.0);
        return bot.times(1.0 - blend).plus(top.times(blend));
    }

    public interface ProgressCallback {
        void onProgress(int percent);
    }
}
