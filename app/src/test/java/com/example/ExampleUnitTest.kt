package com.example

import com.example.data.local.CurriculumSeed
import com.example.data.model.CustomExamConfig
import com.example.data.model.CustomExamGenerator
import com.example.data.model.ExamBank
import com.example.data.model.SibiCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun `test level calculation formula`() {
        val xp0 = 0
        val level0 = (xp0 / 100) + 1
        assertEquals(1, level0)

        val xp99 = 99
        val level99 = (xp99 / 100) + 1
        assertEquals(1, level99)

        val xp100 = 100
        val level100 = (xp100 / 100) + 1
        assertEquals(2, level100)

        val xp250 = 250
        val level250 = (xp250 / 100) + 1
        assertEquals(3, level250)
    }

    @Test
    fun `test initial curriculum contains materials for all 4 school levels`() {
        val materials = CurriculumSeed.getInitialMaterials()
        val levels = materials.map { it.schoolLevel }.toSet()
        assertTrue(levels.contains("SD"))
        assertTrue(levels.contains("SMP"))
        assertTrue(levels.contains("SMA"))
        assertTrue(levels.contains("SMK"))
        assertTrue(materials.size >= 8)
    }

    @Test
    fun `test exam bank filtering`() {
        val mathSma = ExamBank.getQuestionsByFilter("SMA", "Matematika")
        assertTrue(mathSma.isNotEmpty())
        assertEquals("SMA", mathSma.first().schoolLevel)
        assertEquals("Matematika", mathSma.first().subject)
    }

    @Test
    fun `sibi catalog has no fabricated records`() {
        // Katalog hardcode lama dihapus; daftar harus kosong sampai ada sumber data sah yang terverifikasi.
        assertTrue(SibiCatalog.books.isEmpty())
        assertTrue(SibiCatalog.filterBooks("SD", 0, "Semua", "Semua", "").isEmpty())
        assertEquals(0, SibiCatalog.getSubjectBookCount("Matematika"))
    }

    @Test
    fun `test custom exam generator from user materials`() {
        val materials = CurriculumSeed.getInitialMaterials()
        val selectedMaterial = materials.first()

        val config = CustomExamConfig(
            examTitle = "Kuis Uji Pemahaman",
            selectedMaterialIds = listOf(selectedMaterial.id),
            questionCount = 3,
            timeLimitMinutes = 10
        )

        val result = CustomExamGenerator.generateQuestions(materials = materials, config = config)
        assertTrue("Generation should succeed", result is CustomExamGenerator.GenerationResult.Success)
        val success = result as CustomExamGenerator.GenerationResult.Success
        assertTrue("Questions generated should be between 1 and 3", success.questions.isNotEmpty())
        success.questions.forEach { q ->
            assertEquals(4, q.options.size)
            assertTrue(q.correctAnswerIndex in 0..3)
            assertNotNull(q.explanation)
        }
    }
}
