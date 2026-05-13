# Architecture Overview

## Project

PhotoEditor is a JavaFX-based desktop image editor created as a learning and portfolio project.

The main goal of the project is not only image editing itself, but also understanding:

- JavaFX application structure
- MVC architecture
- Real-time image processing
- Color space transformations
- Immutable data pipelines
- Graphics programming fundamentals

---

# High-Level Architecture

The application follows a layered structure:

UI (FXML + Controllers)
        ↓
Processing Pipeline
        ↓
Color Conversion Layer
        ↓
Immutable Color Models

---

# Main Components

## Main

Responsible for:

- JavaFX application startup
- Stage initialization
- Scene switching
- Initial controller wiring

Main acts as the application entry point and navigation coordinator.

---

## BrowserController

Responsible for:

- File browsing
- Image selection
- Transition from browser scene to editor scene

After successful image selection:

1. Image is loaded
2. `showEditor(...)` is called
3. Editor scene is initialized
4. Image is passed into `EditorController`

BrowserController should remain lightweight and not contain image processing logic.

---

## EditorController

Responsible for:

- Editor UI interaction
- Slider state management
- Real-time parameter updates
- Communication with ImageProcessor

Current sliders:

- Brightness
- Contrast
- Saturation

Controller responsibilities:

- Read slider values in real time
- Build immutable `FilterParams`
- Trigger processing pipeline

The controller SHOULD NOT contain:

- Per-pixel processing
- Color conversion algorithms
- Heavy computations

---

# Processing Layer

## ImageProcessor

Central image processing pipeline.

Responsible for:

- Per-pixel image traversal
- Reading image pixels
- Applying transformations
- Producing processed output image

Current processing flow:

Image
 → pixel extraction
 → RGB conversion
 → RGB → HSV
 → filter adjustments
 → HSV → RGB
 → write processed pixel

Future plans:

- Preview downscaling
- Multi-threaded processing
- Processing/render separation
- Buffered processing pipeline

---

# Color System

## Goal

The project intentionally avoids using built-in color manipulation utilities in order to understand graphics fundamentals manually.

The application implements custom color models and conversion logic.

---

# Color Models

Located in:

model/color/

---

## RgbColor

Represents standard RGB color.

Range:

R: 0..255
G: 0..255
B: 0..255

Responsibilities:

- Store RGB channels
- Clamp invalid values
- Normalize into `NormalizedRgbColor`

Immutable Java Record.

---

## NormalizedRgbColor

Represents normalized RGB values.

Range:

0..1

Used internally for color conversion math.

Purpose:

- Separate display RGB from mathematical RGB
- Avoid repeated normalization logic

Immutable Java Record.

---

## HsvColor

Represents HSV color model.

Range:

Hue:        0..360
Saturation: 0..1
Value:      0..1

Responsibilities:

- Store HSV channels
- Clamp invalid ranges
- Keep hue normalized

Immutable Java Record.

---

# ColorConverter

Responsible for:

- RGB → HSV conversion
- HSV → RGB conversion

Important design decisions:

- Conversion logic is centralized
- Controllers do not perform color math
- Color models remain immutable
- Clamp operations are explicit

The converter currently uses:

- Sector-based HSV conversion
- Manual chroma calculations
- Manual normalization

This implementation is intentionally educational rather than optimized.

---

# Filter System

## FilterParams

Immutable parameter container passed into processing pipeline.

Current structure:

FilterParams
 └── AdjustColor
       ├── brightness
       ├── contrast
       └── saturation

Reasoning:

- Scalable architecture
- Future filter groups can be added cleanly
- Avoids large parameter lists
- Keeps processing pipeline extensible

Example future expansion:

FilterParams
 ├── AdjustColor
 ├── Blur
 ├── Sharpen
 ├── Curves
 └── NoiseReduction

---

# Design Principles

## 1. Immutable Data

Color objects and filter parameters use Java Records.

Reasoning:

- Safer processing
- Easier debugging
- Predictable transformations
- Functional-style pipeline

---

## 2. Separation of Concerns

Controllers:
- UI only

Processor:
- image processing only

Converter:
- color math only

Models:
- data only

---

## 3. Educational Over Optimization

Current implementation prioritizes:

- readability
- understanding
- explicit math
- architecture clarity

over:

- micro-optimizations
- GPU acceleration
- advanced rendering techniques

---

# Planned Improvements

## Performance

- Preview resolution scaling
- Background processing threads
- Render throttling
- Pixel buffer optimization

---

## Features

- Crop tool
- Rotation
- Undo / redo
- Histogram
- Export pipeline
- Additional filters

---

# Current State

Implemented:

- JavaFX editor window
- Image loading
- Scene switching
- Slider system
- RGB ↔ HSV conversion
- Filter parameter pipeline
- Processing architecture foundation

In Progress:

- Real-time filter application
- Pixel processing pipeline
- Rendering optimization
