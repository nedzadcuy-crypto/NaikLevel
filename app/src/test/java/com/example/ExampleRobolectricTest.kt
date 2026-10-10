package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.aistudio.naiklevel.bmedkq.R
import com.example.data.model.ExamBank
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string resources and verify mandatory branding and attribution`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        val tagline = context.getString(R.string.tagline)
        val attribution = context.getString(R.string.attribution)

        assertEquals("NaikLevel", appName)
        assertEquals("Belajar Modern", tagline)
        assertEquals("Created by Nedzad Elvedino Ariesza", attribution)
    }

    @Test
    fun `verify exam question bank covers SD SMP SMA SMK`() {
        val sdQuestions = ExamBank.getQuestionsByLevel("SD")
        val smpQuestions = ExamBank.getQuestionsByLevel("SMP")
        val smaQuestions = ExamBank.getQuestionsByLevel("SMA")
        val smkQuestions = ExamBank.getQuestionsByLevel("SMK")

        assertTrue("SD should have questions", sdQuestions.isNotEmpty())
        assertTrue("SMP should have questions", smpQuestions.isNotEmpty())
        assertTrue("SMA should have questions", smaQuestions.isNotEmpty())
        assertTrue("SMK should have questions", smkQuestions.isNotEmpty())
    }

    @Test
    fun `launch MainActivity and verify it starts successfully`() {
        val controller = org.robolectric.Robolectric.buildActivity(MainActivity::class.java)
        controller.setup()
        org.junit.Assert.assertNotNull(controller.get())
    }
}
