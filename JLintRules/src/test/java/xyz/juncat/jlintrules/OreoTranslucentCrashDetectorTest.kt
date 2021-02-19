package xyz.juncat.jlintrules

import com.android.tools.lint.checks.infrastructure.LintDetectorTest
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Issue

class OreoTranslucentCrashDetectorTest : LintDetectorTest() {
    override fun getDetector(): Detector = OreoTranslucentCrashDetector()

    override fun getIssues(): MutableList<Issue> = mutableListOf(OreoTranslucentCrashDetector.ISSUE)
}