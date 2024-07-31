plugins {
    id("buildlogic.kotlin-application-conventions")
}

dependencies {
    implementation(project(":tatsugo-core"))
}

application {
    mainClass = "example.AppKt"
}
