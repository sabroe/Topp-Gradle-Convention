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
 * Git-properties convention plugin.
 *
 * @author Morten Sabroe Mortensen
 * @version 1.0
 * @since 2025-12-16
 */

import com.gorylenko.GitPropertiesPluginExtension
import org.gradle.api.plugins.JavaPlugin

plugins {
    id("java") apply false
    id("com.gorylenko.gradle-git-properties") apply false
}

val conventionName = "git-properties"

project.logger.info("Convention ${conventionName} loaded.")

project.plugins.withType<JavaPlugin> {
    val enablePropertyName = "convention.${conventionName}.enable"
    val enable = project.findProperty(enablePropertyName)?.toString()?.trim()?.toBooleanStrictOrNull()?:true
    if (!enable) {
        project.logger.debug("Convention ${conventionName} disabled.")
    } else {
        project.logger.debug("Convention ${conventionName} enabled.")

        apply(plugin = "com.gorylenko.gradle-git-properties")

        extensions.configure<GitPropertiesPluginExtension> {
            gitPropertiesName = "git.properties"
            failOnNoGitDirectory = false

            keys = listOf(
                "git.branch",
                "git.build.host",
                "git.build.user.email",
                "git.build.user.name",
                "git.build.version",
                "git.commit.id",
                "git.commit.time",
                "git.dirty",
                "git.remote.origin.url",
                "git.tags"
            )
        }
    }
}

/*
 * Note:
 *     To inspect the content of build Java archives, consider this command for ad hoc inspection:
 *     find . -name *.jar | grep -E "(.*)/build/libs/[^/]*-[0-9]+[.][0-9]+[.][0-9]+(-SNAPSHOT)?[.]jar" | while read r; do echo; echo $r; unzip -p $r git.properties; done
 */
