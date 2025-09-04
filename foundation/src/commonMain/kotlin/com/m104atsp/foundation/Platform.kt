package com.m104atsp.foundation

/**
 * Platform abstraction for accessing platform-specific functionality
 */
interface Platform {
    val name: String
    val version: String
}

expect fun getPlatform(): Platform