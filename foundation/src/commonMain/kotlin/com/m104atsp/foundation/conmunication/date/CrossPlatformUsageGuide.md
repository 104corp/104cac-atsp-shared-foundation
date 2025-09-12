# Cross-Platform Usage Examples for M104Foundation

This document provides examples of how to use the M104Foundation framework in both Android (AAR) and iOS (XCFramework) projects.

## Building for Platforms

### Building Android AAR

To build the AAR for Android, run:

```bash
./gradlew :foundation:assembleDebug
./gradlew :foundation:assembleRelease
```

The AAR files will be available at:
- Debug: `foundation/build/outputs/aar/foundation-debug.aar`
- Release: `foundation/build/outputs/aar/foundation-release.aar`

### Building iOS XCFramework

To build the XCFramework for iOS, run:

```bash
./gradlew :foundation:assembleM104FoundationXCFramework
```

The XCFramework will be available at:
- Debug: `foundation/build/XCFrameworks/debug/M104Foundation.xcframework`
- Release: `foundation/build/XCFrameworks/release/M104Foundation.xcframework`

## Integration

### Android Project Integration

1. Add the AAR file to your Android project's `libs` folder
2. Add to your module's `build.gradle`:

```gradle
dependencies {
    implementation files('libs/foundation-release.aar')
    // or
    implementation project(':foundation')
}
```

3. Import in your Kotlin/Java files:
```kotlin
import com.m104atsp.foundation.conmunication.date.InterviewDateValidator
import com.m104atsp.foundation.conmunication.date.InterviewDateError
```

### iOS Project Integration

1. Drag the `M104Foundation.xcframework` into your Xcode project
2. Add it to your target's "Frameworks, Libraries, and Embedded Content"
3. Import in your Swift files: `import M104Foundation`

## Usage Examples

### Android/Kotlin Usage

#### Basic Interview Date Validation

```kotlin
import com.m104atsp.foundation.conmunication.date.InterviewDateValidator
import com.m104atsp.foundation.conmunication.date.InterviewDateError

// Create timestamp list (in milliseconds)
val timestamps = listOf(
    1703123456789L,  // Future date
    1703123400000L,  // Same minute as above (will be detected as duplicate)
    1609459200000L   // Past date (will be detected as expired)
)

// Method 1: Simple validation (returns Boolean)
val isValid = InterviewDateValidator.isBasicInterviewDatesValid(timestamps)
println("Basic validation result: $isValid")

// Method 2: Detailed validation (returns result object)
val result = InterviewDateValidator.validateBasicInterviewDates(timestamps)
println("Is valid: ${result.isValid}")
println("Error message: ${result.errorMessage}")
println("Error count: ${result.getErrorCount()}")

// Check specific error types
if (result.hasExpiredError()) {
    println("Some dates are expired")
}

if (result.hasRepeatError()) {
    println("Some dates are duplicated")
}

// Process individual errors
result.errors.forEachIndexed { index, error ->
    if (error != InterviewDateError.NONE) {
        val timestamp = timestamps[index]
        val formattedTime = InterviewDateValidator.formatTimestamp(timestamp)
        val errorDesc = InterviewDateValidator.getErrorDescription(error)
        println("Error at index $index ($formattedTime): $errorDesc")
    }
}
```

#### Collaborative Interview Date Validation

```kotlin
// Create timestamp list
val timestamps = listOf(1703123456789L)

// Create available time slots
val availableSlots = listOf(
    InterviewDateValidator.createTimeSlot(1703120000000L, 1703130000000L),
    InterviewDateValidator.createTimeSlot(1703140000000L, 1703150000000L)
)

// Use predefined duration constants
val duration = InterviewDateValidator.DurationConstants.THIRTY_MINUTES

// Method 1: Simple validation
val isValid = InterviewDateValidator.isCollaborativeInterviewDatesValid(
    timestamps, availableSlots, duration
)

// Method 2: Detailed validation
val result = InterviewDateValidator.validateCollaborativeInterviewDates(
    timestamps, availableSlots, duration
)

if (result.hasOutOfRangeError()) {
    println("Some interview times are outside available slots")
}
```

### iOS/Swift Usage

#### Basic Interview Date Validation

