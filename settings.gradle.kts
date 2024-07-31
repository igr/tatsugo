plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "tatsugo"

include("tatsugo-core")
include("tatsugo-bus-flow")
include("tatsugo-fleet-simple")
include("examples")
include("examples:game-of-life")
