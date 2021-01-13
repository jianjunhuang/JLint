package xyz.juncat.jlintrules

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UExpression
import java.util.*


@Suppress("UnstableApiUsage")
class ToastUtilsDetector : Detector(), Detector.UastScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> {
        return listOf(UElement::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler {
        return object : UElementHandler() {
            override fun visitCallExpression(node: UCallExpression) {
                val resolvedName = node.classReference?.resolvedName
                val methodName = node.methodName
                println("resolvedName: $resolvedName, methodName:$methodName")
                context.report(
                    ISSUE,
                    node,
                    context.getNameLocation(node),
                    "resolvedName: $resolvedName, methodName:$methodName, ${node.receiver}"
                )
                if (resolvedName == "Toast" && methodName == "makeText") {
                    context.report(
                        ISSUE,
                        node,
                        context.getNameLocation(node),
                        "Use ToastUtils instead!"
                    )
                }
            }

            override fun visitExpression(node: UExpression) {
                context.report(
                    ISSUE,
                    node,
                    context.getNameLocation(node),
                    "${node.toString()}"
                )
            }

            override fun visitClass(node: UClass) {

            }
        }
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