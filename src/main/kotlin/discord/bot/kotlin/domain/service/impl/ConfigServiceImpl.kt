package discord.bot.kotlin.domain.service.impl

import discord.bot.kotlin.domain.model.config.AllConfig
import discord.bot.kotlin.domain.model.config.ComputeEngineConfig
import discord.bot.kotlin.domain.model.config.DiscordConfig
import java.io.InputStreamReader
import java.net.URL
import java.util.*
import discord.bot.kotlin.domain.service.ConfigService as ConfigService1

class ConfigServiceImpl : ConfigService1 {
  override fun getComputeEngineConfig(): ComputeEngineConfig {
    val config: AllConfig = getAllConfig()
    return ComputeEngineConfig(
      applicationName = config.APPLICATION_NAME,
      projectId = config.PROJECT_ID,
      zoneName = config.ZONE_NAME
    )
  }

  override fun getDiscordConfig(): DiscordConfig {
    val config: AllConfig = getAllConfig()
    return DiscordConfig(botId = config.BOT_ID, discordToken = config.DISCORD_TOKEN)
  }

  private fun getAllConfig(): AllConfig {
    val prop = Properties()
    val config: URL? = this.javaClass.classLoader.getResource("config.properties")
    InputStreamReader(config?.openStream(), "UTF-8").use { inStream ->
      prop.load(inStream)
      return AllConfig(
        APPLICATION_NAME = prop.getProperty("APPLICATION_NAME"),
        PROJECT_ID = prop.getProperty("PROJECT_ID"),
        ZONE_NAME = prop.getProperty("ZONE_NAME"),
        BOT_ID = prop.getProperty("BOT_ID"),
        DISCORD_TOKEN = prop.getProperty("DISCORD_TOKEN")
      )
    }
  }
}
