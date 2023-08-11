import japicmp.model.JApiChangeStatus
import me.champeau.gradle.japicmp.JapicmpTask
import org.yaproxy.yap.japicmp.AcceptMethodAbstractNowDefaultRule
import org.yaproxy.yap.tasks.GradleBuildWithGitRepos
import org.yaproxy.yap.tasks.internal.JapicmpExcludedData
import java.time.LocalDate
import java.util.stream.Collectors

plugins {
    `java-library`
    jacoco
    id("me.champeau.gradle.japicmp")
    id("org.yaproxy.common") version "0.1.0"
    id("org.yaproxy.crowdin") version "0.3.1"
    org.yaproxy.yap.distributions
    org.yaproxy.yap.installers
    org.yaproxy.yap.`github-releases`
    org.yaproxy.yap.jflex
    org.yaproxy.yap.publish
    org.yaproxy.yap.spotless
    org.yaproxy.yap.test
}

group = "org.yaproxy"
val versionBC = project.property("yap.japicmp.baseversion") as String

val versionLangFile = "1"
val creationDate by extra { project.findProperty("creationDate") ?: LocalDate.now().toString() }
val distDir = file("src/main/dist/")

java {
    // Compile with appropriate Java version when building YAP releases.
    if (System.getenv("YAP_RELEASE") != null) {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(System.getenv("YAP_JAVA_VERSION")))
        }
    } else {
        val javaVersion = JavaVersion.VERSION_11
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
}

crowdin {
    credentials {
        token.set(System.getenv("CROWDIN_AUTH_TOKEN"))
    }

    configuration {
        file.set(file("gradle/crowdin.yml"))
    }
}

tasks.named<JacocoReport>("jacocoTestReport") {
    reports {
        xml.required.set(true)
    }
}

