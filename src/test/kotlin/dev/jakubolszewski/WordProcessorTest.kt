package dev.jakubolszewski

import java.util.Locale
import kotlin.test.Test
import kotlin.test.assertEquals

class WordProcessorTest {
    @Test
    fun `bundled Lextek list removes its words without removing subject terms`() {
        assertEquals(423, WordProcessor.stopWords.size)
        assertEquals(emptySet(), WordProcessor.words(WordProcessor.stopWords.joinToString(" ")))
        assertEquals(
            setOf("graph", "networks", "search"),
            WordProcessor.words("A different WORK about graph networks search across YEARS")
        )
    }

    @Test
    fun `preprocessing uses distinct lowercase tokens and fixed stop words`() {
        val previous = Locale.getDefault()
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"))
            assertEquals(setOf("image", "graph", "based", "café", "42"),
                WordProcessor.words("IMAGE image, the GRAPH-based café! 42"))
        } finally {
            Locale.setDefault(previous)
        }
    }
}
