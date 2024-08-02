import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
	id("org.jetbrains.compose")
	kotlin("jvm") version "2.0.0"
	id("org.jetbrains.kotlin.plugin.compose")
	kotlin("plugin.serialization") version "2.0.0"
}

group = "net.projecttl"
version = "0.1.0-beta.1"

val exposed_version: String by project

repositories {
	mavenCentral()
	maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
	google()
}

dependencies {
	// Note, if you develop a library, you should use compose.desktop.common.
	// compose.desktop.currentOs should be used in launcher-sourceSet
	// (in a separate module for demo project and in testMain).
	// With compose.desktop.common you will also lose @Preview functionality
	implementation(compose.material3)
	implementation(compose.desktop.currentOs)
	implementation(compose.materialIconsExtended)
	implementation("org.xerial:sqlite-jdbc:3.46.0.0")
	implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
	implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0-RC")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
}

compose.desktop {
	application {
		mainClass = "net.wh64.player.MainKt"

		nativeDistributions {
			targetFormats(
				TargetFormat.Dmg,
				TargetFormat.Msi,
				TargetFormat.Deb,
				TargetFormat.Rpm
			)
			packageName = "whplayer"
			packageVersion = "1.0.0"
		}
	}
}
