plugins {
    `kotlin-dsl`
    id("io.gitlab.arturbosch.detekt").version("1.14.2")
}

allprojects {
    repositories {
        google()
        jcenter()
        maven("https://dl.bintray.com/badoo/maven")
    }

    apply(plugin = "io.gitlab.arturbosch.detekt")

    detekt {
        toolVersion = "1.14.2"
        config = files("$rootDir/detekt.yml")

        input =
            files(
                file("src")
                    .listFiles()
                    ?.filter { it.isDirectory && it.name.endsWith("main", ignoreCase = true) }
            )
    }
}
