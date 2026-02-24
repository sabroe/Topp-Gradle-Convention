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
 * SLF4J convention plugin.
 *
 * @author Morten Sabroe Mortensen
 * @version 1.0
 * @since 2025-12-16
 */

import org.gradle.api.plugins.JavaPlugin
import org.gradle.kotlin.dsl.dependencies

plugins {
    id("java-library") apply false  //Note: Required for type-safe dependency accessors!
}

val conventionName = "slf4j"

project.logger.info("Convention ${conventionName} loaded.")

project.plugins.withType<JavaPlugin> {
    val enablePropertyName = "convention.${conventionName}.enable"
    val enable = project.findProperty(enablePropertyName)?.toString()?.trim()?.toBooleanStrictOrNull()?:true
    if (!enable) {
        project.logger.debug("Convention ${conventionName} disabled.")
    } else {
        project.logger.debug("Convention ${conventionName} enabled.")

        dependencies {
            val slf4jVersion = "2.0.17"

            val apiExists = project.configurations.findByName("api") != null
            if (apiExists) {
                //Use 'api' if available (implies java-library plugin is applied)
                logger.debug("Applying 'api' dependencies.")
                api("org.slf4j:slf4j-api:${slf4jVersion}")
                api("org.slf4j:slf4j-ext:${slf4jVersion}")
            } else {
                //Fallback to 'implementation' (implies only java plugin is applied)
                logger.debug("Applying 'implementation' dependencies.")
                implementation("org.slf4j:slf4j-api:${slf4jVersion}")
                implementation("org.slf4j:slf4j-ext:${slf4jVersion}")
            }

            //Apply testImplementation only for intermediate libraries
            if (apiExists) {
                logger.debug("Applying 'testImplementation' dependencies.")
                testImplementation("org.slf4j:slf4j-simple:${slf4jVersion}")
            }
        }
    }
}
