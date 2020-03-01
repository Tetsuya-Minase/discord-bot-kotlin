package discord.bot.kotlin.domain.service.impl

import discord.bot.kotlin.domain.service.DiscordMessageService
import discord4j.core.DiscordClient
import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.entity.MessageChannel
import discord4j.core.`object`.util.Snowflake
import discord4j.core.event.domain.message.MessageCreateEvent

class DiscordMessageServiceImpl : DiscordMessageService {
  private val computeEngineService: ComputeEngineServiceImpl = ComputeEngineServiceImpl()

  override fun startInstance(client: DiscordClient, botId: String) {
    client.eventDispatcher.on(MessageCreateEvent::class.java)
      .map { messageCreateEvent: MessageCreateEvent -> messageCreateEvent.message }
      .filter { message: Message ->
        // TODO: BOTにのみメンションされたら反応したい
        if (message.userMentionIds.size == 1) {
          message.userMentionIds.map { i: Snowflake -> i.asString() }.any { id -> id == botId }
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
  }

  override fun stopInstance(client: DiscordClient, botId: String) {
    client.eventDispatcher.on(MessageCreateEvent::class.java)
      .map { messageCreateEvent: MessageCreateEvent -> messageCreateEvent.message }
      .filter { message: Message ->
        // TODO: BOTにのみメンションされたら反応したい
        if (message.userMentionIds.size == 1) {
          message.userMentionIds.map { i: Snowflake -> i.asString() }.any { id -> id == botId }
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
  }

  override fun getInstanceList(client: DiscordClient, botId: String) {
    client.eventDispatcher.on(MessageCreateEvent::class.java)
      .map { messageCreateEvent: MessageCreateEvent -> messageCreateEvent.message }
      .filter { message: Message ->
        // TODO: BOTにのみメンションされたら反応したい
        if (message.userMentionIds.size == 1) {
          message.userMentionIds.map { i: Snowflake -> i.asString() }.any { id -> id == botId }
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

  }

  override fun getBotHelp(client: DiscordClient, botId: String) {
    client.eventDispatcher.on(MessageCreateEvent::class.java)
      .map { messageCreateEvent: MessageCreateEvent -> messageCreateEvent.message }
      .filter { message: Message ->
        // TODO: BOTにのみメンションされたら反応したい
        if (message.userMentionIds.size == 1) {
          message.userMentionIds.map { i: Snowflake -> i.asString() }.any { id -> id == botId }
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
  }
}
