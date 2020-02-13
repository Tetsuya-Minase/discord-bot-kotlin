package discord.bot.kotlin.application.impl

import discord.bot.kotlin.application.DiscordService
import discord4j.core.DiscordClient
import discord4j.core.DiscordClientBuilder
import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.entity.MessageChannel
import discord4j.core.event.domain.lifecycle.ReadyEvent
import discord4j.core.event.domain.message.MessageCreateEvent


class DiscordServiceImpl : DiscordService {
  override fun connectBot() {
    val client: DiscordClient =
      DiscordClientBuilder("").build()
    client.eventDispatcher.on(ReadyEvent::class.java)
      .subscribe { ready: ReadyEvent -> println("Logged in as " + ready.self.username) }

    client.eventDispatcher.on(MessageCreateEvent::class.java)
      .map { obj: MessageCreateEvent -> obj.message }
      .filter { msg: Message ->
        msg.content.map { anObject: String? -> "!ping".equals(anObject) }.orElse(false)
      }
      .flatMap<MessageChannel> { obj: Message -> obj.channel }
      .flatMap<Message> { channel: MessageChannel -> channel.createMessage("Pong!") }
      .subscribe()
    client.login().block()
  }
}
