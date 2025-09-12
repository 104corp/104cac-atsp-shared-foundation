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
- `foundation/src/commonMain/kotlin/com/m104atsp/foundation/Platform.kt`: Platform abstraction interface
- Platform-specific implementations in `Platform.android.kt` and `Platform.ios.kt`
- `foundation/src/commonMain/kotlin/com/m104atsp/foundation/conmunication/date/`: Date validation business logic
  - `DateRuleChecker.kt`: Interview date validation with business rules
  - `InterviewDateError.kt`: Error states enum for date validation

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

# Build iOS framework for simulator
./gradlew :foundation:linkDebugFrameworkIosSimulatorArm64
./gradlew :foundation:linkReleaseFrameworkIosSimulatorArm64

# Build XCFramework (includes both device and simulator)
./gradlew :foundation:assembleM104FoundationXCFramework

# Clean build
./gradlew clean

# Build entire project
./gradlew build
```

### Quality & Verification
```bash
# Run lint analysis
./gradlew lint
./gradlew :foundation:lintDebug

# Run all verification tasks
./gradlew check
```

### Testing
```bash
# Run all tests across platforms
./gradlew test
./gradlew :foundation:allTests

# Run Android unit tests
./gradlew :foundation:testDebugUnitTest
./gradlew :foundation:testReleaseUnitTest

# Run iOS tests
./gradlew :foundation:iosSimulatorArm64Test
./gradlew :foundation:iosArm64Test

# Run verification tasks (includes testing)
./gradlew check
```

### Development Setup
- **Kotlin**: 2.0.0
- **Android Gradle Plugin**: 8.10.1  
- **JVM Target**: Java 17
- **Platforms**: Android (minSdk 24, targetSdk 36) and iOS (arm64 + simulator)
- **Key Dependencies**: kotlinx-datetime (0.6.1) for cross-platform date/time handling

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

### Existing Business Logic Components

#### Date Validation System
- **`DateRuleChecker`**: Comprehensive interview date validation
  - `checkInterviewDatesViewErrors()`: Basic interview date validation
  - `checkCollaborativeInterviewDatesViewErrors()`: Advanced validation with time slot range checking
  - Empty list validation (required field check)
  - Expiration validation against system time using kotlinx-datetime
  - Duplicate detection with minute-level precision
  - Time slot range validation for collaborative interviews
  - Prioritized error handling (required → expired → duplicate → out of range)
- **`InterviewDateError`**: Error states enum (`NONE`, `MUST`, `DATE_EXPIRED`, `INTERVIEW_DATE_REPEAT`, `OUT_OF_RANGE`)

### Example Business Logic Areas
- Date/time validation and business rules
- Cross-platform data processing algorithms
- Business rule enforcement
- API data models and parsing
- Platform-agnostic utility functions

### Development Notes

#### Time Handling
- Use `kotlinx-datetime` for all date/time operations
- Get system time with `Clock.System.now().toEpochMilliseconds()`
- Supports cross-platform time operations without platform-specific code

#### Project Structure
- Version management via `gradle/libs.versions.toml` (version catalog)
- Framework naming: iOS framework is named "M104Foundation"
- Package structure follows `com.m104atsp.foundation` namespace