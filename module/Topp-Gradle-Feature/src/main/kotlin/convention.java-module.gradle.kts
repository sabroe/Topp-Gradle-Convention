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
 * "Java Module" convention plugin.
 *
 * @author Morten Sabroe Mortensen
 * @version 1.0
 * @since 2025-12-20
 */

import org.gradle.kotlin.dsl.apply

plugins {
}

afterEvaluate {
    project.plugins.withType<JavaPlugin> {
        println("afterEvaluate!")
        apply(plugin = "com.yelstream.topp.grasp.feature.spotbugs")
        apply(plugin = "com.yelstream.topp.grasp.feature.sonarqube")

        apply(plugin = "convention.lombok")
        apply(plugin = "convention.slf4j")
        apply(plugin = "convention.junit")
//TODO: 2026-01-20        apply(plugin = "convention.decycle")
        apply(plugin = "convention.decycle")
        apply(plugin = "convention.git-properties")
        apply(plugin = "convention.jacoco")
        apply(plugin = "convention.checkstyle")
        apply(plugin = "convention.cyclonedx")

        apply(plugin = "convention.java-options")
        apply(plugin = "convention.java-jars")
        apply(plugin = "convention.jar-manifest")
        apply(plugin = "convention.archive-naming")
        apply(plugin = "convention.publish")
    }
}
