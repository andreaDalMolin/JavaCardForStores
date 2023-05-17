plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.8.9")

    implementation(files("C:\\JCDK\\java_card_kit-2_2_2\\java_card_kit-2_2_2-rr-bin-windows-do\\lib\\apduio.jar"))
}

tasks.test {
    useJUnitPlatform()
}