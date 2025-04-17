import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.net.URI


plugins {
	id ("fabric-loom").version("1.10-SNAPSHOT")
	`maven-publish`
	kotlin("jvm").version("2.1.20")
	kotlin("plugin.serialization").version("2.1.20")
}

version =  property("mod_version")!!

group = property("maven_group")!!
base {
	archivesName  = property("archives_base_name")!! as String
}
repositories {
	maven {
		name = "FzzyMaven"
		url = uri("https://maven.fzzyhmstrs.me/")
	}
	maven {
		url = uri("https://oss.sonatype.org/content/repositories/snapshots")
	}
	maven { url = URI("https://maven.terraformersmc.com/releases/") }
	maven {
		url = URI("https://maven.nucleoid.xyz/")
		name = "Nucleoid"
	}
	mavenCentral()
	// Add repositories to retrieve artifacts from in here.
	// You should only use this when depending on other mods because
	// Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
	// See https://docs.gradle.org/current/userguide/declaring_repositories.html
	// for more information about repositories.
}

fabricApi {
	configureDataGeneration {
		client = true
	}
}

dependencies {
	// To change the versions see the gradle.properties file
	minecraft ("net.minecraft:minecraft:${property("minecraft_version")}")
	mappings ("net.fabricmc:yarn:${property("yarn_mappings")}:v2")
	modImplementation ("net.fabricmc:fabric-loader:${property("loader_version")}")

	// Fabric API. This is technically optional, but you probably want it anyway.
	modImplementation(libs.fabric.api)
	modImplementation(libs.fabric.language.kotlin)

	modImplementation(libs.fzzy.config)


	include(implementation(platform("org.dizitart:nitrite-bom:4.3.0"))!!)
	include(implementation  ("org.dizitart:potassium-nitrite")!!)
	include(implementation  ("org.dizitart:nitrite")!!)
	include(implementation  ("org.dizitart:potassium-nitrite")!!)
	include(implementation  ("org.dizitart:nitrite-mvstore-adapter")!!)

	include(modImplementation("eu.pb4:placeholder-api:2.5.2+1.21.3")!!)
	include(modImplementation("me.lucko:fabric-permissions-api:0.3.3")!!)
	include(implementation ("com.h2database:h2:2.3.232")!!)

	modLocalRuntime ("com.terraformersmc:modmenu:13.0.2")

}
tasks {
	processResources {
		filesMatching("fabric.mod.json") {
			expand(getProperties())
		}
	}

	compileJava {
		options.release = 21
	}
	compileKotlin {
		compilerOptions {
			jvmTarget = JvmTarget.JVM_21
		}
	}
	jar {
		inputs.property("archivesName", project.base.archivesName)
		from("LICENSE") {
		rename { "${it}_${inputs.properties["archivesName"]}" }
		}

	}
}
java {
	withSourcesJar()
	withJavadocJar()
	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21
}
publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			artifactId = properties["archives_base_name"]!! as String
			from(components["java"])
		}
	}
	repositories {
		// Add repositories to publish to here.
		// Notice: This block does NOT have the same function as the block in the top level.
		// The repositories here will be used for publishing your artifact, not for
		// retrieving dependencies.
	}
}