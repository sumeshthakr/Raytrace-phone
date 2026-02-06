package com.raytrace.phone.scenes;

import com.raytrace.phone.engine.ObjectGroup;
import com.raytrace.phone.engine.RenderConfig;
import com.raytrace.phone.engine.Triplet;

/** Every demo scene must provide objects, camera placement, and sky color. */
public abstract class SceneBlueprint {
    public abstract String title();
    public abstract void populate(ObjectGroup world, RenderConfig cfg);
    public abstract Triplet cameraOrigin(RenderConfig cfg);
    public abstract Triplet cameraTarget(RenderConfig cfg);
    public Triplet skyTop()    { return new Triplet(0.40, 0.55, 0.90); }
    public Triplet skyBottom() { return new Triplet(0.85, 0.85, 0.95); }
    public Triplet ambientLight() { return new Triplet(0.05, 0.05, 0.05); }
}
