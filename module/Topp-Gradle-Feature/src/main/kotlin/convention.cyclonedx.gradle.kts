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
 * CycloneDX convention plugin.
 *
 * @author Morten Sabroe Mortensen
 * @version 1.0
 * @since 2025-12-17
 */

import org.cyclonedx.gradle.CyclonedxDirectTask
import org.cyclonedx.model.Component
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.withType

plugins {
    id("java")
    //Note: 'org.cyclonedx.bom' is not applied here, but below, only when 'convention.cyclonedx.enable' is true.
    //      Applied here, its tasks would always exist, also with the convention switched off.
    //      See https://plugins.gradle.org/plugin/org.cyclonedx.bom
}

val conventionName = "cyclonedx"

project.logger.info("Convention ${conventionName} loaded.")

project.plugins.withType<JavaPlugin> {
    val enablePropertyName = "convention.${conventionName}.enable"
    val enable = project.findProperty(enablePropertyName)?.toString()?.trim()?.toBooleanStrictOrNull()?:true
    if (!enable) {
        project.logger.debug("Convention ${conventionName} disabled.")
    } else {
        project.logger.debug("Convention ${conventionName} enabled.")

        apply(plugin = "org.cyclonedx.bom")

        val bomTask = tasks.named<CyclonedxDirectTask>("cyclonedxDirectBom") {
            group = "reporting"
            description = "Generates a per-project CycloneDX SBOM (runtime dependencies only)"

            includeConfigs.set(listOf("runtimeClasspath"))
            skipConfigs.set(listOf("compileClasspath", "testCompileClasspath"))

            //Note: Lazy, so that an 'application' plugin applied after this convention is seen.
            projectType.set(provider {
                if (project.plugins.hasPlugin("application")) Component.Type.APPLICATION else Component.Type.LIBRARY
            })

            val outputDir = layout.buildDirectory.dir("reports/cyclonedx-direct")
            jsonOutput.set(outputDir.map { it.file("bom.json") })
            xmlOutput.set(outputDir.map { it.file("bom.xml") })
        }

        // Publishes the SBOM with every Maven publication, as '<artifactId>-<version>-cyclonedx.json' and '.xml'.
        // This depends only on Gradle's 'maven-publish' plugin, not on any publishing convention: whatever creates
        // a Maven publication, the SBOM is added to it, and signed with it if signing is set up. Without
        // 'maven-publish', nothing happens. The task runs only when a publication needs its files, not in 'build'.
        project.plugins.withId("maven-publish") {
            extensions.getByType<PublishingExtension>().publications.withType<MavenPublication>().configureEach {
                artifact(bomTask.flatMap { it.jsonOutput }) {
                    classifier = "cyclonedx"
                    extension = "json"
                }
                artifact(bomTask.flatMap { it.xmlOutput }) {
                    classifier = "cyclonedx"
                    extension = "xml"
                }
            }
        }
    }
}

/*
 * Note:
 *     To inspect the content of build BOM files, consider this command for ad hoc inspection:
 *     find . -name bom.* | grep -E "(.*)/build/reports/(.*)(bom.xml|bom.json)" | while read r; do echo; echo $r; head -n30 $r; done | less
 */
