plugins {
    id("org.jetbrains.kotlin.jvm")
    id(Kotlin.Plugin.ID.kapt)
    id(Maven.Plugin.public)
}

group = "com.github.D10NGYANG"
version = "1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlin_ver")

    // 协程
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
}

publishing {
    publications {
        create("release", MavenPublication::class) {
            from(components.getByName("java"))
        }
    }
}