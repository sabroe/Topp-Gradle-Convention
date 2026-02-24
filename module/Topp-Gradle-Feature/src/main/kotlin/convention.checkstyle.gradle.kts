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
 * "Checkstyle" convention plugin.
 *
 * @author Morten Sabroe Mortensen
 * @version 1.0
 * @since 2025-12-16
 */

import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.quality.Checkstyle
import org.gradle.api.plugins.quality.CheckstyleExtension
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.withType

plugins {
    id("java") apply false
    id("checkstyle") apply false
}

val conventionName = "checkstyle"

project.logger.info("Convention ${conventionName} loaded.")

project.plugins.withType<JavaPlugin> {
    val enablePropertyName = "convention.${conventionName}.enable"
    val enable = project.findProperty(enablePropertyName)?.toString()?.trim()?.toBooleanStrictOrNull()?:true
    if (!enable) {
        project.logger.debug("Convention ${conventionName} disabled.")
    } else {
        project.logger.debug("Convention ${conventionName} enabled.")

        apply(plugin = "checkstyle")

        val sourceSets = extensions.getByType(SourceSetContainer::class.java)

        extensions.configure<CheckstyleExtension> {
            toolVersion = "12.3.0"  //See https://checkstyle.sourceforge.io/releasenotes.html

            config = resources.text.fromFile(rootProject.file("config/checkstyle/Yelstream/checkstyle.xml"))

            val checkstyleSourceSet = sourceSets.findByName("checkstyle")
            if (checkstyleSourceSet != null) {
                this.sourceSets = sourceSets.filter { it == checkstyleSourceSet }.toSet()
            }
        }

        //Optional: ensure Checkstyle tasks are part of the 'check' lifecycle (they are by default):
        tasks.withType<Checkstyle> {
            //Add any per-task configuration here if needed
        }
    }
}
