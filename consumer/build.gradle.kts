dependencies {
    implementation("org.apache.kafka:kafka-streams:2.7.0")
    implementation("org.apache.kafka:kafka-clients:2.7.0")
    implementation(project(":common"))
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "pl.poznan.put.consumer.MainKt"
        attributes["Multi-Release"] = "true"
    }

    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
}