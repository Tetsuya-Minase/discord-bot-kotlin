package discord.bot.kotlin.domain.service

interface ComputeEngineService {
  fun startInstance(): String
  fun stopInstance(): String
  fun getInstanceList(): List<String>
}
