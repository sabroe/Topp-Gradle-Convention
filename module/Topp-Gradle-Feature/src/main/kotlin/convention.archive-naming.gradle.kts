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
 * "Archive Naming" convention plugin.
 *
 * @author Morten Sabroe Mortensen
 * @version 1.0
 * @since 2025-12-18
 */

import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.plugins.JavaLibraryDistributionPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.bundling.AbstractArchiveTask

plugins {
    id("java") apply false
    id("base") apply false                 // Needed for BasePlugin and archivesName
    id("java-library-distribution") apply false  // Needed for distribution extension
}

val conventionName = "archive-naming"

project.logger.info("Convention ${conventionName} loaded.")

project.plugins.withType<JavaPlugin> {
    val enablePropertyName = "convention.${conventionName}.enable"
    val enable = project.findProperty(enablePropertyName)?.toString()?.trim()?.toBooleanStrictOrNull()?:true
    if (!enable) {
        project.logger.debug("Convention ${conventionName} disabled.")
    } else {
        project.logger.debug("Convention ${conventionName} enabled.")

        val projectNameC14N = project.name.replace(" ", "-")
        project.logger.info("Project name canonicalization: $projectNameC14N.")

        val baseNamePrefix = project.extra.properties["archives.base-name-prefix"] as? String ?: ""
        project.logger.info("Base name prefix: '$baseNamePrefix'.")

        val finalArchivesName = "$baseNamePrefix$projectNameC14N"

        project.plugins.withType<BasePlugin>().configureEach {
            base {
                archivesName.set(finalArchivesName)
            }
        }

        project.plugins.withType<JavaLibraryDistributionPlugin>().configureEach {
            tasks.named<AbstractArchiveTask>("distTar") {
                duplicatesStrategy = DuplicatesStrategy.EXCLUDE
            }
            tasks.named<AbstractArchiveTask>("distZip") {
                duplicatesStrategy = DuplicatesStrategy.EXCLUDE
            }
        }
    }
}
