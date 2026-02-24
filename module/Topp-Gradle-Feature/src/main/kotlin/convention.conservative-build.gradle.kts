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
 * "Conservative Build" convention plugin.
 *
 * @author Morten Sabroe Mortensen
 * @version 1.0
 * @since 2025-12-21
 */

import org.gradle.api.plugins.JavaPlugin

plugins {
}

val conventionName = "conservative-build"

project.logger.info("Convention ${conventionName} loaded.")

project.plugins.withType<JavaPlugin> {

    val enablePropertyName = "convention.${conventionName}.enable"
    val enable = project.findProperty(enablePropertyName)?.toString()?.trim()?.toBooleanStrictOrNull() ?: true
    if (!enable) {
        project.logger.debug("Convention ${conventionName} disabled.")
    } else {
        project.logger.debug("Convention ${conventionName} enabled.")


        tasks.register("quickAssemble") {
            group = "build"
            description = "Clean and assemble (fastest artifact build, no checks)."
            dependsOn("clean", "assemble")
            gradle.taskGraph.whenReady {
                if (hasTask(this@register)) {
                    tasks.matching { it.name in setOf("check","javadoc") }.configureEach { enabled = false }
                }
            }
        }
        tasks.register("quickBuild") {
            group = "build"
            description = "Clean and build, skips all verification tasks (check, test, javadoc, checkstyle, etc.)."
            dependsOn("clean", "build")
            gradle.taskGraph.whenReady {
                if (hasTask(this@register)) {
                    tasks.matching { it.name in setOf("check","test","javadoc") }.configureEach { enabled = false }
                }
            }
        }

    }
}
