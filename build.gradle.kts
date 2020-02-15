plugins {
  // Apply the Kotlin JVM plugin to add support for Kotlin on the JVM
  id("org.jetbrains.kotlin.jvm").version("1.3.10")

  // Apply the application to add support for building a CLI application
  application
  idea
}

repositories {
  mavenCentral()
  google()
}

dependencies {
  // Use the Kotlin JDK 8 standard library
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  testImplementation("org.jetbrains.kotlin:kotlin-test")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
  // google compute engine api
  compile("com.google.apis:google-api-services-compute:v1-rev20190905-1.30.3")
  compile("com.google.api-client:google-api-client:1.30.8")
  compile("com.google.auth:google-auth-library-oauth2-http:0.20.0")
  compile("com.fasterxml.jackson.core:jackson-core:2.10.2")
  // discord4j
  compile("com.discord4j:discord4j-core:3.0.12")

}

application {
  // Define the main class for the application
  mainClassName = "discord.bot.kotlin.AppKt"
}
