plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin on the JVM
    id("org.jetbrains.kotlin.jvm").version("1.3.10")

    // Apply the application to add support for building a CLI application
    application
    idea
}

repositories {
    mavenCentral()
}

dependencies {
    // Use the Kotlin JDK 8 standard library
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    // google compute engine api
    compile("com.google.apis:google-api-services-compute:v1-rev214-1.25.0")
}

application {
    // Define the main class for the application
    mainClassName = "discord.bot.kotlin.AppKt"
}
