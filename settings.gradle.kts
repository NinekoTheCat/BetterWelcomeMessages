pluginManagement {
	repositories {
		maven("https://maven.fabricmc.net/") {
			name = "Fabric"
		}
        maven("https://maven.kikugie.dev/releases")
        maven("https://maven.kikugie.dev/snapshots")
		mavenCentral()
		gradlePluginPortal()
	}
}

plugins {
    id("dev.kikugie.stonecutter") version "0.6-beta.2"
}
stonecutter {
    create(rootProject) {
        versions( "1.21.2","1.21.3","1.21.4","1.21.5")
        vcsVersion = "1.21.4"
    }


}
