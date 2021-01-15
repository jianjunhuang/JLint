package xyz.juncat.jlintrules

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue

@Suppress("UnstableApiUsage")
class JIssueRegistry : IssueRegistry() {
    override val issues: List<Issue>
        get() = arrayListOf(
            ToastUtilsDetector.ISSUE,
            OreoTranslucentCrashDetector.ISSUE
        )

    override val api: Int
        get() = CURRENT_API
}