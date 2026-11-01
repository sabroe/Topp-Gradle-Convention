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

import org.gradle.api.GradleException
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

    /**
     * Reads a property by name with [Project.findProperty].
     * This finds values from `gradle.properties` files, `-P` and values set by the Choreography Cascade plugin,
     * so a convention works both with and without that plugin.
     * The value is trimmed, and a blank value counts as unset.
     */
    fun Project.stringProperty(key: String): String? =
        findProperty(key)?.toString()?.trim()?.takeIf { it.isNotEmpty() }

    /**
     * Reads a string property, see [stringProperty]. Gives [default] when the property is unset or blank.
     */
    fun Project.stringProperty(key: String, default: String): String =
        stringProperty(key) ?: default

    /**
     * Reads a boolean property, see [stringProperty]. Gives [default] when the property is unset or blank.
     * @throws GradleException if the value is neither `true` nor `false`.
     */
    fun Project.booleanProperty(key: String, default: Boolean): Boolean =
        stringProperty(key)?.let {
            it.toBooleanStrictOrNull()
                ?: throw GradleException("Property '$key' must be 'true' or 'false', was '$it'.")
        } ?: default

    /**
     * Reads an integer property, see [stringProperty]. Gives [default] when the property is unset or blank.
     * @throws GradleException if the value is not an integer.
     */
    fun Project.intProperty(key: String, default: Int): Int =
        stringProperty(key)?.let {
            it.toIntOrNull()
                ?: throw GradleException("Property '$key' must be an integer, was '$it'.")
        } ?: default

    /**
     * Reads a whitespace-separated property as a list, see [stringProperty].
     * Gives an empty list when the property is unset or blank.
     */
    fun Project.listProperty(key: String): List<String> =
        stringProperty(key)?.split(Regex("\\s+")).orEmpty()
}
