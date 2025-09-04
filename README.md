# M104SharedLogic

> Kotlin Multiplatform shared business logic library for cross-platform mobile applications

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.0-blue.svg)](https://kotlinlang.org)
[![KMP](https://img.shields.io/badge/Kotlin-Multiplatform-orange.svg)](https://kotlinlang.org/docs/multiplatform.html)
[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com/)
[![iOS](https://img.shields.io/badge/Platform-iOS-lightgrey.svg)](https://developer.apple.com/ios/)

## 🚀 Project Overview

**M104SharedLogic** is a Kotlin Multiplatform library focused exclusively on **shared business logic** between iOS and Android platforms. This library contains no UI framework dependencies and serves as a foundation for cross-platform business logic implementation.

### Key Features

- ✅ **Pure Business Logic**: No UI dependencies, frameworks, or presentation layer code
- ✅ **Cross-Platform**: Shared algorithms, data processing, and business rules
- ✅ **Platform Abstractions**: Clean expect/actual pattern implementation
- ✅ **Date/Time Validation**: Comprehensive interview scheduling validation system
- ✅ **Type-Safe**: Full Kotlin type safety across all platforms
- ✅ **Testable**: Comprehensive unit testing coverage

## 🏗️ Architecture

```
M104SharedLogic/
├── foundation/                          # Core multiplatform module
│   ├── src/
│   │   ├── commonMain/kotlin/           # Shared business logic
│   │   │   └── com/m104atsp/foundation/
│   │   │       ├── BusinessLogic.kt     # Main business logic entry point
│   │   │       ├── Platform.kt          # Platform abstraction interface
│   │   │       └── conmunication/date/  # Date validation system
│   │   │           ├── DateRuleChecker.kt
│   │   │           └── InterviewDateError.kt
│   │   ├── androidMain/kotlin/          # Android-specific implementations
│   │   ├── iosMain/kotlin/              # iOS-specific implementations
│   │   └── commonTest/kotlin/           # Shared unit tests
│   └── build.gradle.kts                 # Module configuration
├── gradle/libs.versions.toml            # Version catalog
└── CLAUDE.md                           # Development guide
```

## 🔧 Technical Stack

- **Language**: Kotlin 2.0.0
- **Platforms**: Android (API 24+) • iOS (arm64 + Simulator)
- **Build System**: Gradle 8.14.3
- **JVM Target**: Java 17
- **Dependencies**:
  - `kotlinx-datetime 0.6.1` - Cross-platform date/time handling

## 📦 Installation

### Android Integration

Add the library to your Android project:

```kotlin
dependencies {
    implementation project(':foundation')
    // or published artifact:
    implementation 'com.m104atsp:foundation:1.0.0'
}
```

### iOS Integration

The library compiles to a static framework named **M104Foundation**:

1. Build the iOS framework:
   ```bash
   ./gradlew :foundation:linkReleaseFrameworkIosArm64
   ```

2. Add the generated framework to your Xcode project
3. Import in Swift/Objective-C:
   ```swift
   import M104Foundation
   ```

## 🛠️ Development

### Prerequisites

- Kotlin 2.0.0+
- Android Studio or IntelliJ IDEA
- Xcode (for iOS development)
- JDK 17+

### Build Commands

```bash
# Build entire project
./gradlew build

# Build foundation library
./gradlew :foundation:build

# Android library (AAR)
./gradlew :foundation:assembleDebug
./gradlew :foundation:assembleRelease

# iOS framework
./gradlew :foundation:linkDebugFrameworkIosArm64
./gradlew :foundation:linkReleaseFrameworkIosArm64
./gradlew :foundation:linkDebugFrameworkIosSimulatorArm64

# Clean build
./gradlew clean
```

### Testing

```bash
# Run all tests
./gradlew test

# Android tests
./gradlew :foundation:testDebugUnitTest

# iOS tests
./gradlew :foundation:iosSimulatorArm64Test

# Verification (includes linting)
./gradlew check
```

## 💼 Business Logic Components

### Date Validation System

The library includes a comprehensive date validation system for interview scheduling:

```kotlin
import com.m104atsp.foundation.conmunication.date.DateRuleChecker
import com.m104atsp.foundation.conmunication.date.InterviewDateError

// Validate interview dates
val timestamps = mutableListOf(1703123456789L, 1703123400000L)
val errors = DateRuleChecker.checkDates(timestamps)

// Handle validation results
errors.forEach { error ->
    when (error) {
        InterviewDateError.NONE -> // Valid date
        InterviewDateError.MUST -> // Required field error
        InterviewDateError.DATE_EXPIRED -> // Past date error
        InterviewDateError.INTERVIEW_DATE_REPEAT -> // Duplicate date error
    }
}
```

#### Validation Rules

1. **Required Field**: Empty lists return `MUST` error
2. **Expiration Check**: Dates before current system time are `DATE_EXPIRED`
3. **Duplicate Detection**: Minute-level precision duplicate checking
4. **Priority**: Required → Expired → Duplicate

### Platform Abstraction

Use expect/actual pattern for platform-specific functionality:

```kotlin
// commonMain - Platform.kt
expect fun getPlatform(): Platform

// androidMain - Platform.android.kt
actual fun getPlatform(): Platform = AndroidPlatform()

// iosMain - Platform.ios.kt  
actual fun getPlatform(): Platform = IOSPlatform()
```

## 📖 Documentation

- **[CLAUDE.md](./CLAUDE.md)** - Development guide for Claude Code
- **[DateRuleChecker.md](./foundation/src/commonMain/kotlin/com/m104atsp/foundation/conmunication/date/DateRuleChecker.md)** - Date validation system documentation
- **[docs/](./docs/)** - Additional documentation wiki

## 🧪 Testing Strategy

- **Common Tests**: Shared business logic validation in `commonTest/`
- **Platform Tests**: Platform-specific implementation testing
- **Integration Tests**: Cross-platform functionality validation
- **Coverage**: Focus on business logic and validation rules

## 🚀 Usage Examples

### Basic Business Logic

```kotlin
import com.m104atsp.foundation.BusinessLogic
import com.m104atsp.foundation.getPlatform

// Initialize business logic
val businessLogic = BusinessLogic()
val greeting = businessLogic.greet()

// Get platform information
val platform = getPlatform()
println("Running on: ${platform.name}")
```

### Cross-Platform Date Handling

```kotlin
import kotlinx.datetime.*

// Get current system time (works on all platforms)
val now = Clock.System.now()
val timestamp = now.toEpochMilliseconds()

// Local date/time
val localDateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())
val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
```

## 🤝 Contributing

1. **Follow Architecture**: Business logic only, no UI dependencies
2. **Use expect/actual**: For platform-specific implementations
3. **Write Tests**: Include comprehensive test coverage
4. **Documentation**: Update relevant documentation files

### Development Patterns

```kotlin
// ✅ Good: Pure business logic
class DataProcessor {
    fun processData(input: String): ProcessedData {
        // Business logic here
    }
}

// ❌ Bad: UI dependencies
class DataProcessor(private val context: Context) { // Android-specific
    // Don't do this in commonMain
}
```

## 📋 Project Status

- ✅ **Foundation Module**: Core business logic infrastructure
- ✅ **Date Validation**: Interview scheduling validation system  
- ✅ **Platform Abstractions**: Android and iOS implementations
- ✅ **Testing**: Unit test coverage for business logic
- 🔄 **In Progress**: Additional business logic components
- 📋 **Planned**: API data models and parsing utilities

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🔗 Links

- [Kotlin Multiplatform Documentation](https://kotlinlang.org/docs/multiplatform.html)
- [kotlinx-datetime Documentation](https://github.com/Kotlin/kotlinx-datetime)
- [Android Integration Guide](https://developer.android.com/kotlin/multiplatform)
- [iOS Integration Guide](https://kotlinlang.org/docs/multiplatform-mobile-integrate-in-existing-app.html)