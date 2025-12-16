package io.github.techtastic.cc_vs

import dan200.computercraft.api.ComputerCraftAPI
import io.github.techtastic.cc_vs.apis.AerodynamicsAPI
import io.github.techtastic.cc_vs.apis.DragAPI
import io.github.techtastic.cc_vs.apis.ShipAPI

object CCVSMod {
    const val MOD_ID = "cc_vs"

    @JvmStatic
    fun init() {
        ComputerCraftAPI.registerAPIFactory(::ShipAPI)
        ComputerCraftAPI.registerAPIFactory(::AerodynamicsAPI)
        ComputerCraftAPI.registerAPIFactory(::DragAPI)
    }

    @JvmStatic
    fun initClient() {
    }
}
