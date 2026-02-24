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
 * "JAR Manifest" convention plugin.
 *
 * @author Morten Sabroe Mortensen
 * @version 1.0
 * @since 2025-12-16
 */

import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.bundling.Jar
import org.gradle.jvm.toolchain.JavaLanguageVersion

plugins {
    id("java") apply false
}

val conventionName = "jar-manifest"

project.logger.info("Convention ${conventionName} loaded.")

project.plugins.withType<JavaPlugin> {
    val enablePropertyName = "convention.${conventionName}.enable"
    val enable = project.findProperty(enablePropertyName)?.toString()?.trim()?.toBooleanStrictOrNull()?:true
    if (!enable) {
        project.logger.debug("Convention ${conventionName} disabled.")
    } else {
        project.logger.debug("Convention ${conventionName} enabled.")

        tasks.withType<Jar>().configureEach {
            manifest {
                attributes(
                    "Implementation-Title" to (
                        project.extra.properties["jar.manifest.implementation-title"] as? String ?:
                        project.extra.properties["implementation.title"] as? String ?:
                        project.name
                        ),
                    "Implementation-Group" to (
                        project.extra.properties["jar.manifest.implementation-group"] as? String ?:
                        project.extra.properties["implementation.group"] as? String ?:
                        project.group.toString()
                        ),
                    "Implementation-Version" to (
                        project.extra.properties["jar.manifest.implementation-version"] as? String ?:
                        project.extra.properties["implementation.version"] as? String ?:
                        project.version.toString()
                        ),
                    "Implementation-Vendor" to (
                        project.extra.properties["jar.manifest.implementation-vendor"] as? String ?:
                        project.extra.properties["implementation.vendor-name"] as? String ?:
                        "Yelstream"
                        ),
                    "Implementation-Build-Date" to (
                        project.extra.properties["jar.manifest.implementation-build-date"] as? String ?:
                        project.extra.properties["implementation.build-date"] as? String ?:
                        project.extra.properties["build-time"] as? String ?:
                        ""
                        ),
                    "Implementation-Java-Language-Version" to (
                        project.extra.properties["jar.manifest.implementation-java-language-version"] as? String ?:
                        java.toolchain.languageVersion.map { it.asInt().toString() }.getOrElse("")
                        ),
                    "Implementation-Licence" to (
                        project.extra.properties["jar.manifest.implementation-license"] as? String ?:
                        project.extra.properties["implementation.license"] as? String ?:
                        "Apache License 2.0"
                        ),
                    "Implementation-Copyright" to (
                        project.extra.properties["jar.manifest.implementation-copyright"] as? String ?:
                        project.extra.properties["implementation.copyright"] as? String ?:
                        "Copyright 2022-2026 Morten Sabroe Mortensen"
                        ),
                    "Implementation-Author" to (
                        project.extra.properties["jar.manifest.implementation-author"] as? String ?:
                        project.extra.properties["implementation.author"] as? String ?:
                        "Morten Sabroe Mortensen"
                        ),
                    "Implementation-Contact" to (
                        project.extra.properties["jar.manifest.implementation-contact"] as? String ?:
                        project.extra.properties["implementation.contact"] as? String ?:
                        "mailto:morten.sabroe.mortensen@gmail.com"
                        ),
                    "Implementation-Comment" to (
                        project.extra.properties["jar.manifest.implementation-comment"] as? String ?:
                        project.extra.properties["implementation.comment"] as? String ?:
                        "Greetings to all!"
                        )
                )
            }

            exclude("**/.keep")
        }
    }
}
