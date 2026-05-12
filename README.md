# Photo Editor

A simple desktop photo editor built with Java and JavaFX.
The project is designed as a learning-focused application to practice:
* JavaFX UI development
* MVC architecture
* Image processing
* Color space conversions (RGB ↔ HSV)
* Real-time filter pipelines
* Immutable data modeling with Java Records

## Features

Current functionality:

- Open image from local storage
- Display selected image in editor window
- Real-time sliders for:
- Brightness
- Contrast
- Saturation
- RGB ↔ HSV color conversion pipeline
- Per-pixel image processing architecture

## Planned Features

* Image scaling optimization
* Multithreaded image processing
* Additional filters
* Crop and rotate tools
* Histogram visualization
* Undo / redo system
* Export edited images

## Tech Stack

* Java
* JavaFX
* FXML
* Maven / Gradle (depending on your setup)


## Architecture
The project follows a layered structure:

### Controllers
Responsible for JavaFX UI interaction and user input handling.

### Models
Immutable color representations using Java Records.

### Services
Contain image processing logic and color space conversions.

## Learning Goals

* Object-oriented design
* Functional-style data pipelines
* Graphics programming fundamentals
* Separation of concerns
* Real-time UI interaction

## Current Status

The project is being actively as a portfolio and learning project developed.

## Run Project

### Maven

```bash
mvn clean javafx:run
```
### Or run manually
Run Main class from your IDE

## TODO
