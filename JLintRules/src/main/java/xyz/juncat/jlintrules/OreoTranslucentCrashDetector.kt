package xyz.juncat.jlintrules

import com.android.SdkConstants
import com.android.tools.lint.detector.api.*
import com.android.utils.forEach
import org.w3c.dom.Element

@Suppress("UnstableApiUsage")
class OreoTranslucentCrashDetector : Detector(), XmlScanner {

    override fun getApplicableElements(): Collection<String> {
        return listOf(SdkConstants.TAG_ACTIVITY, SdkConstants.TAG_STYLE)
    }

    private val themeMapper = HashMap<ElementEntity, String>()

    override fun visitElement(context: XmlContext, element: Element) {
        when (element.tagName) {
            SdkConstants.TAG_ACTIVITY -> {
                if (hasOrientation(element)) {
                    val theme = element.getAttributeNS(
                        SdkConstants.ANDROID_URI,
                        SdkConstants.ATTR_THEME
                    )
                    if (theme.contains("Transparent")) {
                        reportError(context, element)
                    } else {
                        themeMapper[ElementEntity(context, element)] = theme.substringAfter('/')
                    }
                }
            }
            SdkConstants.TAG_STYLE -> {
                val style = element.getAttribute(SdkConstants.ATTR_NAME)
                themeMapper.forEach { (elementEntity, theme) ->
                    if (theme == style) {
                        if (isTranslucentOrFloating(element)) {
                            reportError(elementEntity.context, elementEntity.element)
                        }
                    } else if (element.hasAttribute(SdkConstants.ATTR_PARENT)) {
                        themeMapper[elementEntity] = element.getAttribute(SdkConstants.ATTR_PARENT)
                    }
                }
            }
        }
    }

    private fun isTranslucentOrFloating(element: Element): Boolean {
        element.childNodes.forEach { child ->
            if (child is Element
                && SdkConstants.TAG_ITEM == child.tagName
                && child.getFirstChild() != null
                && SdkConstants.VALUE_TRUE == child.getFirstChild()
                    .nodeValue
            ) {
                when (child.getAttribute(SdkConstants.ATTR_NAME)) {
                    "android:windowIsTranslucent",
                    "android:windowSwipeToDismiss",
                    "android:windowIsFloating" -> return true
                    else -> {
                    }
                }
            }
        }
        return "Theme.AppTheme.Transparent" == element.getAttribute(SdkConstants.ATTR_PARENT)
    }

    private fun hasOrientation(element: Element): Boolean {
        return try {
            return when (element.getAttributeNS(SdkConstants.ANDROID_URI, "screenOrientation")) {
                "landscape",
                "sensorLandscape",
                "reverseLandscape",
                "userLandscape",
                "portrait",
                "sensorPortrait",
                "reversePortrait",
                "userPortrait",
                "locked" ->
                    true
                else ->
                    false
            }
        } catch (e: Exception) {
            false
        }
    }

    private class ElementEntity(val context: XmlContext, val element: Element)

    private fun reportError(context: XmlContext, element: Element) {
        context.report(
            ISSUE,
            element,
            context.getLocation(element),
            "On API 28, setting screenOrientation and translucent together will throw Exception"
        )
    }

    companion object {
        val ISSUE: Issue = Issue.create(
            id = "OreoTranslucentCrash",
            briefDescription = "",
            explanation = "On API 28, setting screenOrientation and translucent together will throw Exception",
            category = Category.CORRECTNESS,
            priority = 1,
            severity = Severity.ERROR,
            implementation = Implementation(
                OreoTranslucentCrashDetector::class.java,
                Scope.ALL_RESOURCES_SCOPE
            )
        )
    }
}