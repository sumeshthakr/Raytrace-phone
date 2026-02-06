package com.raytrace.phone.scenes;

import com.raytrace.phone.engine.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Builds and returns all 20 demo scenes.
 * Each scene populates an ObjectGroup with various shapes and materials.
 */
public final class SceneCatalog {

    private SceneCatalog() {}

    public static List<SceneBlueprint> allScenes() {
        List<SceneBlueprint> catalog = new ArrayList<>();
        catalog.add(cornellBoxClassic());
        catalog.add(mirrorSpheres());
        catalog.add(glassPrism());
        catalog.add(metallicShowcase());
        catalog.add(checkerboardFloor());
        catalog.add(nestedTransparent());
        catalog.add(coloredLights());
        catalog.add(shadowGallery());
        catalog.add(fogDepthScene());
        catalog.add(cylinderArray());
        catalog.add(triangleMesh());
        catalog.add(planeIntersection());
        catalog.add(glossyTable());
        catalog.add(discoBall());
        catalog.add(underwaterCaustics());
        catalog.add(sunsetGradient());
        catalog.add(infiniteMirrors());
        catalog.add(diamondRefraction());
        catalog.add(spotLightDemo());
        catalog.add(mixedMaterials());
        return catalog;
    }

    // ── Scene 1: Cornell Box Classic ──
    private static SceneBlueprint cornellBoxClassic() {
        return new SceneBlueprint() {
            @Override public String title() { return "Cornell Box Classic"; }
            @Override public Triplet skyTop()    { return new Triplet(0.0, 0.0, 0.0); }
            @Override public Triplet skyBottom() { return new Triplet(0.0, 0.0, 0.0); }

            @Override public void populate(ObjectGroup w, RenderConfig cfg) {
                double wallSize = 5.0;
                // floor
                w.place(new Sheet(new Triplet(0, 0, 0), new Triplet(0, 1, 0),
                    SurfaceProperties.diffuse(new Triplet(0.73, 0.73, 0.73))));
                // ceiling
                w.place(new Sheet(new Triplet(0, wallSize, 0), new Triplet(0, -1, 0),
                    SurfaceProperties.diffuse(new Triplet(0.73, 0.73, 0.73))));
                // back wall
                w.place(new Sheet(new Triplet(0, 0, -wallSize), new Triplet(0, 0, 1),
                    SurfaceProperties.diffuse(new Triplet(0.73, 0.73, 0.73))));
                // left wall (red)
                w.place(new Sheet(new Triplet(-wallSize * 0.5, 0, 0), new Triplet(1, 0, 0),
                    SurfaceProperties.diffuse(new Triplet(0.65, 0.05, 0.05))));
                // right wall (green)
                w.place(new Sheet(new Triplet(wallSize * 0.5, 0, 0), new Triplet(-1, 0, 0),
                    SurfaceProperties.diffuse(new Triplet(0.12, 0.45, 0.15))));
                // ceiling light
                w.place(new Disk(new Triplet(0, wallSize - 0.01, -wallSize * 0.4),
                    new Triplet(0, -1, 0), 1.2,
                    SurfaceProperties.emissive(new Triplet(1.0, 0.95, 0.8), 8.0)));
                // tall box
                w.place(new Slab(new Triplet(-1.2, 0, -3.5), new Triplet(0.2, 3.0, -2.0),
                    SurfaceProperties.diffuse(new Triplet(0.73, 0.73, 0.73))));
                // short box
                w.place(new Slab(new Triplet(0.6, 0, -1.8), new Triplet(2.0, 1.5, -0.5),
                    SurfaceProperties.diffuse(new Triplet(0.73, 0.73, 0.73))));
            }
            @Override public Triplet cameraOrigin(RenderConfig cfg) {
                double h = 2.5 + (cfg.cameraHeightFraction - 0.5) * 3.0;
                double d = 7.0 + (cfg.cameraDistanceFraction - 0.5) * 6.0;
                return new Triplet(0, h, d);
            }
            @Override public Triplet cameraTarget(RenderConfig cfg) {
                return new Triplet(0, 2.5, -2.5);
            }
        };
    }

