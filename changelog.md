# Changelog
All notable changes to this project will be documented in this file. The format of this file follows that specified by [Keep a Changelog](https://keepachangelog.com/en/1.0.0/). This project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).



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

[1.2.0]: https://github.com/theskidster/XJGE/commit/17331b216f6002e1ab2e348be722d4812cb756bc
[1.0.9]: https://github.com/theskidster/XJGE/commit/90211a1e8cdc884334377beaf80c7fbf1671965a
[1.0.5]: https://github.com/theskidster/XJGE/commit/0f82e35a60f807098d05be3c5e02283ff424e0f3
[1.0.4]: https://github.com/theskidster/XJGE/commit/4bb6e591d3b40f52da837ec27a66d92f4c6e1dbf
[1.0.0]: https://github.com/theskidster/XJGE/commit/67b3a12dab536e1db056f03fba46988b25752591
