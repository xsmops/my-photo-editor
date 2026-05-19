# Photo Editor

A simple desktop photo editor built with Java and JavaFX.
The project is designed as a learning-focused application to practice:
* JavaFX UI development
* MVC architecture
* Image processing
* Color space conversions (RGB ↔ HSV)
* Real-time filter pipelines
* Immutable data modeling with Java Records
* Multithreaded pixel processing
* Lookup-table based adjustment pipelines

## Features

Current functionality:

- Open image from local storage
- Display selected image in editor window
- Real-time sliders for:
    - Brightness
    - Contrast
    - Saturation
    - Hue
- RGB ↔ HSV color conversion pipeline
- Per-pixel image processing parallelized across CPU cores
- Precomputed lookup tables for saturation, value, and hue adjustments
- Downscaled preview while sliders are being dragged
- Delayed full-size rendering after slider interaction stops
- Background rendering with cancellation of stale render requests

## Planned Features

* Brightness and contrast in RGB space for full black ↔ white and gray ↔ binarization range
* Additional filters
* Crop and rotate tools
* Histogram visualization
* Undo / redo system
* Export edited images

## Tech Stack

* Java
* JavaFX
* FXML
* Maven

## Architecture

The project follows a layered structure:

### Controllers
Responsible for JavaFX UI interaction and user input handling.
`BrowserController` opens the file chooser and forwards the selected image to
the editor. `EditorController` owns slider state, builds filter parameter
snapshots, and decides when preview or full-size rendering should be requested.

### Models
Immutable color representations using Java Records:
`RgbColor` (0..255 channels), `NormalizedRgbColor` (0..1 channels), and
`HsvColor` (hue in degrees, saturation and value in 0..1).

### Services
Contain image processing logic and color space conversions:
* `ColorConverter` — RGB ↔ HSV conversion formulas
* `AdjustmentLut` — precomputed lookup tables for slider adjustments
* `ImageProcessor` — parallel per-pixel processing pipeline
* `PreviewImage` — fast downscaled copy used while sliders are dragged
* `RenderService` — background executor with stale-render cancellation

## Learning Goals

* Object-oriented design
* Functional-style data pipelines
* Graphics programming fundamentals
* Separation of concerns
* Real-time UI interaction
* Background work and thread safety in JavaFX

## Current Status

The project is being actively developed as a portfolio and learning project.

## Run Project

### Maven

```bash
mvn clean javafx:run
```

### Or run manually

Run `Main` class from your IDE.

## TODO