    // ── Scene 2: Mirror Spheres ──
    private static SceneBlueprint mirrorSpheres() {
        return new SceneBlueprint() {
            @Override public String title() { return "Mirror Spheres"; }
            @Override public void populate(ObjectGroup w, RenderConfig cfg) {
                w.place(new Sheet(new Triplet(0, 0, 0), new Triplet(0, 1, 0),
                    SurfaceProperties.diffuseChecked(new Triplet(0.9, 0.9, 0.9))));
                w.place(new Orb(new Triplet(-2.0, 1.0, -3.0), 1.0,
                    SurfaceProperties.metallic(new Triplet(0.95, 0.95, 0.95), 0.0)));
                w.place(new Orb(new Triplet(0.5, 0.7, -2.0), 0.7,
                    SurfaceProperties.metallic(new Triplet(0.8, 0.7, 0.5), 0.05)));
                w.place(new Orb(new Triplet(2.5, 1.3, -4.0), 1.3,
                    SurfaceProperties.metallic(new Triplet(0.7, 0.8, 0.95), 0.0)));
                w.place(new Orb(new Triplet(-0.5, 0.4, -0.8), 0.4,
                    SurfaceProperties.metallic(new Triplet(0.9, 0.5, 0.5), 0.02)));
            }
            @Override public Triplet cameraOrigin(RenderConfig cfg) {
                double h = 2.0 + (cfg.cameraHeightFraction - 0.5) * 3.0;
                double d = 5.0 + (cfg.cameraDistanceFraction - 0.5) * 5.0;
                return new Triplet(0, h, d);
            }
            @Override public Triplet cameraTarget(RenderConfig cfg) {
                return new Triplet(0, 0.8, -2.5);
            }
        };
    }

    // ── Scene 3: Glass Prism ──
    private static SceneBlueprint glassPrism() {
        return new SceneBlueprint() {
            @Override public String title() { return "Glass Prism"; }
            @Override public void populate(ObjectGroup w, RenderConfig cfg) {
                w.place(new Sheet(new Triplet(0, 0, 0), new Triplet(0, 1, 0),
                    SurfaceProperties.diffuse(new Triplet(0.8, 0.8, 0.85))));
                // Prism approximated as a glass sphere
                w.place(new Orb(new Triplet(0, 1.5, -3), 1.5,
                    SurfaceProperties.glass(1.52)));
                // Colored backdrop spheres to show refraction
                w.place(new Orb(new Triplet(-3, 0.5, -7), 0.5,
                    SurfaceProperties.diffuse(new Triplet(0.9, 0.2, 0.2))));
                w.place(new Orb(new Triplet(-1, 0.5, -7), 0.5,
                    SurfaceProperties.diffuse(new Triplet(0.2, 0.9, 0.2))));
                w.place(new Orb(new Triplet(1, 0.5, -7), 0.5,
                    SurfaceProperties.diffuse(new Triplet(0.2, 0.2, 0.9))));
                w.place(new Orb(new Triplet(3, 0.5, -7), 0.5,
                    SurfaceProperties.diffuse(new Triplet(0.9, 0.9, 0.2))));
            }
            @Override public Triplet cameraOrigin(RenderConfig cfg) {
                return new Triplet(0, 2.0 + (cfg.cameraHeightFraction - 0.5) * 2.0,
                                    4.0 + (cfg.cameraDistanceFraction - 0.5) * 4.0);
            }
            @Override public Triplet cameraTarget(RenderConfig cfg) {
                return new Triplet(0, 1.0, -3);
            }
        };
    }

    // ── Scene 4: Metallic Showcase ──
    private static SceneBlueprint metallicShowcase() {
        return new SceneBlueprint() {
            @Override public String title() { return "Metallic Showcase"; }
            @Override public void populate(ObjectGroup w, RenderConfig cfg) {
                w.place(new Sheet(new Triplet(0, 0, 0), new Triplet(0, 1, 0),
                    SurfaceProperties.diffuse(new Triplet(0.3, 0.3, 0.35))));
                // Row of spheres with increasing fuzz
                for (int i = 0; i < 6; i++) {
                    double fuzzVal = i * 0.2;
                    double xPos = (i - 2.5) * 2.0;
                    w.place(new Orb(new Triplet(xPos, 0.8, -3), 0.8,
                        SurfaceProperties.metallic(new Triplet(0.85, 0.65, 0.3), fuzzVal)));
                }
            }
            @Override public Triplet cameraOrigin(RenderConfig cfg) {
                return new Triplet(0, 3.0 + (cfg.cameraHeightFraction - 0.5) * 2.0,
                                    6.0 + (cfg.cameraDistanceFraction - 0.5) * 4.0);
            }
            @Override public Triplet cameraTarget(RenderConfig cfg) {
                return new Triplet(0, 0.5, -3);
            }
        };
    }

