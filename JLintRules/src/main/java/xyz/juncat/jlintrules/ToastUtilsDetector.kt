package xyz.juncat.jlintrules

import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression


@Suppress("UnstableApiUsage")
class ToastUtilsDetector : Detector(), Detector.UastScanner {

    override fun getApplicableMethodNames(): List<String>? {
        return listOf("makeText", "show")
    }

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        if (!context.evaluator.isMemberInClass(method, "android.widget.Toast")) {
            return
        }

        val args = node.valueArguments

        var fix: LintFix? = null
        if (args.size == 3) {
            fix = LintFix.create()
                .name("replace to ToastUtils.show(${args[1].asSourceString()})")
                .replace()
                .with("ToastUtils.show(${args[1].asSourceString()})")
                .build()
        }

        context.report(
            ISSUE, method, context.getCallLocation(
                node,
                includeReceiver = true,
                includeArguments = true
            ), "Use ToastUtils instead !", fix
        )

    }

    companion object {
        val ISSUE: Issue = Issue.create(
            id = "ToastChecker",
            briefDescription = "Check the use of Toast",
            explanation = "Check the use of Toast, use ToastUtils instead",
            category = Category.CORRECTNESS,
            priority = 1,
            severity = Severity.WARNING,
            implementation = Implementation(
                ToastUtilsDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }
}