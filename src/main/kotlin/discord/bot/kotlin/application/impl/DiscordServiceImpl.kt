package discord.bot.kotlin.application.impl

import discord.bot.kotlin.application.DiscordService
import discord.bot.kotlin.domain.model.config.DiscordConfig
import discord.bot.kotlin.domain.service.impl.ConfigServiceImpl
import discord.bot.kotlin.domain.service.impl.DiscordMessageServiceImpl
import discord4j.core.DiscordClient
import discord4j.core.DiscordClientBuilder
import discord4j.core.event.domain.lifecycle.ReadyEvent


class DiscordServiceImpl : DiscordService {
  private val discordConfig: DiscordConfig = ConfigServiceImpl().getDiscordConfig()
  private val discordMessageService: DiscordMessageServiceImpl = DiscordMessageServiceImpl()

  override fun connectBot() {
    val client: DiscordClient =
      DiscordClientBuilder(discordConfig.discordToken).build()

    client.eventDispatcher.on(ReadyEvent::class.java)
      .subscribe { ready: ReadyEvent -> println("Logged in as " + ready.self.username) }

    // instance listを取得
    discordMessageService.getInstanceList(client, discordConfig.botId)

    // start instance
    discordMessageService.startInstance(client, discordConfig.botId)

    // stop instance
    discordMessageService.stopInstance(client, discordConfig.botId)

    // get help
    discordMessageService.getBotHelp(client, discordConfig.botId)

    client.login().block()
  }
}
