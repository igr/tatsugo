plugins {
    id("buildlogic.kotlin-application-conventions")
}

dependencies {
    implementation(project(":tatsugo-core"))
    implementation(project(":tatsugo-flow"))
}

application {
    mainClass = "example.AppKt"
}
