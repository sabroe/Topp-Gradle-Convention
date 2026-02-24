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
    id("com.github.spotbugs-base")  //Reference: https://plugins.gradle.org/plugin/com.github.spotbugs-base
}

spotbugs {
    ignoreFailures = false
    showStackTraces = true
    showProgress = true

//    effort.set(com.github.spotbugs.snom.effort.Effort.MAX)
}

dependencies {
    spotbugs("com.github.spotbugs:spotbugs:4.9.8")
}
