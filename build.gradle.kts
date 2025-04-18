import me.modmuss50.mpp.ReleaseType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.net.URI

plugins {
	id ("fabric-loom").version("1.10-SNAPSHOT")
	`maven-publish`
	kotlin("jvm").version("2.1.20")
    id("me.modmuss50.mod-publish-plugin") version "0.8.4"
    id("com.palantir.git-version") version "3.2.0"
    id("com.google.devtools.ksp").version("2.1.20-2.0.0")
}

version =  property("mod_version")!!

group = property("maven_group")!!
base {
	archivesName  = "${property("archives_base_name")!!}-mc${stonecutter.current.project}"
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
loom {
    runConfigs.all {
        ideConfigGenerated(true)
        runDir = "../../run"
    }

}
dependencies {
    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.15.2")
	// To change the versions see the gradle.properties file
	minecraft("com.mojang:minecraft:${stonecutter.current.project}")
	mappings ("net.fabricmc:yarn:${property("deps.yarn_mappings")}:v2")
    modImplementation(libs.fabric.loader)

	// Fabric API. This is technically optional, but you probably want it anyway.
	modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_version")}")
	modImplementation(libs.fabric.language.kotlin)

	modImplementation("me.fzzyhmstrs:fzzy_config:${property("deps.fzzy_config")}")

    include(implementation(platform("org.dizitart:nitrite-bom:4.3.0"))!!)
	include(implementation  ("org.dizitart:potassium-nitrite")!!)
	include(implementation  ("org.dizitart:nitrite")!!)
	include(implementation  ("org.dizitart:potassium-nitrite")!!)
	include(implementation  ("org.dizitart:nitrite-mvstore-adapter")!!)
	include(modImplementation("eu.pb4:placeholder-api:${property("deps.placeholder-api")}")!!)
	include(modImplementation("me.lucko:fabric-permissions-api:0.3.3")!!)
    implementation(libs.h2)
	include(libs.h2)
    include(implementation("com.squareup.okio:okio:3.6.0")!!)
    include(implementation("com.squareup.okio:okio-jvm:3.6.0")!!)
    include(implementation("com.squareup.okhttp3:okhttp:4.12.0")!!)
    include(implementation("com.squareup.moshi:moshi-kotlin:1.15.2")!!)
    include(implementation("com.squareup.moshi:moshi:1.15.2")!!)
    modApi("com.terraformersmc:modmenu:${property("deps.modmenu")}")
	modLocalRuntime ("com.terraformersmc:modmenu:${property("deps.modmenu")}")

}
tasks {
	processResources {
        inputs.property("minecraft", stonecutter.current.version)
		filesMatching("fabric.mod.json") {
			expand(getProperties() +mapOf("minecraft" to stonecutter.current.version) )
		}
        filesMatching("assets/default_welcome_message.txt") {
            expand(getProperties() + mapOf("minecraft" to stonecutter.current.version))
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

    register<Copy>("buildAndCollect") {
        group = "build"

        from(remapJar.get().archiveFile)
        into(rootProject.layout.buildDirectory.file("libs/"))
        dependsOn("build")
    }
    register<Copy>("assembleAndCollect") {
        group = "build"

        from(remapJar.get().archiveFile)
        into(rootProject.layout.buildDirectory.file("libs/"))
        dependsOn("assemble")
    }
    this.publishMods.configure {
        dependsOn("assembleAndCollect")
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


publishMods {
    val mod = property("mod_version") as String
    val mc = stonecutter.current.project
    displayName = "Better Welcome Messages v$mod mc$mc"
    version = "$mod+mc$mc"
    type = ReleaseType.of(providers.environmentVariable("RELEASE_TYPE").getOrElse("STABLE"))
    file = tasks.remapJar.get().archiveFile
    additionalFiles.from(tasks.remapSourcesJar.get().archiveFile)
    modLoaders.add("fabric")
    changelog = file(rootDir.path + "/CHANGELOG.md").readText()
    modrinth {
        accessToken = providers.environmentVariable("MODRINTH_TOKEN").getOrElse(String())
        projectId = "YQKZc9je"
        minecraftVersions.add(mc)
        requires{
            id="hYykXjDp"
            version = property("deps.fzzy_config").toString()
        }
        embeds {
            id="eXts2L7r"
            version = property("deps.placeholder-api").toString()

        }
        embeds {
            id="lzVo0Dll"
            version = "0.3.3"
        }
    }
    github {
        announcementTitle = "Better Welcome Messages v$mod for $mc release."
        accessToken = providers.environmentVariable("GITHUB_TOKEN").getOrElse(String())
        repository = "NinekoTheCat/BetterWelcomeMessages"
        commitish= providers.environmentVariable("GIT_BRANCH").getOrElse(String())
    }
}