```swift
import M104Foundation

// Create timestamp array (in milliseconds)
let timestamps: [Int64] = [
    1703123456789,  // Future date
    1703123400000,  // Same minute as above (will be detected as duplicate)
    1609459200000   // Past date (will be detected as expired)
]

// Method 1: Simple validation (returns Boolean)
let isValid = InterviewDateValidator.isBasicInterviewDatesValid(timestampList: timestamps)
print("Basic validation result: \(isValid)")

// Method 2: Detailed validation (returns result object)
let result = InterviewDateValidator.validateBasicInterviewDates(timestampList: timestamps)
print("Is valid: \(result.isValid)")
print("Error message: \(result.errorMessage)")
print("Error count: \(result.getErrorCount())")

// Check specific error types
if result.hasExpiredError() {
    print("Some dates are expired")
}

if result.hasRepeatError() {
    print("Some dates are duplicated")
}

// Process individual errors
for (index, error) in result.errors.enumerated() {
    if error != InterviewDateError.none {
        let timestamp = timestamps[index]
        let formattedTime = InterviewDateValidator.formatTimestamp(timestampMillis: timestamp)
        let errorDesc = InterviewDateValidator.getErrorDescription(error: error)
        print("Error at index \(index) (\(formattedTime)): \(errorDesc)")
    }
}
```

#### Collaborative Interview Date Validation

```swift
import M104Foundation

// Create timestamp array
let timestamps: [Int64] = [1703123456789]

// Create available time slots using helper method
let availableSlots: [KotlinPair<KotlinLong, KotlinLong>] = [
    InterviewDateValidator.createTimeSlot(startTimeMillis: 1703120000000, endTimeMillis: 1703130000000),
    InterviewDateValidator.createTimeSlot(startTimeMillis: 1703140000000, endTimeMillis: 1703150000000)
]

// Use predefined duration constants
let duration = InterviewDateValidator.DurationConstants.thirtyMinutes

// Method 1: Simple validation
let isValid = InterviewDateValidator.isCollaborativeInterviewDatesValid(
    timestampList: timestamps,
    availableTimeSlots: availableSlots,
    durationMillis: duration
)

// Method 2: Detailed validation
let result = InterviewDateValidator.validateCollaborativeInterviewDates(
    timestampList: timestamps,
    availableTimeSlots: availableSlots,
    durationMillis: duration
)

if result.hasOutOfRangeError() {
    print("Some interview times are outside available slots")
}
```

## Working with Duration Constants

### Android/Kotlin
```kotlin
// Use predefined duration constants
val duration15min = InterviewDateValidator.DurationConstants.FIFTEEN_MINUTES
val duration30min = InterviewDateValidator.DurationConstants.THIRTY_MINUTES
val duration1hour = InterviewDateValidator.DurationConstants.ONE_HOUR

// Or create custom duration
val customDuration = 20L * 60 * 1000 // 20 minutes in milliseconds
```

### iOS/Swift
```swift
import M104Foundation

// Use predefined duration constants
let duration15min = InterviewDateValidator.DurationConstants.fifteenMinutes
let duration30min = InterviewDateValidator.DurationConstants.thirtyMinutes
let duration1hour = InterviewDateValidator.DurationConstants.oneHour

// Or create custom duration
let customDuration: Int64 = 20 * 60 * 1000 // 20 minutes in milliseconds
```

## Utility Functions

### Android/Kotlin
```kotlin
// Get current timestamp
val currentTime = InterviewDateValidator.getCurrentTimestamp()
println("Current time: $currentTime")

// Format timestamp to readable string
val formattedTime = InterviewDateValidator.formatTimestamp(currentTime)
println("Formatted time: $formattedTime")

// Create time slots
val slot = InterviewDateValidator.createTimeSlot(
    currentTime,
    currentTime + InterviewDateValidator.DurationConstants.ONE_HOUR
)
```

### iOS/Swift
```swift
import M104Foundation

// Get current timestamp
let currentTime = InterviewDateValidator.getCurrentTimestamp()
print("Current time: \(currentTime)")

// Format timestamp to readable string
let formattedTime = InterviewDateValidator.formatTimestamp(timestampMillis: currentTime)
print("Formatted time: \(formattedTime)")

// Create time slots
let slot = InterviewDateValidator.createTimeSlot(
    startTimeMillis: currentTime,
    endTimeMillis: currentTime + InterviewDateValidator.DurationConstants.oneHour
)
```

