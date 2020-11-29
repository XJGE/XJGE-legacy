# Changelog
All notable changes to this project will be documented in this file. The format of this file follows that specified by [Keep a Changelog](https://keepachangelog.com/en/1.0.0/). This project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).



## [1.4.6] - 2020-11-29

### Added

* New Polygon class that can be used to draw 2D shapes as part of a UI.
* 9th uniform type in the default shaders for the previously mentioned polygon object.

### Changed

* Minor changes made to the shader documentation.



## [1.4.1] - 2020-11-01

### Added

* Separate fields used internally to indicate engine and game implementation versions.

### Removed

* Unused import and variable in Window.java



## [1.4.0] - 2020-07-28

### Added

* Basic 3D animation playback utilities including looping and speed manipulation.
* Automatic smooth transitioning between skeletal animations.
* New bipedal "Buster" model to test animation features.

### Changed

- Merged the "img_light.png" and "spr_icons_input.png" files into a single sprite sheet image. Changed the classes which used these files accordingly.
- Entity2DAnimTest class to instead use the controller sprites instead of the loading icon.
- Fixed some broken documentation links, made minor changes to some sections.
- Fixed a bug in the Model class that would erroneously assign textures to the wrong meshes.
- Fixed a bug where keyframes would compound into a single animation during model loading.
- Increased default MAX_BONE size from 32 to 128.
- Fixed the bug that would incorrectly assign vertex weights to the wrong bones.
- Fixed bug that would crash the engine if null was passed as the sound source position in the playSound() method.
- Changes made to permit interpolation between keyframes of skeletal animations.
- Moved animation playback information into the model class, animation state such as speed will now be retained between animations.
- Had to rewrite the KeyFrame and SkeletalAnimation classes since they got too messy.
- The name of the Entity2DAnimTest to "EntitySprite".

### Removed

- The "spr_icons_load.png" sprite sheet image.
- The 3D animation obelisk and all of its accompanying dependencies.



## [1.3.0] - 2020-06-19

### Added

* New Cubemap class that represents three-dimensional textures.
* New Skybox class that enables a greater level of detail in 3D environments.
* Various utilities to the Level class including setSkybox() and renderSkybox() methods to allow it to utilize skyboxes objects.
* New default Skybox objects that correspond to the default Light objects.
* New 3D audio solution that enables individual viewports to experience stereo sound during split screen play.

### Changed

* Fixed the bug that would crash the engine if an audio device outside the number available was specified through the "setAudioDevice" terminal command.
* The documentation for several classes to reflect changes including; Texture.java, Sound.java, Song.java, Entity.java, Level.java, and Sound.java.
* The location of the IDENTITY matrix object from App.java to KeyFrame.java.
* Fixed broken link in freeTexture() method documentation.
* The signature of the render() function in entity/level, it will now just pass the viewports current camera object instead of its vectors.
* Modified default shaders to permit the use of skyboxes.
* The brightness of the default light object MIDNIGHT.
* The ordering of several methods in the AudioService and Audio classes to better resemble the loose conventions imposed by the engine.

### Removed

* The model matrix object from the render method of the Skybox class since it was unused.
* The documentation for the vectors in the Camera class as it was creating too much clutter, updated any documentation links accordingly.



## [1.2.3] - 2020-05-19

### Added

* The ability for model textures to exhibit transparency.

### Changed

* Changes made to the DAYLIGHT object to make it less intense.
* The documentation of the 3D utilities added in the previous update to be more descriptive.

### Removed

* Unused light import in test level.



## [1.2.0] - 2020-04-29

### Added

- Support for 3D model loading with some basic functions to rotate, translate, and scale objects.
- New colors "SILVER", "SLATE_GRAY", and "PERIWINKLE" to the default color palette.
- New "showLightSources" command to terminal that toggles the visibility of light source location icons at runtime.
- Basic lighting utilities provided through the Level class.
- New static addLightSource() method to Game class.
- Support for simple non-interpolated 3D skeletal animation.
- New test entities; EntityTeapot, Entity3DAnimTest and Entity2DAnimTest- these will likely be replaced/modified in the future.
- New SpriteAnimation class that facilitates 2D animation.

### Changed

- Changed the field names in the SpriteSheet class to be more meaningful.
- Minor changes made to the documentation to make it more cohesive.
- Changed the name of the "model" Matrix4f field in the Graphics class to "modelMatrix" so it wont get confused with instances of the Model class.
- Fixed the bug where the engine would crash if the terminal was opened after an entity had been destroyed.
- Logger output with WARNING level severity will now provide stack traces to the console at runtime if an exception has been caught.
- Minor changes made to the Texture parsing code to help pinpoint what part of the parsing process failed.
- The SpriteSheet class now provides a default mapping of its texture coordinates through the imgOffsets field.



## [1.0.9] - 2020-03-09

### Added

* Icon class that enables icon images to be drawn to the UI through use of a sprite sheet.
* Accompanying documentation to new classes/methods.
* Validation to input device getter methods in App.

### Changed

* Fixed broken documentation links.
* Fixed the bug where using either the terminal or free-cam wouldn't disable the keyboard.
* Significant changes made to the output of the showInputInfo command.
* Found and fixed a bug that left input devices disabled even after a disconnection event was resolved.

### Removed

* Model matrix from Viewport.java.



## [1.0.5] - 2020-03-03

### Added

* Branching & Cloning clause to the README file.
* The "dev" web domain prefix to all packages. 

### Changed

- The changelogs file format has been changed from plain text (.txt) to markdown (.md).



## [1.0.4] - 2020-02-13
### Added
- Troubleshooting section to README.
- Test entity object.
- Test level.
- getViewportActive() method in the App class.
- addDisCon() convenience method in the Window class.
- New "beep" command to command terminal.

### Changed
- Controller disconnection messages will instead pop up in viewport 0 if the viewport associated with that controller is inactive.

### Removed
- Automatic termination. The engine will instead enter the test level.
- Lingering @todo statements.



## [1.0.0] - 2020-02-12
### Added
- Simple 3D camera made controllable through the keyboard.
- Extensible command line that can be used to interact with the engine at runtime.
- Support for bitmap font rendering.
- Monotonic timing mechanism made available as a component.
- Customizable audio engine implemented using OpenAL.
- Support for up to four controllers simultaneously.
- Input system that decouples input events from devices used enabling game objects to exhibit predictable behavior regardless of controller model.
- Observable component that enables implementing objects to broadcast state changes to the rest of the engine.
- Component based UI system.
- Internal priority-oriented event system that allows both game and application events to be added with ease.
- Split screen functionality.
- Error state checking for dependencies.
- Graphics layer that allows complete control over the OpenGL graphics pipeline.
- Game state traversal.
- Logger that tracks significant events occurring within the engine and writes the output to a .txt file.

[1.4.0]: https://github.com/theskidster/XJGE/commit/df8cd342589f6578b64dc08d8200e85f312fe45e
[1.3.0]: https://github.com/theskidster/XJGE/commit/753cda066ed1c58c21a31786c020d0cfdb99ead0
[1.2.3]: https://github.com/theskidster/XJGE/commit/805bc8c1ec004f7bd08d22b429cdcdfb5bf7f69c
[1.2.0]: https://github.com/theskidster/XJGE/commit/17331b216f6002e1ab2e348be722d4812cb756bc
[1.0.9]: https://github.com/theskidster/XJGE/commit/90211a1e8cdc884334377beaf80c7fbf1671965a
[1.0.5]: https://github.com/theskidster/XJGE/commit/0f82e35a60f807098d05be3c5e02283ff424e0f3
[1.0.4]: https://github.com/theskidster/XJGE/commit/4bb6e591d3b40f52da837ec27a66d92f4c6e1dbf
[1.0.0]: https://github.com/theskidster/XJGE/commit/67b3a12dab536e1db056f03fba46988b25752591
