# XJGE - Extensible Java Game Engine 
XJGE is a cross-platform game engine written in Java that enables new features to be added with ease by allowing users interact directly with its source.

XJGE makes no assumptions about the type of game (or application) its being used to create and- as such, provides a lightweight architecture that primarily concerns itself with managing the state of the application. It is expected that the game implementation define how the engines systems interact within the context of its own requirements.

Instead of boxing you in with its own gameplay elements XJGE provides a structure in which these systems may be attached. Thus, heavyweight game-specific features like collision detection are not present in version 1.0.0 but may be added in the future if more implementation agnostic approaches to such systems can be engineered.

XJGE is (currently) closed source and protected under an exclusive license.

[TOC]

## Getting Started
XJGE requires java 12.0.1 or later and currently supports the following platforms:
- Linux x64
- Windows x64
- Windows x86

## Troubleshooting
The most common issues encountered can be resolved by:
- Including the lib folder in the same directory as .jar distributions.
- Updating Java, GLFW, and/or OpenGL.

## Branching & Cloning
This repository is reserved exclusively to track the development of the engine only. Any game projects using this engine should first clone this repo, then push any subsequent commits to their own seperate repository.

## 1.0.0 Features
- Simple 3D camera made controllable through the keyboard.
- Extensible command line that can be used to interact with the engine at runtime.
- Support for bitmap font rendering.
- Monotonic timing mechanism made available as a component.
- Customizable audio engine implemented using OpenAL.
- Support for up to four controllers simultaneously.
- Input system that decouples input events from devices used enabling game objects to exhibit predictable behavior regardless of controller model.
- Observable component that enables implementing objects to broadcast state changes to the rest of the engine.
- Verified to work on PC and Linux systems.
- Component based UI system.
- Internal priority-oriented event system that allows both game and application events to be added with ease.
- Split screen functionality.
- Error state checking for dependencies.
- Graphics layer that allows complete control over the OpenGL graphics pipeline.
- Game state traversal.
- Logger that tracks significant events occurring within the engine and writes the output to a .txt file.
