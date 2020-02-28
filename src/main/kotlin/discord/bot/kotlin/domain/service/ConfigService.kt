package discord.bot.kotlin.domain.service

import discord.bot.kotlin.domain.model.config.ComputeEngineConfig
import discord.bot.kotlin.domain.model.config.DiscordConfig

interface ConfigService {
  fun getComputeEngineConfig(): ComputeEngineConfig
  fun getDiscordConfig(): DiscordConfig
}
