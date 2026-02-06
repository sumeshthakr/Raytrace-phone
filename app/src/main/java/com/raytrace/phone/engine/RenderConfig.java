package com.raytrace.phone.engine;

/** All toggleable and adjustable parameters that the user can control. */
public final class RenderConfig {
    public boolean enableShadows = true;
    public boolean enableReflections = true;
    public boolean enableRefractions = true;
    public boolean enableAO = false;
    public boolean enableSoftShadows = false;
    public boolean enableAA = false;
    public boolean enableDoF = false;
    public boolean enableTextures = true;

    public double fieldOfView = 60.0;
    public int maxBounces = 4;
    public int samplesPerPixel = 1;
    public double brightnessMultiplier = 1.0;
    public double cameraHeightFraction = 0.5;   // 0..1
    public double cameraDistanceFraction = 0.5;  // 0..1
    public double focalLengthFraction = 0.5;     // 0..1
    public double apertureFraction = 0.0;        // 0..1
}