dependencies {
    api("com.fifesoft:rsyntaxtextarea:3.3.4")
    api("com.github.zafarkhaja:java-semver:0.9.0")
    api("commons-beanutils:commons-beanutils:1.9.4")
    api("commons-codec:commons-codec:1.16.0")
    api("commons-collections:commons-collections:3.2.2")
    api("commons-configuration:commons-configuration:1.10")
    api("commons-httpclient:commons-httpclient:3.1")
    api("commons-io:commons-io:2.13.0")
    api("commons-lang:commons-lang:2.6")
    api("org.apache.commons:commons-lang3:3.12.0")
    api("org.apache.commons:commons-text:1.10.0")
    api("edu.umass.cs.benchlab:harlib:1.1.3")
    api("javax.help:javahelp:2.0.05")
    val log4jVersion = "2.20.0"
    api("org.apache.logging.log4j:log4j-api:$log4jVersion")
    api("org.apache.logging.log4j:log4j-1.2-api:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
    api("net.htmlparser.jericho:jericho-html:3.4")
    api("net.sf.json-lib:json-lib:2.4:jdk15")
    api("org.apache.commons:commons-csv:1.10.0")
    api("org.hsqldb:hsqldb:2.7.2")
    api("org.jfree:jfreechart:1.5.4")
    api("org.jgrapht:jgrapht-core:0.9.0")
    api("org.swinglabs.swingx:swingx-all:1.6.5-1")

    implementation("com.formdev:flatlaf:3.1.1")

    runtimeOnly("commons-logging:commons-logging:1.2")
    runtimeOnly("xom:xom:1.3.9") {
        setTransitive(false)
    }

    testImplementation("net.bytebuddy:byte-buddy:1.14.0")
    testImplementation("org.hamcrest:hamcrest-core:2.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.mockito:mockito-junit-jupiter:5.1.1")
    testImplementation("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")

    testRuntimeOnly(files(distDir))

    testGuiImplementation("org.assertj:assertj-swing:3.17.1")
}

tasks.register<JavaExec>("run") {
    group = ApplicationPlugin.APPLICATION_GROUP
    description = "Runs YAP from source, using the default dev home."

    mainClass.set("org.yaproxy.yap.YAP")
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = distDir
}

listOf("jar", "jarDaily").forEach {
    tasks.named<Jar>(it) {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
        dirMode = "0755".toIntOrNull(8)
        fileMode = "0644".toIntOrNull(8)

        val attrs = mapOf(
            "Main-Class" to "org.yaproxy.yap.YAP",
            "Implementation-Version" to ToString({ archiveVersion.get() }),
            "Create-Date" to creationDate,
            "Class-Path" to ToString({ configurations.runtimeClasspath.get().files.stream().map { file -> "lib/${file.name}" }.sorted().collect(Collectors.joining(" ")) }),
        )

        manifest {
            attributes(attrs)
        }
    }
}

val japicmp by tasks.registering(JapicmpTask::class) {
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    description = "Checks ${project.name}.jar binary compatibility with latest version ($versionBC)."

    oldClasspath.from(yapJar(versionBC))
    newClasspath.from(tasks.named<Jar>(JavaPlugin.JAR_TASK_NAME))
    ignoreMissingClasses.set(true)

    var excludedDataFile = "$projectDir/gradle/japicmp.yaml"
    inputs.file(excludedDataFile)

    var excludedData = JapicmpExcludedData.from(excludedDataFile)
    packageExcludes.set(excludedData.packageExcludes)
    fieldExcludes.set(excludedData.fieldExcludes)
    classExcludes.set(excludedData.classExcludes)
    methodExcludes.set(excludedData.methodExcludes)

    richReport {
        destinationDir.set(file("$buildDir/reports/japicmp/"))
        reportName.set("japi.html")
        addDefaultRules.set(true)
        addRule(JApiChangeStatus.MODIFIED, AcceptMethodAbstractNowDefaultRule::class.java)
    }
}

tasks.named(LifecycleBasePlugin.CHECK_TASK_NAME) {
    dependsOn(japicmp)
}

tasks.named<Javadoc>("javadoc") {
    title = "Zed Attack Proxy"
    source = sourceSets["main"].allJava.matching {
        include("org/parosproxy/**")
        include("org/yaproxy/**")
    }
    (options as StandardJavadocDocletOptions).run {
        links("https://docs.oracle.com/javase/8/docs/api/")
        encoding = "UTF-8"
        source("${java.targetCompatibility}")
    }
}

val langPack by tasks.registering(Zip::class) {
    group = LifecycleBasePlugin.BUILD_GROUP
    description = "Assembles the language pack for the Core Language Files add-on."

    archiveFileName.set("$buildDir/langpack/YAP_${project.version}_language_pack.$versionLangFile.yaplang")
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true

    into("lang") {
        from(File(distDir, "lang"))
        from("src/main/resources/org/yaproxy/yap/resources") {
            include("Messages.properties", "vulnerabilities.xml")
        }
    }
}

tasks.register<Copy>("copyLangPack") {
    group = "YAP Misc"
    description = "Copies the language pack into the Core Language Files add-on (assumes yap-extensions repo is in same directory as yaproxy)."

    from(langPack)
    into("$rootDir/../yap-extensions/addOns/coreLang/src/main/yapHomeFiles/lang/")
}

val copyWeeklyAddOns by tasks.registering(GradleBuildWithGitRepos::class) {
    group = "YAP Misc"
    description = "Copies the weekly add-ons into plugin dir, built from local repos."

    repositoriesDirectory.set(rootDir.parentFile)
    repositoriesDataFile.set(file("src/main/weekly-add-ons.json"))
    cloneRepositories.set(false)
    updateRepositories.set(false)

    val outputDir = file("src/main/dist/plugin/")
    tasks {
        register("copyYapAddOn") {
            args.set(listOf("--into=$outputDir"))
        }
    }
}

val generateAllApiEndpoints by tasks.registering {
    group = "YAP Misc"
    description = "Generates (and copies) the YAP API endpoints for all languages."
}

listOf(
    "org.yaproxy.yap.extension.api.DotNetAPIGenerator",
    "org.yaproxy.yap.extension.api.GoAPIGenerator",
    "org.yaproxy.yap.extension.api.JavaAPIGenerator",
    "org.yaproxy.yap.extension.api.NodeJSAPIGenerator",
    "org.yaproxy.yap.extension.api.PhpAPIGenerator",
    "org.yaproxy.yap.extension.api.PythonAPIGenerator",
    "org.yaproxy.yap.extension.api.RustAPIGenerator",
    "org.yaproxy.yap.extension.api.WikiAPIGenerator",
).forEach {
    val langName = it.removePrefix("org.yaproxy.yap.extension.api.").removeSuffix("APIGenerator")
    val task = tasks.register<JavaExec>("generate${langName}ApiEndpoints") {
        group = "YAP Misc"
        description = "Generates (and copies) the YAP API endpoints for $langName."

        mainClass.set(it)
        classpath = sourceSets["main"].runtimeClasspath
        workingDir = file("$rootDir")
    }

    generateAllApiEndpoints {
        dependsOn(task)
    }
}

launch4j {
    setJarTask(tasks.named<Jar>("jar").get())
}

class ToString(private val callable: Callable<String>) {
    override fun toString() = callable.call()
}

fun yapJar(version: String): File {
    val oldGroup = group
    try {
        // https://discuss.gradle.org/t/is-the-default-configuration-leaking-into-independent-configurations/2088/6
        group = "virtual_group_for_japicmp"
        val conf = configurations.detachedConfiguration(dependencies.create("$oldGroup:$name:$version"))
        conf.isTransitive = false
        return conf.singleFile
    } finally {
        group = oldGroup
    }
}
