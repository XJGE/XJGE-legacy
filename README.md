# XJGE - Extensible Java Game Engine 
XJGE is a cross-platform game engine written in Java that enables new features to be added with ease by allowing users interact directly with its source.

<b>UPDATE: This is an older version of the engine that is no longer supported, please use https://github.com/XJGE/XJGE-2 instead.</b>

XJGE makes no assumptions about the type of game (or application) its being used to create and- as such, provides a lightweight architecture that primarily concerns itself with managing the state of the application. It is expected that the game implementation define how the engines systems interact within the context of its own requirements.

Instead of boxing you in with its own gameplay elements XJGE provides a structure in which these systems may be attached. Thus, heavyweight game-specific features like collision detection are not present in version 1.0.0 but may be added in the future if more implementation agnostic approaches to such systems can be engineered.

XJGE is open source and protected under an GNU Lesser General Public License.

## Getting Started
XJGE requires java 15.0.2 or later and currently supports the following platforms:
- Linux x64
- Windows x64
- Windows x86

## Troubleshooting
The most common issues encountered can be resolved by:
- Including the lib folder in the same directory as .jar distributions.
- Updating Java, GLFW, and/or OpenGL.

## Branching & Cloning
This repository is reserved exclusively to track the development of the engine only. Any game projects using this engine should first clone this repo, change the origin of the clone, then push any subsequent commits to their own seperate repository.
