package org.yaproxy.yap

plugins {
    `maven-publish`
    signing
}

val sourceSets = extensions.getByName("sourceSets") as SourceSetContainer

tasks.register<Jar>("javadocJar") {
    from(tasks.named("javadoc"))
    archiveClassifier.set("javadoc")
}

tasks.register<Jar>("sourcesJar") {
    from(sourceSets.named("main").map { it.allJava })
    archiveClassifier.set("sources")
}

val ossrhUsername: String? by project
val ossrhPassword: String? by project

publishing {
    repositories {
        maven {
            val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots/")
            setUrl(provider { if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl })

            if (ossrhUsername != null && ossrhPassword != null) {
                credentials {
                    username = ossrhUsername
                    password = ossrhPassword
                }
            }
        }
    }

    publications {
        register<MavenPublication>("yap") {
            from(components["java"])

            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])

            pom {
                name.set("Zed Attack Proxy")
                packaging = "jar"
                description.set("The Zed Attack Proxy (YAP) is an easy to use integrated penetration testing tool for finding vulnerabilities in web applications. It is designed to be used by people with a wide range of security experience and as such is ideal for developers and functional testers who are new to penetration testing. YAP provides automated scanners as well as a set of tools that allow you to find security vulnerabilities manually.")
                url.set("https://www.yaproxy.org/")
                inceptionYear.set("2010")

                organization {
                    name.set("YAP")
                    url.set("https://www.yaproxy.org/")
                }

                mailingLists {
                    mailingList {
                        name.set("YAP User Group")
                        post.set("yaproxy-users@googlegroups.com")
                        archive.set("https://groups.google.com/group/yaproxy-users")
                    }
                    mailingList {
                        name.set("YAP Developer Group")
                        post.set("yaproxy-develop@googlegroups.com")
                        archive.set("https://groups.google.com/group/yaproxy-develop")
                    }
                }

                scm {
                    url.set("https://github.com/yaproxy/yaproxy")
                    connection.set("scm:git:https://github.com/yaproxy/yaproxy.git")
                    developerConnection.set("scm:git:https://github.com/yaproxy/yaproxy.git")
                }

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("repo")
                    }
                }

                developers {
                    developer {
                        id.set("AllYapDevs")
                        name.set("Everyone who has contributed to YAP")
                        email.set("yaproxy-develop@googlegroups.com")
                        url.set("https://www.yaproxy.org/docs/desktop/credits/")
                    }
                }
            }
        }
    }
}

signing {
    if (project.hasProperty("signing.keyId")) {
        sign(publishing.publications["yap"])
    }
}