    // ── Scene 5: Checkerboard Floor ──
    private static SceneBlueprint checkerboardFloor() {
        return new SceneBlueprint() {
            @Override public String title() { return "Checkerboard Floor"; }
            @Override public void populate(ObjectGroup w, RenderConfig cfg) {
                w.place(new Sheet(new Triplet(0, 0, 0), new Triplet(0, 1, 0),
                    SurfaceProperties.diffuseChecked(new Triplet(0.95, 0.95, 0.95))));
                w.place(new Orb(new Triplet(0, 1, -3), 1.0,
                    SurfaceProperties.diffuse(new Triplet(0.1, 0.4, 0.8))));
                w.place(new Orb(new Triplet(2.5, 0.6, -2), 0.6,
                    SurfaceProperties.diffuse(new Triplet(0.8, 0.3, 0.1))));
                w.place(new Orb(new Triplet(-2.0, 0.8, -4), 0.8,
                    SurfaceProperties.metallic(new Triplet(0.9, 0.9, 0.9), 0.1)));
            }
            @Override public Triplet cameraOrigin(RenderConfig cfg) {
                return new Triplet(0, 2.5 + (cfg.cameraHeightFraction - 0.5) * 3.0,
                                    5.0 + (cfg.cameraDistanceFraction - 0.5) * 4.0);
            }
            @Override public Triplet cameraTarget(RenderConfig cfg) {
                return new Triplet(0, 0.5, -2);
            }
        };
    }

    // ── Scene 6: Nested Transparent Spheres ──
    private static SceneBlueprint nestedTransparent() {
        return new SceneBlueprint() {
            @Override public String title() { return "Nested Transparent Spheres"; }
            @Override public void populate(ObjectGroup w, RenderConfig cfg) {
                w.place(new Sheet(new Triplet(0, 0, 0), new Triplet(0, 1, 0),
                    SurfaceProperties.diffuseChecked(new Triplet(0.85, 0.85, 0.9))));
                w.place(new Orb(new Triplet(0, 1.5, -3), 1.5, SurfaceProperties.glass(1.5)));
                w.place(new Orb(new Triplet(0, 1.5, -3), 1.0,
                    SurfaceProperties.tintedGlass(new Triplet(0.8, 0.9, 1.0), 1.3)));
                w.place(new Orb(new Triplet(0, 1.5, -3), 0.5,
                    SurfaceProperties.diffuse(new Triplet(0.9, 0.2, 0.2))));
            }
            @Override public Triplet cameraOrigin(RenderConfig cfg) {
                return new Triplet(0, 2.0 + (cfg.cameraHeightFraction - 0.5) * 2.0,
                                    5.0 + (cfg.cameraDistanceFraction - 0.5) * 4.0);
            }
            @Override public Triplet cameraTarget(RenderConfig cfg) {
                return new Triplet(0, 1.5, -3);
            }
        };
    }

    // ── Scene 7: Colored Lights ──
    private static SceneBlueprint coloredLights() {
        return new SceneBlueprint() {
            @Override public String title() { return "Colored Lights"; }
            @Override public Triplet skyTop()    { return new Triplet(0.02, 0.02, 0.05); }
            @Override public Triplet skyBottom() { return new Triplet(0.02, 0.02, 0.05); }
            @Override public void populate(ObjectGroup w, RenderConfig cfg) {
                w.place(new Sheet(new Triplet(0, 0, 0), new Triplet(0, 1, 0),
                    SurfaceProperties.diffuse(new Triplet(0.5, 0.5, 0.5))));
                w.place(new Orb(new Triplet(-3, 3, -2), 0.5,
                    SurfaceProperties.emissive(new Triplet(1.0, 0.2, 0.2), 12.0)));
                w.place(new Orb(new Triplet(0, 3.5, -4), 0.5,
                    SurfaceProperties.emissive(new Triplet(0.2, 1.0, 0.2), 12.0)));
                w.place(new Orb(new Triplet(3, 3, -2), 0.5,
                    SurfaceProperties.emissive(new Triplet(0.2, 0.2, 1.0), 12.0)));
                w.place(new Orb(new Triplet(0, 0.8, -3), 0.8,
                    SurfaceProperties.diffuse(new Triplet(0.9, 0.9, 0.9))));
                w.place(new Orb(new Triplet(-1.5, 0.5, -1.5), 0.5,
                    SurfaceProperties.metallic(new Triplet(0.9, 0.9, 0.9), 0.05)));
            }
            @Override public Triplet cameraOrigin(RenderConfig cfg) {
                return new Triplet(0, 2.5 + (cfg.cameraHeightFraction - 0.5) * 2.0,
                                    6.0 + (cfg.cameraDistanceFraction - 0.5) * 4.0);
            }
            @Override public Triplet cameraTarget(RenderConfig cfg) {
                return new Triplet(0, 0.8, -2.5);
            }
        };
    }

