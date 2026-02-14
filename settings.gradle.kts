pluginManagement {
    repositories {
      google()
      mavenCentral()
      gradlePluginPortal()
  }
}
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
      google()
      mavenCentral()
  }
}

rootProject.name = "Refoodio"
include(":app")
include(":core")
include(":feature:inventory")
include(":core:navigation")
include(":core:ui")
include(":data")
include(":data:catalog")
include(":core:database")
include(":core:domain")
