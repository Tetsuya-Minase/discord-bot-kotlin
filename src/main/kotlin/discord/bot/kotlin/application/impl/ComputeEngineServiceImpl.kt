package discord.bot.kotlin.application.impl

import com.google.api.services.compute.Compute
import discord.bot.kotlin.application.ComputeEngineService

class ComputeEngineServiceImpl: ComputeEngineService {

    override fun startInstance() {
        val compute = Compute.Builder()
    }

    companion object {
        private const val APPLICATION_NAME = "hoge"
        private const val PROJECT_ID = "id"
        private const val ZONE_NAME = "zone"
    }

}
