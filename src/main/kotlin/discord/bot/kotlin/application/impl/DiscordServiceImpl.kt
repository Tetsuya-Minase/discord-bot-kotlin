package discord.bot.kotlin.application.impl

import discord.bot.kotlin.application.DiscordService
import discord.bot.kotlin.domain.model.config.DiscordConfig
import discord.bot.kotlin.domain.service.impl.ComputeEngineServiceImpl
import discord.bot.kotlin.domain.service.impl.ConfigServiceImpl
import discord4j.core.DiscordClient
import discord4j.core.DiscordClientBuilder
import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.entity.MessageChannel
import discord4j.core.`object`.util.Snowflake
import discord4j.core.event.domain.lifecycle.ReadyEvent
import discord4j.core.event.domain.message.MessageCreateEvent


class DiscordServiceImpl : DiscordService {
  private val computeEngineService: ComputeEngineServiceImpl = ComputeEngineServiceImpl()
  private val discordConfig: DiscordConfig = ConfigServiceImpl().getDiscordConfig()

  override fun connectBot() {
    val client: DiscordClient =
      DiscordClientBuilder(discordConfig.discordToken).build()

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
        // TODO: BOTにのみメンションされたら反応したい
        if (message.userMentionIds.size == 1) {
          message.userMentionIds.map { i: Snowflake -> i.asString() }.any { id -> id == discordConfig.botId }
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
        // TODO: BOTにのみメンションされたら反応したい
        if (message.userMentionIds.size == 1) {
          message.userMentionIds.map { i: Snowflake -> i.asString() }.any { id -> id == discordConfig.botId }
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

    // stop instance
    client.eventDispatcher.on(MessageCreateEvent::class.java)
      .map { messageCreateEvent: MessageCreateEvent -> messageCreateEvent.message }
      .filter { message: Message ->
        // TODO: BOTにのみメンションされたら反応したい
        if (message.userMentionIds.size == 1) {
          message.userMentionIds.map { i: Snowflake -> i.asString() }.any { id -> id == discordConfig.botId }
        } else {
          false
        }
      }
      .filter { message: Message ->
        message.content.map { content: String? -> content?.contains("stop") ?: false }
          .orElse(false)
      }
      .flatMap<MessageChannel> { message: Message -> message.channel }
      .flatMap<Message> { channel: MessageChannel ->
        val message = computeEngineService.stopInstance()
        if (message == "Success!") channel.createMessage("server stop!") else channel.createMessage("error!")
      }
      .subscribe()

    // get help
    client.eventDispatcher.on(MessageCreateEvent::class.java)
      .map { messageCreateEvent: MessageCreateEvent -> messageCreateEvent.message }
      .filter { message: Message ->
        // TODO: BOTにのみメンションされたら反応したい
        if (message.userMentionIds.size == 1) {
          message.userMentionIds.map { i: Snowflake -> i.asString() }.any { id -> id == discordConfig.botId }
        } else {
          false
        }
      }
      .filter { message: Message ->
        message.content.map { content: String? -> content?.contains("help") ?: false }
          .orElse(false)
      }
      .flatMap<MessageChannel> { message: Message -> message.channel }
      .flatMap<Message> { channel: MessageChannel ->
        val helpMessage: String = """
          command list
          ・start: start minecraft instance.
          ・stop: stop minecraft instance.
          ・list: get active instances.
          ・help: get help to use bot.
        """.trimIndent()
        channel.createMessage(helpMessage)
      }
      .subscribe()

    client.login().block()
  }
}
