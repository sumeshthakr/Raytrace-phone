# Raytrace Phone

A real-time path-tracing demo for Android that ships with **20 built-in scenes** and a full set of interactive controls.

## Features

| Category | Details |
|----------|---------|
| **Rendering engine** | Custom recursive path tracer written in pure Java — no native code or OpenGL required |
| **Shapes** | Spheres (`Orb`), infinite planes (`Sheet`), cylinders (`Pillar`), axis-aligned boxes (`Slab`), triangles (`Tri`), disks (`Disk`) |
| **Materials** | Diffuse, Metallic (with configurable roughness), Glass / tinted glass (Snell's law + Fresnel), Glossy, Emissive, Procedural checkerboard |
| **Lighting** | Direct illumination, soft shadows, ambient occlusion, emissive area lights, sky gradient background |
| **Camera** | Perspective with adjustable FOV, depth-of-field (thin lens model), movable height & distance |
| **Anti-aliasing** | Multi-sample jittered AA |
| **Controls** | 8 feature toggles (shadows, reflections, refractions, AO, soft shadows, AA, DoF, textures) and 8 parameter sliders (FOV, bounces, samples, brightness, camera height, camera distance, focal length, aperture) |
| **CI/CD** | GitHub Actions workflow builds a debug APK on every push and uploads it as an artifact |

## Included Scenes

1. Cornell Box Classic
2. Mirror Spheres
3. Glass Prism
4. Metallic Showcase
5. Checkerboard Floor
6. Nested Transparent Spheres
7. Colored Lights
8. Shadow Gallery
9. Fog Depth
10. Cylinder Array
11. Triangle Mesh
12. Plane Intersection
13. Glossy Table
14. Disco Ball
15. Underwater Caustics
16. Sunset Gradient
17. Infinite Mirrors
18. Diamond Refraction
19. Spot Light Demo
20. Mixed Materials

## Building

```bash
./gradlew assembleDebug
```

The resulting APK is at `app/build/outputs/apk/debug/app-debug.apk`.

## Architecture

```
app/src/main/java/com/raytrace/phone/
├── engine/          # Core ray-tracing primitives
│   ├── Triplet      # 3-component vector (position / direction / color)
│   ├── Photon       # A directional ray
│   ├── Collision     # Hit-test result record
│   ├── SurfaceProperties  # Material descriptor
│   ├── Renderable   # Shape interface
│   ├── Orb / Sheet / Pillar / Slab / Tri / Disk  # Concrete shapes
│   ├── ObjectGroup  # Aggregate of renderables
│   ├── Viewport     # Perspective camera with DoF
│   ├── PathEngine   # Recursive path tracer
│   └── RenderConfig # All user-controllable settings
├── scenes/
│   ├── SceneBlueprint   # Abstract scene definition
│   └── SceneCatalog     # Factory for all 20 demo scenes
└── RaytraceMainActivity # Android UI with toggles, sliders, and scene picker
```