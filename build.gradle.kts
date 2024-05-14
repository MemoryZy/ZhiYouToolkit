import org.jetbrains.intellij.IntelliJPluginExtension

plugins {
    java
    kotlin("jvm") version "1.4.32"
//    id("org.jetbrains.kotlin.jvm") version "1.9.0"
//    id("org.jetbrains.intellij") version "1.15.0"
    id("org.jetbrains.intellij") version "0.7.2"
}

buildscript {
    repositories {
        mavenLocal()
        maven { url=uri("https://maven.aliyun.com/repository/public/") }
        mavenCentral()
        maven { url=uri("https://plugins.gradle.org/m2/") }
        maven { url=uri("https://oss.sonatype.org/content/repositories/releases/") }
        maven { url=uri("https://dl.bintray.com/jetbrains/intellij-plugin-service") }
        maven { url=uri("https://dl.bintray.com/jetbrains/intellij-third-party-dependencies/") }
    }
    dependencies {
        classpath("org.jetbrains.intellij.plugins:gradle-intellij-plugin:0.7.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.32")
    }
}

//java {
//    sourceCompatibility = JavaVersion.VERSION_11
//    targetCompatibility = JavaVersion.VERSION_11
//}

group = "cn.zhiyou"
version = "1.2.1"

repositories {
    mavenLocal()
    jcenter()
    mavenCentral()
}

dependencies {
    implementation("cn.hutool:hutool-all:5.8.26")
    implementation("com.belerweb:pinyin4j:2.5.1")
    implementation("com.github.jsqlparser:jsqlparser:4.7")
    implementation("mysql:mysql-connector-java:8.0.22")
    implementation("org.jasypt:jasypt:1.9.3")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.16.1")
}


intellij {

}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
//intellij {
//    version = "2021.2.2"
////    version.set("2023.2.4")
//    type = "IU" // Target IDE Platform  IU旗舰版要付费，也就是说打开Run Plugin时要输入license
//
//    pluginName = "ZhiYouToolkit"
//    sandboxDirectory = "${rootProject.rootDir}/idea-sandbox"
//
//    updateSinceUntilBuild = false
//    isDownloadSources = true
//
//    // 添加Java模块，同时要在plugin.xml中定义引入Java扩展 -> <depends>com.intellij.java</depends>
//    setPlugins(
//        "java",
//        "DatabaseTools"
////        "org.intellij.plugins.markdown",
////        "com.jetbrains.sh",
////        "org.jetbrains.kotlin",
////        "org.jetbrains.plugins.yaml",
////        "com.intellij.properties",
////        "org.intellij.groovy",
//        /*"JavaScript"*/)
//}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

//tasks {
//    // Set the JVM compatibility versions
//    withType<JavaCompile> {
//        sourceCompatibility = "11"
//        targetCompatibility = "11"
//        options.encoding = "UTF-8"
//    }
////    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
////        kotlinOptions.jvmTarget = "17"
////    }
//
//    patchPluginXml {
//        // 支持2022.3版本到2024.1版本 (2022.3需要JDK17)
//        sinceBuild.set("203")
//        untilBuild.set("")
//    }
//
//    signPlugin {
//        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
//        privateKey.set(System.getenv("PRIVATE_KEY"))
//        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
//    }
//
//    publishPlugin {
//        token.set(System.getenv("PUBLISH_TOKEN"))
//    }
//}

//fun intellij(configure: Action<IntelliJPluginExtension>) {
//    version = "2021.2.2"
//
//}

