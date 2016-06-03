# Change Log
All notable changes to this project will be documented in this file.
This project adheres to [Semantic Versioning](http://semver.org/).

## [Unreleased]
- Replaced ActionBar by Toolbar
- Replaced offers ListView by RecyclerView

## [1.2.2] - 2016-02-29
### Changed
- Improved displayed message and behavior on connectivity errors
- Included location permission request to user for Android 6+
- _Center in my location_ button will not be displayed in Map View when location is disabled

## [1.2.1] - 2016-02-22
### Fixed
- Application should not crash on opening map activity when offers are not yet loaded

## [1.2.0] - 2016-02-22
### Added
- Implemented user statistics and device information reporting
- Added 'report offer error' functionality in offer details view
- Added CHANGELOG file to keep track of changes between versions

### Changed
- `largeHeap` option is now enabled by default in application manifest to avoid reaching max heap space when loading offer images. This change should avoid _OutOfMemory_ errors in devices with limited memory.


## [1.1.0] - 2016-02-8
### Added
- Included font typeface customization

### Changed
- Icon customization instructions should now be cleaner
- General improvements in UI customization:
  - ActionBar
  - Search Box
  - Action and Search Filter buttons

## [1.0.1] - 2016-01-27
### Changed
- Removed unnecessary Play Services dependencies to reduce method count

## 1.0.0 - 2016-01-20
### Added
- First fully functional public release.

[Unreleased]: https://github.com/Wakup/Wakup-Android-SDK/compare/v1.2.2...HEAD
[1.2.2]: https://github.com/Wakup/Wakup-Android-SDK/compare/v1.2.1...v1.2.2
[1.2.1]: https://github.com/Wakup/Wakup-Android-SDK/compare/v1.2.0...v1.2.1
[1.2.0]: https://github.com/Wakup/Wakup-Android-SDK/compare/v1.1.0...v1.2.0
[1.1.0]: https://github.com/Wakup/Wakup-Android-SDK/compare/v1.0.1...v1.1.0
[1.0.1]: https://github.com/Wakup/Wakup-Android-SDK/compare/v1.0.0...v1.0.1