    // ── Scene 8: Shadow Gallery ──
    private static SceneBlueprint shadowGallery() {
        return new SceneBlueprint() {
            @Override public String title() { return "Shadow Gallery"; }
            @Override public void populate(ObjectGroup w, RenderConfig cfg) {
                w.place(new Sheet(new Triplet(0, 0, 0), new Triplet(0, 1, 0),
                    SurfaceProperties.diffuse(new Triplet(0.9, 0.85, 0.8))));
                // Pillars casting shadows
                for (int i = 0; i < 5; i++) {
                    double xp = (i - 2) * 2.5;
                    w.place(new Pillar(new Triplet(xp, 0, -4), 0.3, 3.0,
                        SurfaceProperties.diffuse(new Triplet(0.7, 0.5, 0.3))));
                }
                w.place(new Orb(new Triplet(0, 0.6, -1.5), 0.6,
                    SurfaceProperties.metallic(new Triplet(0.85, 0.85, 0.9), 0.0)));
            }
            @Override public Triplet cameraOrigin(RenderConfig cfg) {
                return new Triplet(0, 2.0 + (cfg.cameraHeightFraction - 0.5) * 3.0,
                                    6.0 + (cfg.cameraDistanceFraction - 0.5) * 4.0);
            }
            @Override public Triplet cameraTarget(RenderConfig cfg) {
                return new Triplet(0, 0.5, -3);
            }
        };
    }

    // ── Scene 9: Fog Depth ──
    private static SceneBlueprint fogDepthScene() {
        return new SceneBlueprint() {
            @Override public String title() { return "Fog Depth"; }
            @Override public Triplet skyTop()    { return new Triplet(0.6, 0.65, 0.7); }
            @Override public Triplet skyBottom() { return new Triplet(0.7, 0.72, 0.75); }
            @Override public void populate(ObjectGroup w, RenderConfig cfg) {
                w.place(new Sheet(new Triplet(0, 0, 0), new Triplet(0, 1, 0),
                    SurfaceProperties.diffuse(new Triplet(0.5, 0.55, 0.5))));
                for (int d = 0; d < 8; d++) {
                    double zPos = -2.0 - d * 3.0;
                    double rad = 0.5 + d * 0.15;
                    double bright = Math.max(0.2, 1.0 - d * 0.1);
                    w.place(new Orb(new Triplet(Math.sin(d * 1.3) * 2.0, rad, zPos), rad,
                        SurfaceProperties.diffuse(new Triplet(bright * 0.8, bright * 0.3, bright * 0.3))));
                }
            }
            @Override public Triplet cameraOrigin(RenderConfig cfg) {
                return new Triplet(0, 2.0 + (cfg.cameraHeightFraction - 0.5) * 2.0,
                                    5.0 + (cfg.cameraDistanceFraction - 0.5) * 4.0);
            }
            @Override public Triplet cameraTarget(RenderConfig cfg) {
                return new Triplet(0, 1.0, -10);
            }
        };
    }

    // ── Scene 10: Cylinder Array ──
    private static SceneBlueprint cylinderArray() {
        return new SceneBlueprint() {
            @Override public String title() { return "Cylinder Array"; }
            @Override public void populate(ObjectGroup w, RenderConfig cfg) {
                w.place(new Sheet(new Triplet(0, 0, 0), new Triplet(0, 1, 0),
                    SurfaceProperties.diffuseChecked(new Triplet(0.8, 0.8, 0.8))));
                Triplet[] hues = {
                    new Triplet(0.8, 0.2, 0.2), new Triplet(0.2, 0.8, 0.2),
                    new Triplet(0.2, 0.2, 0.8), new Triplet(0.8, 0.8, 0.2),
                    new Triplet(0.8, 0.2, 0.8), new Triplet(0.2, 0.8, 0.8)
                };
                for (int row = 0; row < 2; row++) {
                    for (int col = 0; col < 3; col++) {
                        int idx = row * 3 + col;
                        double xp = (col - 1) * 2.5;
                        double zp = -2.5 - row * 3.0;
                        double ht = 1.0 + idx * 0.4;
                        w.place(new Pillar(new Triplet(xp, 0, zp), 0.5, ht,
                            SurfaceProperties.diffuse(hues[idx])));
                    }
                }
            }
            @Override public Triplet cameraOrigin(RenderConfig cfg) {
                return new Triplet(0, 3.0 + (cfg.cameraHeightFraction - 0.5) * 3.0,
                                    7.0 + (cfg.cameraDistanceFraction - 0.5) * 4.0);
            }
            @Override public Triplet cameraTarget(RenderConfig cfg) {
                return new Triplet(0, 1.0, -4);
            }
        };
    }

