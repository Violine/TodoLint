package ru.rustore.todolint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue

class CustomIssueRegistry : IssueRegistry() {
    override val issues: List<Issue>
        get() = listOf(
            IncorrectTodoDetector.ISSUE,
        )

    override val vendor: Vendor = VENDOR

    override val api: Int = CURRENT_API

    private companion object {
        val VENDOR = Vendor(vendorName = "Rustore")
    }
}