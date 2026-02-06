package com.raytrace.phone;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.raytrace.phone.engine.ObjectGroup;
import com.raytrace.phone.engine.PathEngine;
import com.raytrace.phone.engine.RenderConfig;
import com.raytrace.phone.engine.Triplet;
import com.raytrace.phone.engine.Viewport;
import com.raytrace.phone.scenes.SceneBlueprint;
import com.raytrace.phone.scenes.SceneCatalog;

import java.util.ArrayList;
import java.util.List;

/**
 * Main entry point for the Raytrace Phone app.
 * Provides a scene selector, feature toggles, parameter sliders,
 * and triggers rendering on a background thread.
 */
public class RaytraceMainActivity extends AppCompatActivity {

    private static final int RENDER_WIDTH = 240;
    private static final int RENDER_HEIGHT = 180;

    private ImageView renderCanvas;
    private TextView statusLine;
    private ProgressBar renderProgress;
    private Spinner sceneSelector;
    private Button startRenderBtn;

    private SwitchMaterial switchShadows, switchReflect, switchRefract;
    private SwitchMaterial switchAO, switchSoftShadow, switchAA, switchDoF, switchTexturing;

    private SeekBar sliderFov, sliderBounce, sliderSamples, sliderBright;
    private SeekBar sliderCamH, sliderCamD, sliderFocal, sliderAperture;
    private TextView labelFov, labelBounce, labelSamples, labelBright;
    private TextView labelCamH, labelCamD, labelFocal, labelAperture;

