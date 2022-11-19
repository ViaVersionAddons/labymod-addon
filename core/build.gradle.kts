version = "0.1.0"

plugins {
    id("java-library")
    id("io.freefair.lombok") version "6.5.1"
}

repositories {
    mavenLocal()
    maven("https://repo.viaversion.com/")
    mavenCentral()
}

dependencies {
    labyProcessor()
    api(project(":api"))

    implementation("org.yaml:snakeyaml:1.33")
    implementation("com.viaversion:viaversion:4.4.3-SNAPSHOT")
    implementation("com.viaversion:viabackwards:4.4.2-SNAPSHOT")
    implementation("com.viaversion:viarewind-fabric:2.0.3-SNAPSHOT")

    maven("https://oss.sonatype.org/service/local/staging/deploy/maven2/", "org.yaml:snakeyaml:1.33")
    maven("https://repo.viaversion.com/", "com.viaversion:viaversion:4.4.3-SNAPSHOT")
    maven("https://repo.viaversion.com/", "com.viaversion:viabackwards:4.4.2-SNAPSHOT")
    maven("https://repo.viaversion.com/", "com.viaversion:viarewind-fabric:2.0.3-SNAPSHOT")

    compileOnly("io.netty:netty-all:4.1.77.Final")
    compileOnly("com.google.guava:guava:30.1.1-jre")
    // If you want to use external libraries, you can do that here.
    // The dependencies that are specified here are loaded into your project but will also
    // automatically be downloaded by labymod, but only if the repository is public.
    // If it is private, you have to add and compile the dependency manually.
    // You have to specify the repository, there are getters for maven central and sonatype, every
    // other repository has to be specified with their url. Example:
    // maven(mavenCentral(), "org.apache.httpcomponents:httpclient:4.5.13")
}

addon {
    internalRelease()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.compileJava {
    sourceCompatibility = JavaVersion.VERSION_1_8.toString()
    targetCompatibility = JavaVersion.VERSION_1_8.toString()
}