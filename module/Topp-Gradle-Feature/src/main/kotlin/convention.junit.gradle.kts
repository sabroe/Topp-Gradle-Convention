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
 * "JUnit" convention plugin.
 *
 * @author Morten Sabroe Mortensen
 * @version 1.0
 * @since 2025-12-16
 */

import org.gradle.api.plugins.JavaPlugin
import org.gradle.kotlin.dsl.dependencies

plugins {
    id("java")  //Note: Required for type-safe dependency accessors!
}

val conventionName = "junit"

project.logger.info("Convention ${conventionName} loaded.")

project.plugins.withType<JavaPlugin> {
    val enablePropertyName = "convention.${conventionName}.enable"
    val enable = project.findProperty(enablePropertyName)?.toString()?.trim()?.toBooleanStrictOrNull()?:true
    if (!enable) {
        project.logger.debug("Convention ${conventionName} disabled.")
    } else {
        project.logger.debug("Convention ${conventionName} enabled.")

        dependencies {  //The core unit-testing stack, JUnit, Mockito, AssertJ!
            val junitVersion = "6.0.2"

            testImplementation(platform("org.junit:junit-bom:${junitVersion}"))
            testImplementation("org.junit.jupiter:junit-jupiter")
            testImplementation("org.junit.platform:junit-platform-suite")

            testImplementation("org.mockito:mockito-junit-jupiter:5.21.0")
            testImplementation("org.assertj:assertj-core:3.27.7")

            testImplementation(platform("org.testcontainers:testcontainers-bom:2.0.3"))
            testImplementation("org.testcontainers:testcontainers-junit-jupiter")
        }

        tasks.withType<Test> {
            useJUnitPlatform()

            testLogging {
                events("skipped","failed")
            }

            //Note: Comma-separated patterns; unset keeps the defaults "*TestSuite" and "*Tests".
            //Note: "*TestSuite" is the preferred convention style; suites select classes named "*Test".
            //Note: "*Tests" is present to honor the typical Spring default, e.g. a generated "DemoApplicationTests".
            //      A "*Tests" class must not be selected by a suite, or its tests run twice.
            val includePatterns = project.findProperty("convention.${conventionName}.test.include-patterns")?.toString()?.split(',')?.map { it.trim() }?.filter { it.isNotEmpty() }?.ifEmpty { null } ?: listOf("*TestSuite", "*Tests")
            filter {
                includePatterns.forEach { includeTestsMatching(it) }
            }
        }
    }
}