    private final RenderConfig renderCfg = new RenderConfig();
    private List<SceneBlueprint> allScenes;
    private volatile boolean isRendering = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_canvas);

        allScenes = SceneCatalog.allScenes();
        bindViews();
        setupSceneSpinner();
        setupToggles();
        setupSliders();

        startRenderBtn.setOnClickListener(v -> launchRender());
    }

    private void bindViews() {
        renderCanvas = findViewById(R.id.renderCanvas);
        statusLine = findViewById(R.id.statusLine);
        renderProgress = findViewById(R.id.renderProgress);
        sceneSelector = findViewById(R.id.sceneSelector);
        startRenderBtn = findViewById(R.id.startRenderBtn);

        switchShadows = findViewById(R.id.switchShadows);
        switchReflect = findViewById(R.id.switchReflect);
        switchRefract = findViewById(R.id.switchRefract);
        switchAO = findViewById(R.id.switchAO);
        switchSoftShadow = findViewById(R.id.switchSoftShadow);
        switchAA = findViewById(R.id.switchAA);
        switchDoF = findViewById(R.id.switchDoF);
        switchTexturing = findViewById(R.id.switchTexturing);

        sliderFov = findViewById(R.id.sliderFov);
        sliderBounce = findViewById(R.id.sliderBounce);
        sliderSamples = findViewById(R.id.sliderSamples);
        sliderBright = findViewById(R.id.sliderBright);
        sliderCamH = findViewById(R.id.sliderCamH);
        sliderCamD = findViewById(R.id.sliderCamD);
        sliderFocal = findViewById(R.id.sliderFocal);
        sliderAperture = findViewById(R.id.sliderAperture);

        labelFov = findViewById(R.id.labelFov);
        labelBounce = findViewById(R.id.labelBounce);
        labelSamples = findViewById(R.id.labelSamples);
        labelBright = findViewById(R.id.labelBright);
        labelCamH = findViewById(R.id.labelCamH);
        labelCamD = findViewById(R.id.labelCamD);
        labelFocal = findViewById(R.id.labelFocal);
        labelAperture = findViewById(R.id.labelAperture);
    }

    private void setupSceneSpinner() {
        List<String> titles = new ArrayList<>();
        for (SceneBlueprint bp : allScenes) {
            titles.add(bp.title());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_dropdown_item, titles);
        sceneSelector.setAdapter(adapter);
    }

    private void setupToggles() {
        switchShadows.setOnCheckedChangeListener((v, on) -> renderCfg.enableShadows = on);
        switchReflect.setOnCheckedChangeListener((v, on) -> renderCfg.enableReflections = on);
        switchRefract.setOnCheckedChangeListener((v, on) -> renderCfg.enableRefractions = on);
        switchAO.setOnCheckedChangeListener((v, on) -> renderCfg.enableAO = on);
        switchSoftShadow.setOnCheckedChangeListener((v, on) -> renderCfg.enableSoftShadows = on);
        switchAA.setOnCheckedChangeListener((v, on) -> renderCfg.enableAA = on);
        switchDoF.setOnCheckedChangeListener((v, on) -> renderCfg.enableDoF = on);
        switchTexturing.setOnCheckedChangeListener((v, on) -> renderCfg.enableTextures = on);
    }

    private void setupSliders() {
        wireSlider(sliderFov, labelFov, "FOV", val -> renderCfg.fieldOfView = val);
        wireSlider(sliderBounce, labelBounce, "Bounces", val -> renderCfg.maxBounces = (int) val);
        wireSlider(sliderSamples, labelSamples, "Samples", val -> renderCfg.samplesPerPixel = (int) val);
        wireSlider(sliderBright, labelBright, "Brightness", val -> renderCfg.brightnessMultiplier = val / 100.0);
        wireSlider(sliderCamH, labelCamH, "Cam Height", val -> renderCfg.cameraHeightFraction = val / 100.0);
        wireSlider(sliderCamD, labelCamD, "Cam Distance", val -> renderCfg.cameraDistanceFraction = val / 100.0);
        wireSlider(sliderFocal, labelFocal, "Focal Len", val -> renderCfg.focalLengthFraction = val / 100.0);
        wireSlider(sliderAperture, labelAperture, "Aperture", val -> renderCfg.apertureFraction = val / 100.0);
    }

    private void wireSlider(SeekBar slider, TextView label, String name, SliderAction action) {
        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                label.setText(name + ": " + progress);
                action.apply(progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private interface SliderAction {
        void apply(double value);
    }

    private void launchRender() {
        if (isRendering) return;
        isRendering = true;
        startRenderBtn.setEnabled(false);
        renderProgress.setVisibility(android.view.View.VISIBLE);
        renderProgress.setMax(100);
        renderProgress.setProgress(0);
        statusLine.setText("Rendering...");

        int sceneIdx = sceneSelector.getSelectedItemPosition();
        if (sceneIdx < 0 || sceneIdx >= allScenes.size()) sceneIdx = 0;
        SceneBlueprint chosenScene = allScenes.get(sceneIdx);

        // Snapshot config values for the background thread
        final RenderConfig snapCfg = new RenderConfig();
        snapCfg.enableShadows = renderCfg.enableShadows;
        snapCfg.enableReflections = renderCfg.enableReflections;
        snapCfg.enableRefractions = renderCfg.enableRefractions;
        snapCfg.enableAO = renderCfg.enableAO;
        snapCfg.enableSoftShadows = renderCfg.enableSoftShadows;
        snapCfg.enableAA = renderCfg.enableAA;
        snapCfg.enableDoF = renderCfg.enableDoF;
        snapCfg.enableTextures = renderCfg.enableTextures;
        snapCfg.fieldOfView = renderCfg.fieldOfView;
        snapCfg.maxBounces = renderCfg.maxBounces;
        snapCfg.samplesPerPixel = renderCfg.samplesPerPixel;
        snapCfg.brightnessMultiplier = renderCfg.brightnessMultiplier;
        snapCfg.cameraHeightFraction = renderCfg.cameraHeightFraction;
        snapCfg.cameraDistanceFraction = renderCfg.cameraDistanceFraction;
        snapCfg.focalLengthFraction = renderCfg.focalLengthFraction;
        snapCfg.apertureFraction = renderCfg.apertureFraction;

        final SceneBlueprint sceneRef = chosenScene;
        new Thread(() -> {
            long t0 = System.currentTimeMillis();
            ObjectGroup world = new ObjectGroup();
            sceneRef.populate(world, snapCfg);

            Triplet camOrigin = sceneRef.cameraOrigin(snapCfg);
            Triplet camTarget = sceneRef.cameraTarget(snapCfg);
            double aspect = (double) RENDER_WIDTH / RENDER_HEIGHT;
            double aperture = snapCfg.enableDoF ? snapCfg.apertureFraction * 0.5 : 0.0;
            double focusDist = snapCfg.focalLengthFraction * 10.0 + 1.0;

            Viewport cam = new Viewport(camOrigin, camTarget, new Triplet(0, 1, 0),
                snapCfg.fieldOfView, aspect, aperture, focusDist);

            PathEngine tracer = new PathEngine(world, snapCfg);
            Bitmap result = tracer.renderFrame(RENDER_WIDTH, RENDER_HEIGHT, cam,
                sceneRef.skyTop(), sceneRef.skyBottom(), sceneRef.ambientLight(),
                pct -> runOnUiThread(() -> renderProgress.setProgress(pct)));

            long elapsed = System.currentTimeMillis() - t0;
            runOnUiThread(() -> {
                renderCanvas.setImageBitmap(result);
                statusLine.setText("Rendered in " + elapsed + "ms");
                renderProgress.setVisibility(android.view.View.GONE);
                startRenderBtn.setEnabled(true);
                isRendering = false;
            });
        }).start();
    }
}
