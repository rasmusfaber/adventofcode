plugins {
    kotlin("jvm") version "1.7.22"
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")

}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.22")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.22")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.4.1")
    implementation("io.github.microutils:kotlin-logging:1.6.20")
    implementation("com.google.guava:guava:26.0-jre")
    implementation("org.bouncycastle:bcprov-jdk15on:1.60")
    testImplementation("junit:junit:4.12")
    testImplementation("com.nhaarman:mockito-kotlin:1.6.0")
    implementation("org.apache.commons:commons-text:1.6")
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
}

tasks {
    sourceSets {
        main {
            java.srcDirs("src/main")
        }
        test {
            java.srcDirs("src/test")
        }
    }

    wrapper {
        gradleVersion = "7.6"
    }
}
