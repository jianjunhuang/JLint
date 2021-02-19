package xyz.juncat.jlintrules

import com.android.tools.lint.detector.api.*
import com.intellij.psi.JavaElementVisitor
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiMethodCallExpression
import org.jetbrains.uast.UCallExpression

class LogDetector : Detector(), Detector.UastScanner {

    override fun getApplicableMethodNames(): List<String>? {
        return listOf("i", "d", "e", "w", "v")
    }

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        super.visitMethodCall(context, node, method)
        if (!context.evaluator.isMemberInClass(method, "android.util.Log")) {
            return
        }
        context.report(
            ISSUE,
            method,
            context.getCallLocation(node, includeReceiver = true, includeArguments = true),
            "Use HLog instead of Log"
        )
    }

    override fun visitMethod(
        context: JavaContext,
        visitor: JavaElementVisitor?,
        call: PsiMethodCallExpression,
        method: PsiMethod
    ) {
        super.visitMethod(context, visitor, call, method)
    }

    companion object {
        val ISSUE: Issue = Issue.create(
            id = "LogChecker",
            briefDescription = "Check the use of Log.",
            explanation = "Use HLog instead of Log.",
            category = Category.CORRECTNESS,
            priority = 1,
            severity = Severity.WARNING,
            implementation = Implementation(
                LogDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }

}