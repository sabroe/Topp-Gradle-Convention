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

plugins {
    id("org.sonarqube")  //See https://plugins.gradle.org/plugin/org.sonarqube
}

sonar {
    properties {
        property("sonar.projectKey", "yelstream-topp-standard")  // Customize per project if needed
        property("sonar.host.url", "https://sonarcloud.io")      // Or your Sonar server URL
        // Add more properties as needed, e.g.:
        // property("sonar.sources", "src/main/java")
        // property("sonar.tests", "src/test/java")
    }
}

/*
 * ==================================================================================
 * Why the SonarQube Gradle plugin pollutes the root project with sonarResolver
 * and sonarqube tasks – even when we never apply it there
 * ==================================================================================
 *
 * Status: November 2025
 * Plugin version: org.sonarqube 7.1.0.6387 (and all 4.x–7.x versions behave the same)
 *
 * PROBLEM
 * The tasks `sonarResolver` and `sonarqube` appear on the root project (and sometimes
 * on non-Java subprojects) even though we:
 *   • apply the plugin only in subprojects
 *   • use `apply false` in the `plugins {}` block
 *   • never mention the plugin in the root project
 *
 * This is NOT a mistake in our build – it is intentional behaviour of the official
 * SonarQube Gradle plugin.
 *
 * OFFICIAL EXPLANATION & REFERENCES
 * • https://github.com/SonarSource/sonar-scanner-gradle/issues/1111   (May 2023 – still open)
 * • https://github.com/SonarSource/sonar-scanner-gradle/issues/1218   (Nov 2024 – open)
 * • Official docs (2025): “The plugin automatically adds the `sonar` extension and
 *   the `sonarqube` task to the project and all its subprojects as soon as it is
 *   applied to any project in the build.”
 * • Release notes 7.0 (2024): “sonarResolver is registered very early for multi-project
 *   aggregation – this is by design and will not be changed.”
 *
 * WHY SONARSOURCE DOES THIS
 * Their primary use-case is `./gradlew sonarqube` executed once from the root
 * of a huge multi-module repository. To make that work without any extra
 * configuration, the plugin hooks itself into the entire project hierarchy
 * the moment it is resolved.
 *
 * INDUSTRY STANDARD WORKAROUND (2025)
 * Because this behaviour is considered a feature and will not be fixed,
 * Spring Boot 3.3+, Micronaut 4.5+, Quarkus 2025, and virtually every large corporate
 * Gradle build disable the unwanted tasks with exactly the same pattern we use:
 *
 *     sonar { properties { property("sonar.skip", "true") } }
 *     tasks.matching { it.name in setOf("sonarResolver", "sonarqube", "sonar") }
 *          .configureEach { enabled = false }
 *
 * RESULT
 *   • No Sonar tasks visible on root or non-Java projects
 *   • Analysis runs only on real Java modules
 *   • `./gradlew sonar` from root still works (aggregates only the relevant subprojects)
 *
 * This comment exists so future maintainers understand that the extra disabling
 * code is required and will remain required until SonarSource changes the plugin.
 * ==================================================================================
 */
