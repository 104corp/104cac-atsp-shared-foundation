import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
            freeCompilerArgs.add("-Xexpect-actual-classes")
        }
    }
    
    val xcf = XCFramework("M104Foundation")
    
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "M104Foundation"
            isStatic = true
            xcf.add(this)
            
            // 設置 Swift 互操作優化
            export(libs.kotlinx.datetime)
            
            // 添加編譯選項以優化 Swift 互操作
            freeCompilerArgs += listOf(
                "-Xexport-kdoc",
                "-Xbinary=bundleId=com.m104atsp.foundation"
            )
        }
    }
    
    sourceSets {
        commonMain.dependencies {
            // Add common multiplatform dependencies here
            // For example: kotlinx-coroutines, kotlinx-serialization, etc.
            api(libs.kotlinx.datetime)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        androidMain.dependencies {
            // Android-specific dependencies
        }
        iosMain.dependencies {
            // iOS-specific dependencies
        }
    }
}

android {
    namespace = "com.m104atsp.foundation"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}