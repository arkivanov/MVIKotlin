plugins {
    `kotlin-dsl`
    id("io.gitlab.arturbosch.detekt").version("1.14.2")
    id("io.codearte.nexus-staging")
}

allprojects {
    repositories {
        google()
        jcenter()
    }

    apply(plugin = "io.gitlab.arturbosch.detekt")

    detekt {
        toolVersion = "1.14.2"
        config = files("$rootDir/detekt.yml")
        input = files(file("src").listFiles()?.filter(File::isDirectory))
    }
}

extensions.getByType<io.codearte.gradle.nexus.NexusStagingExtension>().run {
    packageGroup = "com.arkivanov.mvikotlin"
    stagingRepositoryId.set(System.getenv("SONATYPE_REPOSITORY_ID"))
    stagingProfileId = System.getenv("SONATYPE_STAGING_PROFILE_ID")
    username = "arkivanov"
    password = System.getenv("SONATYPE_PASSWORD")
}
