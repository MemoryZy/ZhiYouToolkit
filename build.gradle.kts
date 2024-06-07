plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.15.0"
}

group = "cn.zhiyou"
version = "1.4.0"

repositories {
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

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
//    version.set("2022.2.5")
    version.set("2023.2.4")
    type.set("IU") // Target IDE Platform  IU旗舰版要付费，也就是说打开Run Plugin时要输入license
    pluginName.set("ZhiYouToolkit")

    downloadSources.set(true)

    plugins.set(
        listOf(
            "com.intellij.java",
            "com.intellij.database",
            "com.jetbrains.sh",
            "org.jetbrains.kotlin",
            "JavaScript"
        )
    )
}

tasks {
    runIde {
        // 开启内部模式
        systemProperty("idea.is.internal", true)

        autoReloadPlugins.set(true)

        // Enable hotswap, requires JBR 17+ or JBR 11 with DCEVM, and run in debug mode.
        jvmArgs(listOf("-XX:+AllowEnhancedClassRedefinition"))
    }

    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
        options.encoding = "UTF-8"
    }

    patchPluginXml {
        // 支持2022.3版本到2024.1版本 (2022.3需要JDK17)
        sinceBuild.set("223")
        untilBuild.set("")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
