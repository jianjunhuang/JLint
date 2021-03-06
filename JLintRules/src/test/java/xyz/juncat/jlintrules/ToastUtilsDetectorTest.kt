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
     package xyz.juncat.jlint

    import android.os.Bundle
    import android.util.Log
    import android.widget.Toast
    import androidx.appcompat.app.AppCompatActivity
    
    class MainActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)
    
            Toast.makeText(this, "test", Toast.LENGTH_SHORT).show()
    
            Log.i("TAG", "onCreate: ")
        }
    }
    """.trimIndent()

    private val correctMethodCallKt = """
        package xyz.juncat.jlint

        class Test {
            fun test() {
                ToastUtils.show("test")
            }
        } 
    """.trimIndent()

    override fun getDetector(): Detector = ToastUtilsDetector()

    override fun getIssues(): MutableList<Issue> = mutableListOf(ToastUtilsDetector.ISSUE)

    @Test
    fun testInCorrectToastCall() {
        lint().requireCompileSdk()
            .sdkHome(File(sdkDir))
            .files(kotlin(inCorrectMethodCallKt).indented())
            .run()
            .expectWarningCount(1)
    }

    @Test
    fun testCorrectToastCall() {
        lint().requireCompileSdk()
            .sdkHome(File(sdkDir))
            .files(kotlin(correctMethodCallKt).indented())
            .run()
            .expectClean()
    }

}