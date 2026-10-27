plugins {
    kotlin("jvm") version "2.3.20"
    application
}

repositories { mavenCentral() }

kotlin { jvmToolchain(17) }

dependencies { testImplementation(kotlin("test")) }

tasks.test { useJUnitPlatform() }

application { mainClass.set("dev.jakubolszewski.MainKt") }