## Error Handling

### Android/Kotlin
```kotlin
val result = InterviewDateValidator.validateBasicInterviewDates(timestamps)

// Check different error types
when (result.getFirstError()) {
    InterviewDateError.NONE -> println("All interviews are valid")
    InterviewDateError.MUST -> println("At least one interview time is required")
    InterviewDateError.DATE_EXPIRED -> println("Some interview times are in the past")
    InterviewDateError.INTERVIEW_DATE_REPEAT -> println("Some interview times are duplicated")
    InterviewDateError.OUT_OF_RANGE -> println("Some interview times are outside available slots")
}

// Get all error descriptions
val errorDescriptions = result.getAllErrorDescriptions()
println("All errors: $errorDescriptions")
```

### iOS/Swift
```swift
import M104Foundation

let result = InterviewDateValidator.validateBasicInterviewDates(timestampList: timestamps)

// Check different error types
switch result.getFirstError() {
case .none:
    print("All interviews are valid")
case .must:
    print("At least one interview time is required")
case .dateExpired:
    print("Some interview times are in the past")
case .interviewDateRepeat:
    print("Some interview times are duplicated")
case .outOfRange:
    print("Some interview times are outside available slots")
default:
    print("Unknown error")
}

// Get all error descriptions
let errorDescriptions = result.getAllErrorDescriptions()
print("All errors: \(errorDescriptions)")
```

## Direct Usage of Core API

You can also use the core `InterviewDateRuleChecker` directly if you prefer lower-level access:

### Android/Kotlin
```kotlin
val timestamps = listOf(1703123456789L, 1703127056789L)

// Direct validation
val isValid = InterviewDateRuleChecker.checkInterviewDatesPass(timestamps)
val errors = InterviewDateRuleChecker.checkInterviewDatesWithErrors(timestamps)

// Process errors
errors.forEach { error ->
    println("Error: $error")
}
```

### iOS/Swift
```swift
import M104Foundation

let timestamps: [Int64] = [1703123456789, 1703127056789]

// Direct validation
let isValid = InterviewDateRuleChecker.checkInterviewDatesPass(timestampList: timestamps)
let errors = InterviewDateRuleChecker.checkInterviewDatesWithErrors(timestampList: timestamps)

// Process errors
for error in errors {
    print("Error: \(error)")
}
```

## Platform-Specific Notes

### For Android/Kotlin Developers
1. **Type Safety**: All APIs use standard Kotlin types (`List<Long>`, `Boolean`, etc.)
2. **Null Safety**: All APIs are null-safe and don't return nullables
3. **Collections**: Use standard Kotlin collections (`listOf`, `mutableListOf`)
4. **Integration**: Import directly from the AAR package

### For iOS/Swift Developers
1. **Type Mapping**: Kotlin `Long` maps to Swift `Int64`
2. **Collections**: Kotlin `List` maps to Swift `Array`
3. **Pairs**: Kotlin `Pair<Long, Long>` maps to `KotlinPair<KotlinLong, KotlinLong>` in Swift
4. **Enums**: Kotlin enum values are accessible with lowercase names in Swift (e.g., `InterviewDateError.none`)
5. **Object Methods**: Kotlin object methods are accessible as static methods in Swift
6. **Null Safety**: All APIs are designed to be null-safe and don't return optionals

## Best Practices

### General
1. Use `InterviewDateValidator` for the unified cross-platform API
2. Use duration constants instead of calculating milliseconds manually
3. Always check the validation result before processing timestamps
4. Use the utility functions for formatting and time manipulation
5. Handle different error types appropriately in your UI

### Android Specific
1. Use the `InterviewDateValidationResult` class for comprehensive error handling
2. Leverage Kotlin's `when` expressions for clean error type handling
3. Use the helper methods like `hasExpiredError()` for quick checks

### iOS Specific
1. Use the helper method `createTimeSlot()` instead of creating Pairs directly
2. Access duration constants through the nested object structure
3. Use Swift's `switch` statements with the enum cases for error handling