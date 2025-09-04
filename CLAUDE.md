# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a **Kotlin Multiplatform** project named **M104SharedLogic** that focuses exclusively on shared business logic between iOS and Android platforms. The project contains **no UI framework dependencies** and is designed as a foundation library for cross-platform business logic.

**Project Name**: M104SharedLogic  
**Module**: `:foundation`  
**Package**: `com.m104atsp.foundation`

## Architecture

### Module Structure
- **`/foundation`**: Core multiplatform module containing shared business logic
  - `src/commonMain/kotlin/`: Shared Kotlin business logic for all platforms
  - `src/androidMain/kotlin/`: Android-specific implementations
  - `src/iosMain/kotlin/`: iOS-specific implementations  
  - `src/commonTest/kotlin/`: Shared unit tests

### Key Files
- `foundation/src/commonMain/kotlin/com/m104atsp/foundation/BusinessLogic.kt`: Main business logic class
- `foundation/src/commonMain/kotlin/com/m104atsp/foundation/Platform.kt`: Platform abstraction interface
- Platform-specific implementations in `Platform.android.kt` and `Platform.ios.kt`

### Design Philosophy
- **Business Logic Only**: No UI components, frameworks, or presentation layer code
- **Cross-Platform**: Shared algorithms, data processing, and business rules
- **Platform Abstractions**: Use expect/actual pattern for platform-specific functionality
- **Pure Kotlin**: Focus on business domain without framework dependencies

## Development Commands

### Build Commands
```bash
# Build the foundation library
./gradlew :foundation:build

# Build Android library (AAR)
./gradlew :foundation:assembleDebug
./gradlew :foundation:assembleRelease

# Build iOS framework
./gradlew :foundation:linkDebugFrameworkIosArm64
./gradlew :foundation:linkReleaseFrameworkIosArm64

# Clean build
./gradlew clean
```

### Testing
```bash
# Run common tests
./gradlew :foundation:testDebugUnitTest

# Run all tests across platforms
./gradlew test

# Test specific platforms
./gradlew :foundation:iosSimulatorArm64Test
./gradlew :foundation:iosArm64Test
```

### Development Setup
- **Kotlin**: 2.2.10
- **Android Gradle Plugin**: 8.10.1  
- **JVM Target**: Java 11
- **Platforms**: Android (minSdk 24) and iOS (arm64 + simulator)

## Integration Guide

### Android Integration
The foundation module builds to an Android library (AAR) that can be included in Android apps:
```kotlin
implementation project(':foundation')
// or
implementation 'com.m104atsp:foundation:version'
```

### iOS Integration
The foundation module builds to a static iOS framework named "M104Foundation":
1. Add the generated framework to your Xcode project
2. Import and use the shared business logic in Swift/Objective-C

## Common Development Patterns

### Adding Business Logic
1. Add new business logic classes in `commonMain/kotlin/com/m104atsp/foundation/`
2. Use platform abstractions via expect/actual for platform-specific needs
3. Write corresponding tests in `commonTest/kotlin/com/m104atsp/foundation/`

### Platform-Specific Code
- Use `expect` declarations in `commonMain`
- Provide `actual` implementations in `androidMain`/`iosMain`
- Follow existing pattern established by `Platform.kt`

### Example Business Logic Areas
- Data validation and transformation
- Business rule enforcement
- Algorithm implementations
- API data models and parsing
- Utility functions and extensions