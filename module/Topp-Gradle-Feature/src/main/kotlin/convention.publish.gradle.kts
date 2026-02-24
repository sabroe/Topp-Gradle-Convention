/*
 * Project: Topp Grasp
 * GitHub: https://github.com/sabroe/Topp-Grasp
 *
 * Copyright 2022-2026 Morten Sabroe Mortensen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * "Publish" convention plugin.
 *
 * @author Morten Sabroe Mortensen
 * @version 1.0
 * @since 2025-12-21
 */

import org.gradle.api.plugins.JavaPlugin
import org.gradle.kotlin.dsl.dependencies
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.plugins.signing.SigningExtension
import org.gradle.plugins.signing.SigningPlugin
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.kotlin.dsl.apply

plugins {
    id("java") apply false
//    id 'maven-publish' apply false
//    id 'signing' apply false
}

val conventionName = "lombok"

project.logger.info("Convention ${conventionName} loaded.")

project.plugins.withType<JavaPlugin> {
    val enablePropertyName = "convention.${conventionName}.enable"
    val enable = project.findProperty(enablePropertyName)?.toString()?.trim()?.toBooleanStrictOrNull()?:true
    if (!enable) {
        project.logger.debug("Convention ${conventionName} disabled.")
    } else {
        project.logger.debug("Convention ${conventionName} enabled.")

        apply(plugin = "maven-publish")
        apply(plugin = "signing")

        val custom = project.properties["custom"] as? Map<*, *>
        fun customString(key: String, default: String): String = custom?.get(key)?.toString() ?: default
        fun customBoolean(key: String, default: Boolean): Boolean = custom?.get(key)?.toString()?.trim()?.toBoolean() ?: default


//https://docs.gradle.org/current/userguide/publishing_maven.html
        project.plugins.withType<MavenPublishPlugin>().configureEach {
            val publishing = extensions.getByType<PublishingExtension>()

            publishing.publications {
                create<MavenPublication>("mavenJava") {
                    groupId = customString("publishing.publication.groupId", project.group.toString())
                    artifactId = customString("publishing.publication.artifactId", project.name)
                    version = customString("publishing.publication.version", project.version.toString())

                    from(components["java"])

                    pom {
                        name.set(customString("publishing.publication.name", project.name))
                        description.set(customString("publishing.publication.description", ""))
                        url.set(customString("publishing.publication.url", ""))
                        inceptionYear.set(customString("publishing.publication.inception-year", ""))

                        licenses {
                            license {
                                name.set(customString("publishing.publication.licenses.license-1.name", "Apache License 2.0"))
                                url.set(customString("publishing.publication.licenses.license-1.url", "https://www.apache.org/licenses/LICENSE-2.0"))
                            }
                        }

                        developers {
                            developer {
                                id.set(customString("publishing.publication.developers.developer-1.id", ""))
                                name.set(customString("publishing.publication.developers.developer-1.name", ""))
                                email.set(customString("publishing.publication.developers.developer-1.email", ""))
                            }
                        }

                        scm {
                            connection.set(customString("publishing.publication.scm.connection", ""))
                            developerConnection.set(customString("publishing.publication.scm.developer-connection", ""))
                            url.set(customString("publishing.publication.scm.url", ""))
                        }
                    }
                }

                publishing.repositories {
                    maven {
                        name = "Central"
                        url = uri("https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2/")
                        credentials {
                            username = project.findProperty("centralUsername") as? String
                            password = project.findProperty("centralPassword") as? String
                        }
                    }

                    maven {
                        // DEPRECATED as of June 30, 2025
                        name = "OSSRH"
                        url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                        credentials {
                            username = project.findProperty("ossrhUsername") as? String
                            password = project.findProperty("ossrhPassword") as? String
                        }
                    }

                    maven {
                        name = "BG"
                        val snapshotUrl = "https://nexus.beumer.com/repository/maven-snapshots/"
                        val releaseUrl = "https://nexus.beumer.com/repository/maven-releases/"
                        url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotUrl else releaseUrl)
                        credentials {
                            username = project.findProperty("bgUsername") as? String
                            password = project.findProperty("bgPassword") as? String
                        }
                    }
                }
            }
        }

        plugins.withType<SigningPlugin>().configureEach {
            val publishing = extensions.getByType<PublishingExtension>()

            val signing = extensions.getByType(SigningExtension::class.java)
            val enableInMemoryKeys = customBoolean("feature.sign.allow-in-memory-keys.enable", true)

            if (enableInMemoryKeys) {
                val privateKey = System.getenv("GPG_PRIVATE_KEY")
                val passphrase = System.getenv("GPG_PASSPHRASE")

                if (privateKey != null) {
                    signing.useInMemoryPgpKeys(privateKey, passphrase ?: "")
                }
            }

            signing.sign(publishing.publications["mavenJava"])
        }



    }
}
