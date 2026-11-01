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
 * "Java Options" convention plugin.
 *
 * @author Morten Sabroe Mortensen
 * @version 1.0
 * @since 2025-12-20
 */

import com.yelstream.topp.grasp.format.FormatProperties
import com.yelstream.topp.grasp.util.Projects.booleanProperty
import com.yelstream.topp.grasp.util.Projects.listProperty
import com.yelstream.topp.grasp.util.Projects.stringProperty
import java.nio.charset.StandardCharsets


plugins {
    id("java")
}

val conventionName = "java-options"

    project.logger.info("Convention ${conventionName} loaded.")





project.plugins.withType<JavaPlugin> {
    val enablePropertyName = "convention.${conventionName}.enable"
    val enable = project.findProperty(enablePropertyName)?.toString()?.trim()?.toBooleanStrictOrNull()?:true
    if (!enable) {
        project.logger.debug("Convention ${conventionName} disabled.")
    } else {
        project.logger.debug("Convention ${conventionName} enabled.")

//println(formatPropertiesPretty(project.properties, title = "All Project Properties"))
        val yamlOutput = FormatProperties.propertiesAsCleanYaml(project)
        println(yamlOutput)
//        project.logger.lifecycle(yamlOutput)
/*
TODO:
-- List containers
-- List tasks
-- List extensions
-- How to set "description"?
-- List plugins
-- List dependencies
-- Mask password, secret, ...
-- List project information { description, displayName, rootProject, rootDir, projectPath, projectDir, { group, name?, version}, buildDir, buildFile}
 */

        plugins.withType<JavaPlugin>().configureEach {
            val defaultEncoding = project.stringProperty("java.default-encoding", StandardCharsets.UTF_8.name())

            tasks.compileJava {
                options.encoding = defaultEncoding
            }

            tasks.compileTestJava {
                options.encoding = defaultEncoding
            }

            java {
                val sourceJavaVersionString = project.stringProperty("java.language-version.source")
                val targetJavaVersionString = project.stringProperty("java.language-version.target")
                val toolChainJavaVersionString = project.stringProperty("java.language-version.tool-chain")
                val javaVersionString = project.stringProperty("java.language-version")

                var resolvedLanguageVersion: JavaLanguageVersion? = null

                if (!toolChainJavaVersionString.isNullOrBlank() &&
                    toolChainJavaVersionString != "*" &&
                    toolChainJavaVersionString != "!"
                ) {
                    resolvedLanguageVersion = JavaLanguageVersion.of(toolChainJavaVersionString.trim().toInt())
                } else if (toolChainJavaVersionString == "!") {
                    val defaultJavaVersion = javaVersionString ?: JavaVersion.VERSION_21.majorVersion
                    resolvedLanguageVersion = JavaLanguageVersion.of(defaultJavaVersion.trim().toInt())
                }

                sourceCompatibility = when {
                    !sourceJavaVersionString.isNullOrBlank() -> JavaVersion.toVersion(sourceJavaVersionString.trim())
                    !javaVersionString.isNullOrBlank() -> JavaVersion.toVersion(javaVersionString.trim())
                    else -> JavaVersion.VERSION_21
                }

                targetCompatibility = when {
                    !targetJavaVersionString.isNullOrBlank() -> JavaVersion.toVersion(targetJavaVersionString.trim())
                    !javaVersionString.isNullOrBlank() -> JavaVersion.toVersion(javaVersionString.trim())
                    else -> JavaVersion.VERSION_21
                }

                toolchain {
                    if (resolvedLanguageVersion != null) {
                        languageVersion.set(resolvedLanguageVersion)
                    }
                }
            }

            tasks.javadoc {
                isFailOnError = false
                (options as StandardJavadocDocletOptions).apply {
                    encoding = defaultEncoding

                    fun customBoolean(key: String, default: Boolean): Boolean = project.booleanProperty(key, default)

                    fun customString(key: String, default: String): String = project.stringProperty(key, default)

                    author(customBoolean("javadoc.author", true))
                    version(customBoolean("javadoc.version", true))
                    use(customBoolean("javadoc.use", false))
                    noDeprecatedList(customBoolean("javadoc.no-deprecated-list", false))
                    noSince(customBoolean("javadoc.no-since", false))

                    memberLevel = JavadocMemberLevel.valueOf(customString("javadoc.member-level", "PROTECTED").uppercase())

                    addBooleanOption("Xdoclint:all", customBoolean("javadoc.x.doc-lint.all", true))
                    addBooleanOption("Xdoclint:none", customBoolean("javadoc.x.doc-lint.none", false))

                    addBooleanOption("Xdoclint:accessibility", customBoolean("javadoc.x.doc-lint.accessibility", false))
                    addBooleanOption("Xdoclint:html", customBoolean("javadoc.x.doc-lint.html", false))
                    addBooleanOption("Xdoclint:missing", customBoolean("javadoc.x.doc-lint.missing", false))
                    addBooleanOption("Xdoclint:reference", customBoolean("javadoc.x.doc-lint.reference", false))
                    addBooleanOption("Xdoclint:syntax", customBoolean("javadoc.x.doc-lint.syntax", false))

                    addStringOption("Xmaxerrs", customString("javadoc.x.max-errs", "25"))
                    addStringOption("Xmaxwarns", customString("javadoc.x.max-warns", "25"))
                }
            }
        }

        tasks.withType<JavaCompile>().configureEach {
            options.compilerArgs.addAll(project.listProperty("java.compiler.default-args"))
            options.compilerArgs.addAll(project.listProperty("java.compiler.module-specific-args"))

            options.javaModuleVersion.set(provider { project.version.toString() })
        }
    }
}
