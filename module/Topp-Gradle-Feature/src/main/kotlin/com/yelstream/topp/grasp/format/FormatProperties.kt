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

package com.yelstream.topp.grasp.format

import org.gradle.api.Project
import org.snakeyaml.engine.v2.api.Dump
import org.snakeyaml.engine.v2.api.DumpSettings
import org.snakeyaml.engine.v2.api.StreamDataWriter
import org.snakeyaml.engine.v2.common.FlowStyle
import java.io.StringWriter
import java.util.IdentityHashMap

/**
 * Utility for printing Gradle project properties as clean, readable YAML.
 *
 * Features:
 * - Single root key "properties"
 * - 4-space indentation
 * - Alphabetical sorting of keys (deep)
 * - Safe handling of cycles and complex Gradle objects
 * - Accurate type hints (# String, # List, # Map, # Set, # null, etc.)
 * - Clean mode hides noisy internals
 */
object FormatProperties {
    /**
     * Safely converts any value to something SnakeYAML can serialize.
     * Detects cycles using identity.
     */
    private fun safeValue(value: Any?, seen: IdentityHashMap<Any, Unit> = IdentityHashMap()): Any? =
        when (value) {
            null -> null
            is String, is Number, is Boolean -> value
            is Map<*, *> -> {
                if (seen.containsKey(value)) "<cycle: Map>"
                else {
                    seen[value] = Unit
                    val sorted = value.entries
                        .filter { it.key is String }
                        .sortedBy { it.key.toString() }
                        .associate { it.key.toString() to safeValue(it.value, seen) }
                    seen.remove(value)
                    sorted.ifEmpty { "<empty map>" }
                }
            }
            is Set<*> -> {
                if (seen.containsKey(value)) "<cycle: Set>"
                else {
                    seen[value] = Unit
                    val list = value.map { safeValue(it, seen) }.sortedBy { it.toString() }
                    seen.remove(value)
                    list.ifEmpty { "<empty set>" }
                }
            }
            is Collection<*> -> {
                if (seen.containsKey(value)) "<cycle: Collection>"
                else {
                    seen[value] = Unit
                    val list = value.map { safeValue(it, seen) }
                    seen.remove(value)
                    list.ifEmpty { "<empty collection>" }
                }
            }
            is Array<*> -> {
                if (seen.containsKey(value)) "<cycle: Array>"
                else {
                    seen[value] = Unit
                    val list = value.map { safeValue(it, seen) }
                    seen.remove(value)
                    list.ifEmpty { "<empty array>" }
                }
            }
            else -> {
                val className = value::class.simpleName ?: "Object"
                val str = value.toString()
                when {
                    str.length > 150 -> "$className {…}"
                    str.contains("@") && str.contains(className) -> className
                    else -> str.takeIf { it.isNotBlank() } ?: className
                }
            }
        }

    private fun mapAsYaml(
        properties: Map<*, *>,
        title: String,
        filter: (String) -> Boolean,
        includeTypeHints: Boolean
    ): String {
        val filteredAndSafe: Map<String, Any?> = properties.entries
            .asSequence()
            .filter { it.key is String }
            .map { it.key.toString() to it.value }
            .filter { (key, _) -> filter(key) }
            .sortedBy { it.first }
            .associate { (key, value) -> key to safeValue(value) }

        if (filteredAndSafe.isEmpty()) {
            return "=== $title (no entries after filtering) ==="
        }

        val rootMap = mapOf("properties" to filteredAndSafe)

        val settings = DumpSettings.builder()
            .setDefaultFlowStyle(FlowStyle.BLOCK)
            .setIndent(4)
            .setWidth(140)
            .build()

        val dump = Dump(settings)
        val writer = StringWriter()
        dump.dump(rootMap, object : StreamDataWriter {
            override fun write(str: String) = writer.write(str)
            override fun write(str: String, off: Int, len: Int) = writer.write(str, off, len)
            override fun flush() = writer.flush()
        })

        var yaml = writer.toString().trim()

        if (includeTypeHints) {
            yaml = yaml.lineSequence().joinToString("\n") { line ->
                if (line.trim() == "properties:" || line.trim().startsWith("properties: ")) {
                    line
                } else {
                    val trimmed = line.trim()
                    if (trimmed.matches(Regex("""^[\w.\-]+:\s*.+"""))) {
                        val key = line.substringBefore(":").trim()
                        val value = filteredAndSafe[key]
                        if (value != null) {
                            val typeHint = when {
                                value is String ->
                                    if (value.startsWith("<cycle") || value.startsWith("<empty")) "Info"
                                    else "String"
                                value is Number -> value::class.simpleName ?: "Number"
                                value is Boolean -> "Boolean"
                                value is List<*> -> "List"
                                value is Set<*> -> "Set"
                                value is Map<*,*> -> "Map"
                                else -> "Object"
                            }
                            "$line  # $typeHint"
                        } else {
                            line  // nested, no hint
                        }
                    } else {
                        line
                    }
                }
            }
        }

        return buildString {
            appendLine("=== $title (${filteredAndSafe.size} entries) ===")
            appendLine(yaml)
            appendLine("=== End of $title ===")
        }
    }

    /**
     * Full dump – shows almost everything (still hides extreme noise like self-reference).
     */
    @JvmStatic
    fun propertiesAsYaml(
        project: Project,
        title: String = "All Project Properties",
        includeTypeHints: Boolean = true
    ): String = mapAsYaml(
        properties = project.properties,
        title = title,
        filter = { key -> key != "properties" }, // hide self-reference cycle
        includeTypeHints = includeTypeHints
    )

    /**
     * Clean, readable version – perfect for daily debugging.
     */
    @JvmStatic
    fun propertiesAsCleanYaml(
        project: Project,
        title: String = "Clean Project Properties"
    ): String {
        val alwaysHide = setOf(
            "properties", "logger", "logging", "services", "layout", "fileOperations",
            "providers", "projectEvaluator", "listenerBuildOperationDecorator",
            "configurationActions", "deferredProjectConfiguration", "modelRegistry",
            "scriptHandlerFactory", "scriptPluginFactory", "taskDependencyFactory",
            "dependencyFactory", "standardOutputCapture", "crossProjectModelAccess",
            "local", "nonRootGradle", "lifecycleActionsState", "modelIdentityDisplayName",
            "taskThatOwnsThisObject", "detachedState", "pluginContext", "rootScript", "script"
        )
        val hideIfContains = setOf("Decorator", "Factory", "Handler", "Container", "Scope", "Broadcaster")

        return mapAsYaml(
            properties = project.properties,
            title = title,
            filter = { key ->
                key !in alwaysHide &&
                        !key.startsWith("org.gradle.") &&
                        hideIfContains.none { key.contains(it) }
            },
            includeTypeHints = false
        )
    }
}
