package discord.bot.kotlin.domain.service

import discord4j.core.DiscordClient

interface DiscordMessageService {
  fun startInstance(client: DiscordClient, botId: String): Unit
  fun stopInstance(client: DiscordClient, botId: String): Unit
  fun getInstanceList(client: DiscordClient, botId: String): Unit
  fun getBotHelp(client: DiscordClient, botId: String): Unit
}
