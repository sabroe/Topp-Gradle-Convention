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
 * JAcoco convention plugin.
 *
 * @author Morten Sabroe Mortensen
 * @version 1.0
 * @since 2025-12-16
 */

import org.gradle.api.plugins.JavaPlugin
import org.gradle.kotlin.dsl.dependencies

plugins {
    id("java") apply false
    id("jacoco") apply false
}

val conventionName = "jacoco"

project.logger.info("Convention ${conventionName} loaded.")

project.plugins.withType<JavaPlugin> {
    val enablePropertyName = "convention.${conventionName}.enable"
    val enable = project.findProperty(enablePropertyName)?.toString()?.trim()?.toBooleanStrictOrNull()?:true
    if (!enable) {
        project.logger.debug("Convention ${conventionName} disabled.")
    } else {
        project.logger.debug("Convention ${conventionName} enabled.")

        apply(plugin = "jacoco")

        extensions.configure<JacocoPluginExtension> {
            toolVersion = "0.8.13"  // Latest stable as of December 2025
        }

        //Ensure the test task generates the report after running:
        tasks.withType<Test> {
            finalizedBy(tasks.named("jacocoTestReport"))
        }

        //Ensure the report task runs tests first (if needed):
        tasks.withType<JacocoReport> {
            dependsOn(tasks.withType<Test>())
        }

        //Optional: further customize the report task if needed
/*
        tasks.named("jacocoTestReport", JacocoReport::class) {
            reports {
                html.required.set(true)   // Default location: build/reports/jacoco/test/html
                xml.required.set(true)    // For CI tools like SonarQube
                csv.required.set(false)
            }
        }
*/
    }
}
