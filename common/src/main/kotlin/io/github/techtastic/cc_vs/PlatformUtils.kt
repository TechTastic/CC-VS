package io.github.techtastic.cc_vs

import dev.architectury.injectables.annotations.ExpectPlatform

object PlatformUtils {
    @JvmStatic
    @ExpectPlatform
    fun canTeleport(): Boolean {
        throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun isCommandOnly(): Boolean {
        throw AssertionError()
    }
}