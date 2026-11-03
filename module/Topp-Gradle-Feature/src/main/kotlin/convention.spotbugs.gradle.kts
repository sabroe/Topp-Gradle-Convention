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
 * "SpotBugs" convention plugin.
 *
 * @author Morten Sabroe Mortensen
 * @version 1.0
 * @since 2026-11-03
 */

import com.github.spotbugs.snom.SpotBugsExtension
import com.github.spotbugs.snom.SpotBugsTask
import org.gradle.api.plugins.JavaPlugin
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.withType

plugins {
    id("java")
    //Note: 'com.github.spotbugs' is not applied here, but below, only when 'convention.spotbugs.enable' is true.
    //      Applied here, its tasks would always run, also with the convention switched off.
}

val conventionName = "spotbugs"

project.logger.info("Convention ${conventionName} loaded.")

project.plugins.withType<JavaPlugin> {
    val enablePropertyName = "convention.${conventionName}.enable"
    val enable = project.findProperty(enablePropertyName)?.toString()?.trim()?.toBooleanStrictOrNull()?:true
    if (!enable) {
        project.logger.debug("Convention ${conventionName} disabled.")
    } else {
        project.logger.debug("Convention ${conventionName} enabled.")

        //Note: The full plugin, not 'com.github.spotbugs-base'. Only the full plugin adds the tasks
        //      'spotbugsMain' and 'spotbugsTest' for the source sets, and makes 'check' depend on them.
        apply(plugin = "com.github.spotbugs")  //Reference: https://plugins.gradle.org/plugin/com.github.spotbugs

        extensions.configure<SpotBugsExtension> {
            toolVersion.set("4.10.5")  //See https://github.com/spotbugs/spotbugs/releases
            ignoreFailures.set(true)  //Note: Findings are reported, but don't fail the build.
            showStackTraces.set(true)
            showProgress.set(false)  //Note: 'true' writes one progress line of about 30 KB per task to the build log.
//            effort.set(com.github.spotbugs.snom.Effort.MAX)
        }

        tasks.withType<SpotBugsTask>().configureEach {
            reports.create("html") {
                required.set(true)
            }
        }
    }
}
