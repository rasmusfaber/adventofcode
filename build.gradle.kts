import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.9.21"
}

repositories {
  mavenCentral()
  maven("https://jitpack.io")
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.21")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
  implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.21")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.6.1")
  implementation("io.github.microutils:kotlin-logging:1.6.20")
  implementation("com.google.guava:guava:32.1.3-jre")
  implementation("org.bouncycastle:bcprov-jdk15on:1.60")
  testImplementation("junit:junit:4.12")
  testImplementation("com.nhaarman:mockito-kotlin:1.6.0")
  implementation("org.apache.commons:commons-text:1.11.0")
  implementation("com.github.mifmif:generex:1.0.2")
  implementation("com.marcinmoskala:DiscreteMathToolkit:1.0.3")
  implementation("com.github.aballano:MnemoniK:1.0.0")
  implementation("net.sourceforge.tess4j:tess4j:4.6.0")
  implementation("net.java.dev.jna:jna:5.5.0")
  implementation("net.java.dev.jna:jna-platform:5.5.0")
  implementation("org.hexworks.zircon:zircon.core:2018.5.0-RELEASE")
  implementation("org.hexworks.zircon:zircon.jvm:2018.5.0-RELEASE")
  implementation("org.hexworks.zircon:zircon.jvm.swing:2018.5.0-RELEASE")
  implementation("com.google.zxing:core:3.4.1")
  implementation("com.google.zxing:javase:3.4.1")
  implementation("org.jgrapht:jgrapht-core:1.5.0")
  implementation("io.arrow-kt:arrow-core:0.7.3")
  implementation("com.github.kittinunf.fuel:fuel:2.3.1")
  implementation("io.github.tudo-aqua:z3-turnkey:4.8.12")
  implementation("guru.nidi:graphviz-kotlin:0.18.1")
  implementation("org.jetbrains.kotlinx:multik-core:0.2.2")
  implementation("org.jetbrains.kotlinx:multik-default:0.2.2")
}

kotlin { // Extension for easy setup
  jvmToolchain(17) // Target version of generated JVM bytecode. See 7️⃣
}
