plugins {
    id("dev.kikugie.stonecutter")
}
stonecutter active "1.21.4"

stonecutter registerChiseled tasks.register("chiseledBuild", stonecutter.chiseled) { 
    group = "project"
    ofTask("build")
}
stonecutter registerChiseled tasks.register("chiseledBuildAndCollect", stonecutter.chiseled) {
    group = "project"
    ofTask("buildAndCollect")
}
stonecutter registerChiseled tasks.register("chiseledAssemble", stonecutter.chiseled) {
    group = "project"
    ofTask("assemble")
}
stonecutter registerChiseled tasks.register("chiseledAssembleAndCollect", stonecutter.chiseled) {
    group = "project"
    ofTask("assembleAndCollect")
}
stonecutter registerChiseled tasks.register("chiseledPublish", stonecutter.chiseled) {
    group = "project"
    ofTask("publishMods")
}