    // ── Scene 11: Triangle Mesh ──
    private static SceneBlueprint triangleMesh() {
        return new SceneBlueprint() {
            @Override public String title() { return "Triangle Mesh"; }
            @Override public void populate(ObjectGroup w, RenderConfig cfg) {
                w.place(new Sheet(new Triplet(0, 0, 0), new Triplet(0, 1, 0),
                    SurfaceProperties.diffuse(new Triplet(0.6, 0.6, 0.65))));
                // A simple pyramid from triangles
                Triplet apex = new Triplet(0, 3, -4);
                Triplet bFL  = new Triplet(-1.5, 0, -2.5);
                Triplet bFR  = new Triplet(1.5, 0, -2.5);
                Triplet bBL  = new Triplet(-1.5, 0, -5.5);
                Triplet bBR  = new Triplet(1.5, 0, -5.5);
                SurfaceProperties triMat = SurfaceProperties.diffuse(new Triplet(0.85, 0.65, 0.2));
                w.place(new Tri(bFL, bFR, apex, triMat));
                w.place(new Tri(bFR, bBR, apex, triMat));
                w.place(new Tri(bBR, bBL, apex, triMat));
                w.place(new Tri(bBL, bFL, apex, triMat));
                // base
                SurfaceProperties baseMat = SurfaceProperties.diffuse(new Triplet(0.6, 0.45, 0.15));
                w.place(new Tri(bFL, bFR, bBR, baseMat));
                w.place(new Tri(bFL, bBR, bBL, baseMat));

                w.place(new Orb(new Triplet(3, 0.6, -3), 0.6,
                    SurfaceProperties.metallic(new Triplet(0.9, 0.9, 0.9), 0.0)));
            }
            @Override public Triplet cameraOrigin(RenderConfig cfg) {
                return new Triplet(1, 2.5 + (cfg.cameraHeightFraction - 0.5) * 3.0,
                                    5.0 + (cfg.cameraDistanceFraction - 0.5) * 4.0);
            }
            @Override public Triplet cameraTarget(RenderConfig cfg) {
                return new Triplet(0, 1.0, -4);
            }
        };
    }

    // ── Scene 12: Plane Intersection ──
    private static SceneBlueprint planeIntersection() {
        return new SceneBlueprint() {
            @Override public String title() { return "Plane Intersection"; }
            @Override public void populate(ObjectGroup w, RenderConfig cfg) {
                w.place(new Sheet(new Triplet(0, 0, 0), new Triplet(0, 1, 0),
                    SurfaceProperties.diffuseChecked(new Triplet(0.75, 0.75, 0.8))));
                // angled plane cutting through
                w.place(new Sheet(new Triplet(0, 1.5, -3), new Triplet(0.3, 0.7, 0.1).unit(),
                    SurfaceProperties.diffuse(new Triplet(0.4, 0.6, 0.9))));
                w.place(new Orb(new Triplet(-1, 0.8, -2.5), 0.8,
                    SurfaceProperties.glass(1.5)));
                w.place(new Orb(new Triplet(1.5, 0.6, -2), 0.6,
                    SurfaceProperties.diffuse(new Triplet(0.9, 0.4, 0.1))));
            }
            @Override public Triplet cameraOrigin(RenderConfig cfg) {
                return new Triplet(0, 2.5 + (cfg.cameraHeightFraction - 0.5) * 2.0,
                                    5.0 + (cfg.cameraDistanceFraction - 0.5) * 4.0);
            }
            @Override public Triplet cameraTarget(RenderConfig cfg) {
                return new Triplet(0, 1.0, -3);
            }
        };
    }

    // ── Scene 13: Glossy Table ──
    private static SceneBlueprint glossyTable() {
        return new SceneBlueprint() {
            @Override public String title() { return "Glossy Table"; }
            @Override public void populate(ObjectGroup w, RenderConfig cfg) {
                // Floor
                w.place(new Sheet(new Triplet(0, 0, 0), new Triplet(0, 1, 0),
                    SurfaceProperties.glossy(new Triplet(0.35, 0.2, 0.15), 0.15)));
                // Tabletop (slab)
                w.place(new Slab(new Triplet(-3, 1.0, -5), new Triplet(3, 1.15, -1.5),
                    SurfaceProperties.glossy(new Triplet(0.45, 0.25, 0.1), 0.1)));
                // Objects on table
                w.place(new Orb(new Triplet(-1.2, 1.55, -3.5), 0.4,
                    SurfaceProperties.glass(1.5)));
                w.place(new Orb(new Triplet(0.8, 1.55, -3.0), 0.4,
                    SurfaceProperties.metallic(new Triplet(0.9, 0.8, 0.3), 0.02)));
                w.place(new Pillar(new Triplet(0, 1.15, -2.5), 0.15, 0.6,
                    SurfaceProperties.diffuse(new Triplet(0.2, 0.6, 0.3))));
            }
            @Override public Triplet cameraOrigin(RenderConfig cfg) {
                return new Triplet(0, 2.5 + (cfg.cameraHeightFraction - 0.5) * 2.0,
                                    4.5 + (cfg.cameraDistanceFraction - 0.5) * 4.0);
            }
            @Override public Triplet cameraTarget(RenderConfig cfg) {
                return new Triplet(0, 1.2, -3);
            }
        };
    }

