package com.raytrace.phone.engine;

/** Any object that a Photon can collide with. */
public interface Renderable {
    /** Test for intersection in the range [tMin, tMax]. Returns null if no hit. */
    Collision testHit(Photon ray, double tMin, double tMax);
}
