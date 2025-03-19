package ru.rustore.todolint

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.LintFix
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import org.jetbrains.uast.UComment
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UFile
import ru.rustore.todolint.plugin.TodoLintPluginSettingsRepository

class IncorrectTodoDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> =
        listOf(UFile::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler =
        TodoMessageScanner(context)

    companion object {
        val ISSUE: Issue = Issue.create(
            id = "IncorrectTodo",
            briefDescription = "Detects TODOs JIRA task",
            explanation = "TODOs should be in the format TODO [JIRA_TASK] <comment>",
            category = Category.CORRECTNESS,
            priority = 3,
            severity = Severity.ERROR,
            implementation = Implementation(
                IncorrectTodoDetector::class.java,
                Scope.JAVA_FILE_SCOPE,
            ),
        )
    }
}

private class TodoMessageScanner(private val context: JavaContext) : UElementHandler() {

    val oldPattern = Regex("TODO|todo")
    val replacementText = "TODO RUSTORE-"

    val quickfixData = LintFix.create()
        .name("Create JIRA task for this TODO")
        .replace()
        .pattern(oldPattern.pattern)
        .with(replacementText)
        .robot(true)
        .independent(true)
        .build()

    override fun visitFile(node: UFile) {
        val comments = node.allCommentsInFile
        comments.forEach { comment ->
            val text = comment.text
            if (text.contains("TODO", ignoreCase = true) && !isValidComment(text)) {
                sendReport(context, comment)
            }
        }
    }

    private fun sendReport(context: JavaContext, comment: UComment) {
        context.report(
            issue = IncorrectTodoDetector.ISSUE,
            location = context.getLocation(comment),
            message = "Need to create JIRA task for this TODO",
            quickfixData = quickfixData,
        )
    }

    private fun isValidComment(commentText: String): Boolean {
        println("OLOLO" + TodoLintPluginSettingsRepository.defaultRegexp)
        return Regex(TodoLintPluginSettingsRepository.defaultRegexp).containsMatchIn(commentText)
    }
    //       Regex(".*(RUSTORE|RCORE|SDK|CHEPUSHER|RM|PI)-\\d{1,5}.*").containsMatchIn(commentText)
}