    // ── Scene 14: Disco Ball ──
    private static SceneBlueprint discoBall() {
        return new SceneBlueprint() {
            @Override public String title() { return "Disco Ball"; }
            @Override public Triplet skyTop()    { return new Triplet(0.05, 0.02, 0.1); }
            @Override public Triplet skyBottom() { return new Triplet(0.05, 0.02, 0.1); }
            @Override public void populate(ObjectGroup w, RenderConfig cfg) {
                w.place(new Sheet(new Triplet(0, 0, 0), new Triplet(0, 1, 0),
                    SurfaceProperties.glossy(new Triplet(0.2, 0.2, 0.25), 0.3)));
                // Large mirror ball
                w.place(new Orb(new Triplet(0, 3.0, -4), 1.5,
                    SurfaceProperties.metallic(new Triplet(0.95, 0.95, 0.98), 0.02)));
                // Colored emissive lights around it
                double angleStep = Math.PI * 2.0 / 6.0;
                Triplet[] litColors = {
                    new Triplet(1, 0.2, 0.2), new Triplet(0.2, 1, 0.2), new Triplet(0.2, 0.2, 1),
                    new Triplet(1, 1, 0.2), new Triplet(1, 0.2, 1), new Triplet(0.2, 1, 1)
                };
                for (int li = 0; li < 6; li++) {
                    double ang = angleStep * li;
                    double lx = Math.cos(ang) * 4.0;
                    double lz = -4.0 + Math.sin(ang) * 4.0;
                    w.place(new Orb(new Triplet(lx, 2.5, lz), 0.3,
                        SurfaceProperties.emissive(litColors[li], 10.0)));
                }
            }
            @Override public Triplet cameraOrigin(RenderConfig cfg) {
                return new Triplet(0, 3.0 + (cfg.cameraHeightFraction - 0.5) * 2.0,
                                    7.0 + (cfg.cameraDistanceFraction - 0.5) * 4.0);
            }
            @Override public Triplet cameraTarget(RenderConfig cfg) {
                return new Triplet(0, 2.5, -4);
            }
        };
    }

    // ── Scene 15: Underwater Caustics ──
    private static SceneBlueprint underwaterCaustics() {
        return new SceneBlueprint() {
            @Override public String title() { return "Underwater Caustics"; }
            @Override public Triplet skyTop()    { return new Triplet(0.0, 0.15, 0.35); }
            @Override public Triplet skyBottom() { return new Triplet(0.0, 0.25, 0.45); }
            @Override public void populate(ObjectGroup w, RenderConfig cfg) {
                w.place(new Sheet(new Triplet(0, 0, 0), new Triplet(0, 1, 0),
                    SurfaceProperties.diffuse(new Triplet(0.6, 0.55, 0.4))));
                // Water surface (glass plane approximation)
                w.place(new Sheet(new Triplet(0, 3, 0), new Triplet(0, -1, 0),
                    SurfaceProperties.tintedGlass(new Triplet(0.7, 0.85, 0.95), 1.33)));
                // Underwater objects
                w.place(new Orb(new Triplet(-1.5, 0.7, -3), 0.7,
                    SurfaceProperties.diffuse(new Triplet(0.1, 0.5, 0.3))));
                w.place(new Orb(new Triplet(1.0, 0.5, -2), 0.5,
                    SurfaceProperties.diffuse(new Triplet(0.8, 0.4, 0.1))));
                w.place(new Pillar(new Triplet(0, 0, -4), 0.3, 2.5,
                    SurfaceProperties.diffuse(new Triplet(0.3, 0.6, 0.2))));
            }
            @Override public Triplet cameraOrigin(RenderConfig cfg) {
                return new Triplet(0, 1.5 + (cfg.cameraHeightFraction - 0.5) * 2.0,
                                    5.0 + (cfg.cameraDistanceFraction - 0.5) * 4.0);
            }
            @Override public Triplet cameraTarget(RenderConfig cfg) {
                return new Triplet(0, 0.5, -3);
            }
        };
    }

