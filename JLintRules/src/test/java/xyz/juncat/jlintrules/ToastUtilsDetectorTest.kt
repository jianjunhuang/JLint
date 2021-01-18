package xyz.juncat.jlintrules

import com.android.tools.lint.checks.infrastructure.LintDetectorTest
import com.android.tools.lint.checks.infrastructure.TestLintTask
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Issue
import org.junit.Test
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.*

@Suppress("UnstableApiUsage")
class ToastUtilsDetectorTest : LintDetectorTest() {

    private var sdkDir = ""


    override fun setUp() {
        super.setUp()
        val p = Properties(System.getProperties())
        p.load(FileInputStream("../local.properties"))
        sdkDir = p.getProperty("sdk.dir")
    }


    private val inCorrectMethodCallKt = """
        import android.os.Bundle
        import android.widget.Toast
        import androidx.appcompat.app.AppCompatActivity
        
        class MainActivity : AppCompatActivity() {
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_main)
                Toast.makeText(this, "test", Toast.LENGTH_SHORT).show()
        
            }
        }
    """.trimIndent()

    private val correctMethodCallKt = """
        import android.widget.Toast
       
        class Test {
            fun main(args: Array<String>) {
                ToastUtils.show("test")
            }
        } 
    """.trimIndent()

    override fun getDetector(): Detector = ToastUtilsDetector()

    override fun getIssues(): MutableList<Issue> = mutableListOf(ToastUtilsDetector.ISSUE)

    override fun lint(): TestLintTask {
        return TestLintTask.lint().apply {
            detector(detector)
            issues(*issues.toTypedArray())
            sdkHome(File(sdkDir))
            requireCompileSdk(true)
        }
    }

    @Test
    fun testInCorrectToastCall() {
        lint().sdkHome(File(sdkDir)).issues(ToastUtilsDetector.ISSUE).files(kotlin(inCorrectMethodCallKt)).run().expectWarningCount(1)
    }

    @Test
    fun testCorrectToastCall() {
        lint().sdkHome(File(sdkDir)).files(kotlin(correctMethodCallKt)).run().expectClean()
    }

}