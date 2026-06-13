import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

dependencies {
    implementation(projects.shared)
    implementation(libs.koin.core)
    implementation(libs.compose.components.resources)
    implementation(compose.desktop.currentOs)
}

compose.desktop {
    application {
        mainClass = "org.me2you.rsyncbridge.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.me2you.rsyncbridge"
            packageVersion = "1.0.0"
            jvmArgs += listOf(
                "-Djna.library.path=/usr/lib/x86_64-linux-gnu",
                "-Djava.library.path=/usr/lib/x86_64-linux-gnu"
            )
        }

        buildTypes.release {
            proguard {
                isEnabled = true
                optimize.set(false)
                obfuscate.set(true)
                configurationFiles.from(project.file("proguard-rules.pro"))
            }
        }
    }
}