    // ── Scene 16: Sunset Gradient ──
    private static SceneBlueprint sunsetGradient() {
        return new SceneBlueprint() {
            @Override public String title() { return "Sunset Gradient"; }
            @Override public Triplet skyTop()    { return new Triplet(0.15, 0.05, 0.35); }
            @Override public Triplet skyBottom() { return new Triplet(0.95, 0.55, 0.15); }
            @Override public void populate(ObjectGroup w, RenderConfig cfg) {
                w.place(new Sheet(new Triplet(0, 0, 0), new Triplet(0, 1, 0),
                    SurfaceProperties.glossy(new Triplet(0.15, 0.15, 0.2), 0.2)));
                // Sun sphere on horizon
                w.place(new Orb(new Triplet(0, 1.5, -30), 8.0,
                    SurfaceProperties.emissive(new Triplet(1.0, 0.7, 0.2), 3.0)));
                // Silhouette objects
                w.place(new Pillar(new Triplet(-3, 0, -6), 0.2, 4.0,
                    SurfaceProperties.diffuse(new Triplet(0.05, 0.05, 0.05))));
                w.place(new Pillar(new Triplet(-2, 0, -6), 0.15, 3.5,
                    SurfaceProperties.diffuse(new Triplet(0.05, 0.05, 0.05))));
                w.place(new Orb(new Triplet(2, 0.8, -4), 0.8,
                    SurfaceProperties.metallic(new Triplet(0.7, 0.5, 0.3), 0.05)));
            }
            @Override public Triplet cameraOrigin(RenderConfig cfg) {
                return new Triplet(0, 1.5 + (cfg.cameraHeightFraction - 0.5) * 2.0,
                                    5.0 + (cfg.cameraDistanceFraction - 0.5) * 4.0);
            }
            @Override public Triplet cameraTarget(RenderConfig cfg) {
                return new Triplet(0, 1.5, -10);
            }
        };
    }

    // ── Scene 17: Infinite Mirrors ──
    private static SceneBlueprint infiniteMirrors() {
        return new SceneBlueprint() {
            @Override public String title() { return "Infinite Mirrors"; }
            @Override public Triplet skyTop()    { return new Triplet(0.02, 0.02, 0.05); }
            @Override public Triplet skyBottom() { return new Triplet(0.02, 0.02, 0.05); }
            @Override public void populate(ObjectGroup w, RenderConfig cfg) {
                // Two parallel mirror walls
                w.place(new Sheet(new Triplet(-3, 0, 0), new Triplet(1, 0, 0),
                    SurfaceProperties.metallic(new Triplet(0.92, 0.92, 0.95), 0.0)));
                w.place(new Sheet(new Triplet(3, 0, 0), new Triplet(-1, 0, 0),
                    SurfaceProperties.metallic(new Triplet(0.92, 0.92, 0.95), 0.0)));
                w.place(new Sheet(new Triplet(0, 0, 0), new Triplet(0, 1, 0),
                    SurfaceProperties.diffuse(new Triplet(0.3, 0.3, 0.35))));
                // Emissive orb between mirrors
                w.place(new Orb(new Triplet(0, 1.5, -3), 0.5,
                    SurfaceProperties.emissive(new Triplet(1, 0.8, 0.3), 6.0)));
                w.place(new Orb(new Triplet(0, 0.5, -2), 0.5,
                    SurfaceProperties.glass(1.5)));
            }
            @Override public Triplet cameraOrigin(RenderConfig cfg) {
                return new Triplet(0, 2.0 + (cfg.cameraHeightFraction - 0.5) * 2.0,
                                    4.0 + (cfg.cameraDistanceFraction - 0.5) * 4.0);
            }
            @Override public Triplet cameraTarget(RenderConfig cfg) {
                return new Triplet(0, 1.0, -3);
            }
        };
    }

    // ── Scene 18: Diamond Refraction ──
    private static SceneBlueprint diamondRefraction() {
        return new SceneBlueprint() {
            @Override public String title() { return "Diamond Refraction"; }
            @Override public void populate(ObjectGroup w, RenderConfig cfg) {
                w.place(new Sheet(new Triplet(0, 0, 0), new Triplet(0, 1, 0),
                    SurfaceProperties.diffuseChecked(new Triplet(0.9, 0.9, 0.92))));
                // Diamond-like sphere with high IOR
                w.place(new Orb(new Triplet(0, 1.2, -3), 1.2,
                    SurfaceProperties.glass(2.42)));
                // Background colored objects to refract
                w.place(new Slab(new Triplet(-3, 0, -8), new Triplet(-1, 4, -7.5),
                    SurfaceProperties.diffuse(new Triplet(0.9, 0.1, 0.1))));
                w.place(new Slab(new Triplet(-0.5, 0, -8), new Triplet(0.5, 4, -7.5),
                    SurfaceProperties.diffuse(new Triplet(0.1, 0.9, 0.1))));
                w.place(new Slab(new Triplet(1, 0, -8), new Triplet(3, 4, -7.5),
                    SurfaceProperties.diffuse(new Triplet(0.1, 0.1, 0.9))));
            }
            @Override public Triplet cameraOrigin(RenderConfig cfg) {
                return new Triplet(0, 2.0 + (cfg.cameraHeightFraction - 0.5) * 2.0,
                                    5.0 + (cfg.cameraDistanceFraction - 0.5) * 4.0);
            }
            @Override public Triplet cameraTarget(RenderConfig cfg) {
                return new Triplet(0, 1.0, -3);
            }
        };
    }

