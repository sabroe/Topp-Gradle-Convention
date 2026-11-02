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

import com.yelstream.topp.grasp.util.Projects.stringProperty
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.bundling.Jar
import org.gradle.jvm.toolchain.JavaLanguageVersion

plugins {
    id("java")
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

        // Read by name with stringProperty, so a blank value counts as unset and the fallback is used.
        fun value(vararg keys: String, default: () -> String): String =
            keys.firstNotNullOfOrNull { project.stringProperty(it) } ?: default()

        tasks.withType<Jar>().configureEach {
            manifest {
                attributes(
                    "Implementation-Title" to
                        value("jar.manifest.implementation-title", "implementation.title") { project.name },
                    "Implementation-Group" to
                        value("jar.manifest.implementation-group", "implementation.group") { project.group.toString() },
                    "Implementation-Version" to
                        value("jar.manifest.implementation-version", "implementation.version") { project.version.toString() },
                    "Implementation-Vendor" to
                        value("jar.manifest.implementation-vendor", "implementation.vendor-name") { "Yelstream" },
                    "Implementation-Build-Date" to
                        value("jar.manifest.implementation-build-date", "implementation.build-date", "build-time") { "" },
                    // The toolchain version if one is set, otherwise the version the classes are compiled for.
                    "Implementation-Java-Language-Version" to
                        value("jar.manifest.implementation-java-language-version") {
                            java.toolchain.languageVersion.map { it.asInt().toString() }
                                .getOrElse(java.targetCompatibility.majorVersion)
                        },
                    "Implementation-Licence" to
                        value("jar.manifest.implementation-license", "implementation.license") { "Apache License 2.0" },
                    "Implementation-Copyright" to
                        value("jar.manifest.implementation-copyright", "implementation.copyright") {
                            "Copyright 2022-2026 Morten Sabroe Mortensen"
                        },
                    "Implementation-Author" to
                        value("jar.manifest.implementation-author", "implementation.author") { "Morten Sabroe Mortensen" },
                    "Implementation-Contact" to
                        value("jar.manifest.implementation-contact", "implementation.contact") {
                            "mailto:morten.sabroe.mortensen@gmail.com"
                        },
                    "Implementation-Comment" to
                        value("jar.manifest.implementation-comment", "implementation.comment") { "Greetings to all!" }
                )
            }

            exclude("**/.keep")
        }
    }
}
