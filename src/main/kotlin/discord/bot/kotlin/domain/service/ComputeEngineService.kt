package discord.bot.kotlin.domain.service

interface ComputeEngineService {
  fun startInstance(): String
  fun stopInstance()
  fun getInstanceList(): List<String>
}
