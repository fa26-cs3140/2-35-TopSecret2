plugins {
    id("java")
    application
}

application {
    mainClass.set("TopSecret") // Changed mainClassName to mainClass
}

group = "edu.virgina.cs"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}
tasks.jar {
    archiveFileName.set("TopSecret.jar")
    manifest {
        attributes["Main-Class"] = "TopSecret"
    }
}
