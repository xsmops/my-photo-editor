package com.example.photoeditor.service;

import javafx.concurrent.Task;
import javafx.scene.image.Image;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Runs image rendering work outside the JavaFX application thread.
 *
 * <p>The service accepts a source image and immutable filter parameters, runs
 * the processing pipeline on a single background executor, and returns the
 * finished image through a callback. The callback is invoked from the JavaFX
 * task success handler, so callers can safely update JavaFX UI controls there.</p>
 *
 * <p>Only the most recent render request is allowed to update the caller. Older
 * requests are cancelled when possible and ignored through a render version
 * check if they finish after a newer request has been submitted.</p>
 */
public class RenderService {
    private long renderVersion = 0;
    private Task<Image> currentRenderTask;

    final private ImageProcessor processor = new ImageProcessor();

    final private ExecutorService renderExecutor =
            Executors.newSingleThreadExecutor();

    /**
     * Starts rendering an image with the provided filter parameters.
     *
     * <p>Submitting a new render request invalidates the previous request. This
     * prevents outdated preview or full-size renders from replacing a newer
     * image in the UI.</p>
     *
     * @param source source image to process
     * @param params immutable filter parameters for this render
     * @param renderedImage callback that receives the processed image
     */
    public void render(Image source,
                        FilterParams params,
                        Consumer<Image> renderedImage) {
        if (currentRenderTask != null) {
            currentRenderTask.cancel();
        }

        renderVersion++;
        long taskVersion = renderVersion;

        Task<Image> task = new Task<>() {
            @Override
            protected Image call() throws Exception {
                return processor.run(source, params);
            }
        };

        // Keep the task so a later render request can cancel queued work.
        currentRenderTask = task;

        task.setOnSucceeded(event -> {
            if (renderVersion != taskVersion) return;

            renderedImage.accept(task.getValue());
        });

        task.setOnFailed(event -> {
            if (renderVersion != taskVersion) return;

            Throwable error = task.getException();
            error.printStackTrace();
        });

        renderExecutor.submit(task);

    }

    /**
     * Stops the background executor owned by this service.
     *
     * <p>This should be called when the editor is no longer needed so the
     * application does not keep an unused render thread alive.</p>
     */
    public void shutdown() {
        renderExecutor.shutdown();
    }


}