    // ── Scene 19: Spot Light Demo ──
    private static SceneBlueprint spotLightDemo() {
        return new SceneBlueprint() {
            @Override public String title() { return "Spot Light Demo"; }
            @Override public Triplet skyTop()    { return new Triplet(0.01, 0.01, 0.02); }
            @Override public Triplet skyBottom() { return new Triplet(0.01, 0.01, 0.02); }
            @Override public void populate(ObjectGroup w, RenderConfig cfg) {
                w.place(new Sheet(new Triplet(0, 0, 0), new Triplet(0, 1, 0),
                    SurfaceProperties.diffuse(new Triplet(0.4, 0.4, 0.45))));
                // Spot light approximation: small bright emissive disk pointing down
                w.place(new Disk(new Triplet(0, 5, -3), new Triplet(0, -1, 0), 0.5,
                    SurfaceProperties.emissive(new Triplet(1.0, 0.95, 0.85), 20.0)));
                // Objects under the spotlight
                w.place(new Orb(new Triplet(0, 0.7, -3), 0.7,
                    SurfaceProperties.diffuse(new Triplet(0.8, 0.25, 0.25))));
                w.place(new Slab(new Triplet(-2, 0, -4.5), new Triplet(-0.5, 2, -3.5),
                    SurfaceProperties.diffuse(new Triplet(0.25, 0.25, 0.7))));
                w.place(new Orb(new Triplet(1.8, 0.5, -2), 0.5,
                    SurfaceProperties.glossy(new Triplet(0.7, 0.7, 0.2), 0.2)));
            }
            @Override public Triplet cameraOrigin(RenderConfig cfg) {
                return new Triplet(0, 2.5 + (cfg.cameraHeightFraction - 0.5) * 2.0,
                                    6.0 + (cfg.cameraDistanceFraction - 0.5) * 4.0);
            }
            @Override public Triplet cameraTarget(RenderConfig cfg) {
                return new Triplet(0, 0.8, -3);
            }
        };
    }

    // ── Scene 20: Mixed Materials ──
    private static SceneBlueprint mixedMaterials() {
        return new SceneBlueprint() {
            @Override public String title() { return "Mixed Materials"; }
            @Override public void populate(ObjectGroup w, RenderConfig cfg) {
                w.place(new Sheet(new Triplet(0, 0, 0), new Triplet(0, 1, 0),
                    SurfaceProperties.diffuseChecked(new Triplet(0.85, 0.85, 0.88))));
                // Diffuse
                w.place(new Orb(new Triplet(-4, 0.8, -3), 0.8,
                    SurfaceProperties.diffuse(new Triplet(0.8, 0.3, 0.3))));
                // Metallic
                w.place(new Orb(new Triplet(-2, 0.8, -3), 0.8,
                    SurfaceProperties.metallic(new Triplet(0.9, 0.85, 0.4), 0.0)));
                // Glossy
                w.place(new Orb(new Triplet(0, 0.8, -3), 0.8,
                    SurfaceProperties.glossy(new Triplet(0.3, 0.7, 0.3), 0.25)));
                // Glass
                w.place(new Orb(new Triplet(2, 0.8, -3), 0.8,
                    SurfaceProperties.glass(1.5)));
                // Emissive
                w.place(new Orb(new Triplet(4, 0.8, -3), 0.8,
                    SurfaceProperties.emissive(new Triplet(0.9, 0.6, 0.1), 5.0)));
                // A cylinder and box for variety
                w.place(new Pillar(new Triplet(-1, 0, -6), 0.4, 2.0,
                    SurfaceProperties.diffuse(new Triplet(0.5, 0.3, 0.7))));
                w.place(new Slab(new Triplet(1, 0, -6.5), new Triplet(2.5, 1.5, -5),
                    SurfaceProperties.metallic(new Triplet(0.6, 0.6, 0.65), 0.1)));
                // A triangle
                w.place(new Tri(
                    new Triplet(3, 0, -6), new Triplet(5, 0, -6), new Triplet(4, 2.5, -6),
                    SurfaceProperties.diffuse(new Triplet(0.2, 0.5, 0.8))));
                // A disk
                w.place(new Disk(new Triplet(0, 4, -5), new Triplet(0, 0, 1), 1.5,
                    SurfaceProperties.emissive(new Triplet(1, 1, 0.9), 4.0)));
            }
            @Override public Triplet cameraOrigin(RenderConfig cfg) {
                return new Triplet(0, 3.0 + (cfg.cameraHeightFraction - 0.5) * 3.0,
                                    8.0 + (cfg.cameraDistanceFraction - 0.5) * 5.0);
            }
            @Override public Triplet cameraTarget(RenderConfig cfg) {
                return new Triplet(0, 1.0, -4);
            }
        };
    }
}
