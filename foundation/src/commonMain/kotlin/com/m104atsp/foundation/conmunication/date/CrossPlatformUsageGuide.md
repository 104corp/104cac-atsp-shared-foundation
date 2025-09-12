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
import com.m104atsp.foundation.conmunication.date.InterviewDateRuleChecker
import com.m104atsp.foundation.conmunication.date.InterviewDateError
import com.m104atsp.foundation.conmunication.date.AvailableTimeSlot
```

### iOS Project Integration

1. Drag the `M104Foundation.xcframework` into your Xcode project
2. Add it to your target's "Frameworks, Libraries, and Embedded Content"
3. Import in your Swift files: `import M104Foundation`

## Usage Examples

### Android/Kotlin Usage

#### Basic Interview Date Validation

```kotlin
import com.m104atsp.foundation.conmunication.date.InterviewDateRuleChecker
import com.m104atsp.foundation.conmunication.date.InterviewDateError

// Create timestamp list (in milliseconds)
val timestamps = listOf(
    1703123456789L,  // Future date
    1703123400000L,  // Same minute as above (will be detected as duplicate)
    1609459200000L   // Past date (will be detected as expired)
)

// Method 1: Simple validation (returns Boolean)
val isValid = InterviewDateRuleChecker.checkInterviewDatesPass(timestamps)
println("Basic validation result: $isValid")

// Method 2: Detailed validation (returns error list)
val errors = InterviewDateRuleChecker.checkInterviewDatesWithErrors(timestamps)

// Process individual errors
errors.forEachIndexed { index, error ->
    if (error != InterviewDateError.NONE) {
        val timestamp = timestamps[index]
        println("Error at index $index (timestamp: $timestamp): $error")
    }
}
```

#### Collaborative Interview Date Validation

```kotlin
import com.m104atsp.foundation.conmunication.date.AvailableTimeSlot

// Create timestamp list
val timestamps = listOf(1703123456789L)

// Create available time slots
val availableSlots = listOf(
    AvailableTimeSlot(1703120000000L, 1703130000000L),
    AvailableTimeSlot(1703140000000L, 1703150000000L)
)

// Duration in milliseconds (30 minutes)
val duration = 30L * 60 * 1000

// Method 1: Simple validation
val isValid = InterviewDateRuleChecker.checkCollaborativeInterviewDatesPass(
    timestamps, availableSlots, duration
)

// Method 2: Detailed validation
val errors = InterviewDateRuleChecker.checkCollaborativeInterviewDatesWithErrors(
    timestamps, availableSlots, duration
)

// Check for specific errors
if (errors.contains(InterviewDateError.OUT_OF_RANGE)) {
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
let isValid = InterviewDateRuleChecker.checkInterviewDatesPass(timestampList: timestamps)
print("Basic validation result: \(isValid)")

// Method 2: Detailed validation (returns error array)
let errors = InterviewDateRuleChecker.checkInterviewDatesWithErrors(timestampList: timestamps)

// Process individual errors
for (index, error) in errors.enumerated() {
    if error != InterviewDateError.none {
        let timestamp = timestamps[index]
        print("Error at index \(index) (timestamp: \(timestamp)): \(error)")
    }
}
```

#### Collaborative Interview Date Validation

```swift
import M104Foundation

// Create timestamp array
let timestamps: [Int64] = [1703123456789]

// Create available time slots
let availableSlots = [
    AvailableTimeSlot(startTime: 1703120000000, endTime: 1703130000000),
    AvailableTimeSlot(startTime: 1703140000000, endTime: 1703150000000)
]

// Duration in milliseconds (30 minutes)
let duration: Int64 = 30 * 60 * 1000

// Method 1: Simple validation
let isValid = InterviewDateRuleChecker.checkCollaborativeInterviewDatesPass(
    timestampList: timestamps,
    availableTimeList: availableSlots,
    duration: duration
)

// Method 2: Detailed validation
let errors = InterviewDateRuleChecker.checkCollaborativeInterviewDatesWithErrors(
    timestampList: timestamps,
    availableTimeList: availableSlots,
    duration: duration
)

// Check for specific errors
if errors.contains(InterviewDateError.outOfRange) {
    print("Some interview times are outside available slots")
}
```

## Working with Available Time Slots

### Android/Kotlin
```kotlin
// Create time slots directly
val timeSlot = AvailableTimeSlot(1703120000000L, 1703130000000L)

// Create from Pair (for backward compatibility)
val fromPair = AvailableTimeSlot.fromPair(Pair(1703120000000L, 1703130000000L))

// Check if time range is within slot
val isWithinRange = timeSlot.containsRange(1703121000000L, 1703125000000L)
```

### iOS/Swift
```swift
import M104Foundation

// Create time slots directly
let timeSlot = AvailableTimeSlot(startTime: 1703120000000, endTime: 1703130000000)

// Check if time range is within slot
let isWithinRange = timeSlot.containsRange(start: 1703121000000, end: 1703125000000)
```

## Error Types

The `InterviewDateError` enum provides the following error types:

- `NONE`: No error
- `MUST`: Required field error (empty list)
- `DATE_EXPIRED`: Interview time is in the past
- `INTERVIEW_DATE_REPEAT`: Duplicate interview times
- `OUT_OF_RANGE`: Interview time is outside available slots (collaborative validation only)

## Best Practices

1. **Always validate input**: Use the validation methods before processing interview dates
2. **Handle errors gracefully**: Check for specific error types and provide appropriate user feedback
3. **Use appropriate validation**: Use basic validation for simple scenarios, collaborative validation when time slots are involved
4. **Time precision**: Remember that duplicate checking is done at minute precision level

## Notes

- All timestamps are in milliseconds (Unix timestamp format)
- Time validation is performed against system current time
- Duplicate detection ignores seconds and milliseconds (minute precision)
- Available time slots must completely contain the interview duration
