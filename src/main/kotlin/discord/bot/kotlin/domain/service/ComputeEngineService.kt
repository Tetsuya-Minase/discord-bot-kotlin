package discord.bot.kotlin.domain.service

interface ComputeEngineService {
  fun startInstance()
  fun stopInstance()
  fun getInstanceList(): List<String>
}
