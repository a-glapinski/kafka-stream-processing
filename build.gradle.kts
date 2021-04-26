import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.32" apply false
}

subprojects {
    apply {
        plugin("org.jetbrains.kotlin.jvm")
    }

    group = "pl.poznan.put"
    version = "1.0.0"

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }

    repositories {
        mavenCentral()
    }

    val implementation by configurations

    dependencies {
        implementation(kotlin("stdlib"))
        implementation(kotlin("reflect"))
        implementation("com.fasterxml.jackson.core:jackson-databind:2.12.3")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.3")
        implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.12.3")
        implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.14.1")
        implementation("org.apache.logging.log4j:log4j-core:2.14.1")
    }
}
