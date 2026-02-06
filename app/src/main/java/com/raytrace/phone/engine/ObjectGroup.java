package com.raytrace.phone.engine;

import java.util.ArrayList;
import java.util.List;

/** Holds multiple Renderable items and finds the nearest collision. */
public final class ObjectGroup implements Renderable {
    private final List<Renderable> items = new ArrayList<>();

    public void place(Renderable item) { items.add(item); }
    public void clear() { items.clear(); }
    public List<Renderable> getItems() { return items; }

    @Override
    public Collision testHit(Photon ray, double tMin, double tMax) {
        Collision nearest = null;
        double closest = tMax;
        for (Renderable item : items) {
            Collision c = item.testHit(ray, tMin, closest);
            if (c != null) {
                nearest = c;
                closest = c.dist;
            }
        }
        return nearest;
    }
}
