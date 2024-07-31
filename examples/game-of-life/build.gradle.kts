plugins {
    id("buildlogic.kotlin-application-conventions")
}

dependencies {
    implementation(project(":tatsugo-core"))
    implementation(project(":tatsugo-bus-flow"))
    implementation(project(":tatsugo-fleet-simple"))
}

application {
    mainClass = "example.gol.GameOfLife"
}
