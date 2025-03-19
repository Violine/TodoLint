package ru.rustore.todolint.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class TodoLintPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create(
            "todoLint",
            TodoLintSettings::class.java
        )
        project.beforeEvaluate {
            val a = extension.taskRegexp
            TodoLintPluginSettingsRepository.defaultRegexp = a
        }
    }
}