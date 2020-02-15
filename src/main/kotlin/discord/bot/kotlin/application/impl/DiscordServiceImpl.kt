package discord.bot.kotlin.application.impl

import discord.bot.kotlin.application.DiscordService
import discord.bot.kotlin.domain.service.impl.ComputeEngineServiceImpl
import discord4j.core.DiscordClient
import discord4j.core.DiscordClientBuilder
import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.entity.MessageChannel
import discord4j.core.`object`.util.Snowflake
import discord4j.core.event.domain.lifecycle.ReadyEvent
import discord4j.core.event.domain.message.MessageCreateEvent


class DiscordServiceImpl : DiscordService {
  private val BOT_ID = ""
  private val DISCORD_TOKEN = ""
  private val computeEngineService: ComputeEngineServiceImpl = ComputeEngineServiceImpl()

  override fun connectBot() {
    val client: DiscordClient =
      DiscordClientBuilder(DISCORD_TOKEN).build()

    client.eventDispatcher.on(ReadyEvent::class.java)
      .subscribe { ready: ReadyEvent -> println("Logged in as " + ready.self.username) }

    client.eventDispatcher.on(MessageCreateEvent::class.java)
      .map { obj: MessageCreateEvent -> obj.message }
      .filter { msg: Message ->
        msg.content.map { anObject: String? -> "!ping".equals(anObject) }.orElse(false)
      }
      .flatMap<MessageChannel> { obj: Message -> obj.channel }
      .flatMap<Message> { channel: MessageChannel ->
        // ここでなにかやる
        channel.createMessage("Pong!")
      }
      .subscribe()

    // instance listを取得
    client.eventDispatcher.on(MessageCreateEvent::class.java)
      .map { messageCreateEvent: MessageCreateEvent -> messageCreateEvent.message }
      .filter { message: Message ->
        println(message.userMentionIds.size)
        // TODO: BOTにのみメンションされたら反応したい
        if (message.userMentionIds.size == 1) {
          message.userMentionIds.map { i: Snowflake -> i.asString() }.any { id -> id == BOT_ID }
        } else {
          false
        }
      }
      .filter { message: Message ->
        message.content.map { content: String? -> content?.contains("list") ?: false }
          .orElse(false)
      }
      .flatMap<MessageChannel> { message: Message -> message.channel }
      .flatMap<Message> { channel: MessageChannel ->
        // activeのinstanceListを取得
        val instanceNameList = computeEngineService.getInstanceList()
        if (instanceNameList.isEmpty()) {
          return@flatMap channel.createMessage("active instance is none.")
        } else {
          val instanceString = instanceNameList.joinToString(separator = "\n")
          return@flatMap channel.createMessage("instance list. \n$instanceString")
        }
      }
      .subscribe()

    // start instance
    client.eventDispatcher.on(MessageCreateEvent::class.java)
      .map { messageCreateEvent: MessageCreateEvent -> messageCreateEvent.message }
      .filter { message: Message ->
        println(message.userMentionIds.size)
        // TODO: BOTにのみメンションされたら反応したい
        if (message.userMentionIds.size == 1) {
          message.userMentionIds.map { i: Snowflake -> i.asString() }.any { id -> id == BOT_ID }
        } else {
          false
        }
      }
      .filter { message: Message ->
        message.content.map { content: String? -> content?.contains("start") ?: false }
          .orElse(false)
      }
      .flatMap<MessageChannel> { message: Message -> message.channel }
      .flatMap<Message> { channel: MessageChannel ->
        val message = computeEngineService.startInstance()
        if (message == "Success!") channel.createMessage("server start!") else channel.createMessage("error!")
      }
      .subscribe()

    client.login().block()
  }
}
