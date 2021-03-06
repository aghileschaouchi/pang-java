package me.foly.si.app;

import me.foly.si.core.IAssetsManager;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PImage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ProcessingAssetsManager implements IAssetsManager<PImage, PFont>, Runnable {
    private PApplet applet;
    private ArrayList<String> sources = new ArrayList<>();

    private HashMap<String, PConstants> cache = new HashMap<>();
    private HashMap<String, int[]> contours = new HashMap<>();

    public ProcessingAssetsManager(GameApplet applet) {
        this.applet = applet;

        Path imagesPath = Paths.get(getClass().getResource("/assets").getPath());
        try {
            Files
                    .walk(imagesPath)
                    .filter(path -> Files.isRegularFile(path))
                    .map(path -> path.getFileName().toString())
                    .map(filename -> (filename.endsWith(".ttf") ? "fnt:" : "img:") + filename)
                    .forEach(src -> sources.add(src));
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Thread(this).start();
    }

    @Override
    public double getPreloadingProgress() {
        return (double) cache.size() / (double) sources.size();
    }

    @Override
    public boolean isPreloadingDone() {
        return getPreloadingProgress() == 1;
    }

    @Override
    public PImage getAsset(String imgSrc) {
        return (PImage) cache.get(imgSrc);
    }

    @Override
    public PFont getFont(String fontSrc) {
        return (PFont) cache.get(fontSrc);
    }

    @Override
    public void run() {
        sources.forEach(src -> {
            String filename = src.substring(4);
            if (src.startsWith("fnt:"))
                cache.put(filename, applet.createFont("assets/" + filename, 20));
            else {
                PImage img = applet.loadImage("assets/images/" + filename);
                cache.put(filename, img);

                img.loadPixels();
                contours.put(filename, Arrays.stream(img.pixels).map(color -> applet.alpha(color) == 0 ? 0 : 1).toArray());
            }

            // simulate io latency (TODO: remove)
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public int[] getContour(String imgSrc) {
        return contours.get(imgSrc);
    }
}