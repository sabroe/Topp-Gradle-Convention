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

package com.yelstream.topp.grasp.util

import org.gradle.api.Project

object Projects {
    /**
     * Applies the convention only if enabled via property.
     *
     * Usage example in a convention plugin:
     *
     * project.plugins.withType<JavaPlugin> {
     *     if (!applyIfEnabled("my-convention-name")) return@withType
     *     // ... rest of convention logic
     * }
     */
    fun Project.applyIfEnabled(
        conventionName: String,
        default: Boolean = true,
        block: () -> Unit
    ) {
        val enablePropertyName = "convention.${conventionName}.enable"
        val enable = findProperty(enablePropertyName)?.toString()?.trim()?.toBooleanStrictOrNull() ?: default

        if (!enable) {
            logger.debug("Convention $conventionName disabled.")
        } else {
            logger.debug("Convention $conventionName enabled.")
            block()
        }
    }

    fun Project.isConventionEnabled(
        conventionName: String,
        default: Boolean = true
    ): Boolean {
        val propertyName = "convention.${conventionName}.enable"
        val enabled = findProperty(propertyName)?.toString()?.trim()?.toBooleanStrictOrNull()?:default

        if (!enabled) {
            logger.debug("Convention '$conventionName' disabled.")
        } else {
            logger.debug("Convention '$conventionName' enabled.")
        }

        return enabled
    }

}
