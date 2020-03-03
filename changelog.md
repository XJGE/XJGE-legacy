# Changelog
All notable changes to this project will be documented in this file. The format of this file follows that specified by [Keep a Changelog](https://keepachangelog.com/en/1.0.0/). This project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).



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



[1.0.4]: https://github.com/theskidster/XJGE/commit/4bb6e591d3b40f52da837ec27a66d92f4c6e1dbf
[1.0.0]: https://github.com/theskidster/XJGE/commit/67b3a12dab536e1db056f03fba46988b25752591
