package dev.jakubolszewski

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ArticleSimilarityTest {
    private fun engine() = ArticleSimilarity(listOf(
        Article("X", "alpha beta", "gamma"),
        Article("B", "alpha delta", "epsilon"),
        Article("A", "alpha beta", "delta"),
        Article("C", "beta alpha", "delta"),
        Article("blank", " ", "text"),
        Article("missing", "text", null),
        Article("empty", "the", "and")
    ))

    @Test
    fun `score contains complete evidence and floating point calculation`() {
        val result = engine().score("X", "A")
        assertEquals(0.5, result.similarity)
        assertEquals(setOf("alpha", "beta"), result.title.sharedWords)
        assertEquals(setOf("gamma"), result.abstract.targetOnlyWords)
        assertEquals(setOf("delta"), result.abstract.candidateOnlyWords)
        assertEquals(2, result.title.intersectionCount)
        assertEquals(2, result.title.unionCount)
        assertEquals(1.0 / 6, engine().score("X", "B").similarity)
        assertTrue(Explanation.describe(result).contains("2/2 = 1.0000"))
    }

    @Test
    fun `ranking excludes target and invalid articles with stable ties`() {
        val ranking = engine().rank("X")
        assertEquals(listOf("A", "C", "B"), ranking.results.map { it.candidateId })
        assertEquals(listOf("blank", "missing", "empty"), ranking.exclusions.map { it.articleId })
        assertTrue(ranking.exclusions.last().reason.startsWith("insufficient text"))
        assertFailsWith<IllegalArgumentException> { engine().rank("empty") }
        assertFailsWith<IllegalArgumentException> { engine().rank("missing") }
        assertFailsWith<IllegalArgumentException> { engine().rank("unknown") }
    }

    @Test
    fun `comparison reports winner signed difference and ties`() {
        val comparison = engine().compare("X", "A", "B")
        assertEquals("A", comparison.winnerId)
        assertEquals(1.0 / 3, comparison.scoreDifference, 1e-15)
        assertEquals("A", engine().compare("X", "B", "A").winnerId)
        val tie = engine().compare("X", "A", "C")
        assertNull(tie.winnerId)
        assertTrue(Explanation.compare(tie).contains("The candidates tie."))
    }

    @Test
    fun `identical and disjoint word sets give boundary scores`() {
        val engine = ArticleSimilarity(listOf(
            Article("X", "alpha", "beta"), Article("A", "alpha alpha", "beta beta"),
            Article("B", "gamma", "delta")
        ))
        assertEquals(1.0, engine.score("X", "A").similarity)
        assertEquals(0.0, engine.score("X", "B").similarity)
        assertFailsWith<IllegalArgumentException> { engine.score("X", "X") }
        assertFailsWith<IllegalArgumentException> { engine.compare("X", "A", "A") }
    }

    @Test
    fun `rounding does not determine ranking or ties`() {
        val words = (1..50000).joinToString(" ") { "word$it" }
        val engine = ArticleSimilarity(listOf(
            Article("X", "shared", words), Article("A", "shared", "extra"),
            Article("B", "shared", "extra word1")
        ))
        val comparison = engine.compare("X", "A", "B")
        assertEquals(Explanation.number(comparison.a.similarity), Explanation.number(comparison.b.similarity))
        assertEquals("B", comparison.winnerId)
        assertTrue(comparison.scoreDifference < 0)
    }

    @Test
    fun `worked example separates title and abstract contributions`() {
        val engine = ArticleSimilarity(DemoArticles.articles)
        val comparison = engine.compare("X", "A", "B")
        assertEquals(2.0 / 3, comparison.a.title.similarity)
        assertEquals(3.0 / 4, comparison.a.abstract.similarity)
        assertEquals(17.0 / 24, comparison.a.similarity, 1e-15)
        assertEquals(7.0 / 12, comparison.b.similarity, 1e-15)
        assertEquals(0.125, comparison.scoreDifference, 1e-15)
        assertEquals(-1.0 / 6, comparison.titleDifference, 1e-15)
        assertEquals(7.0 / 24, comparison.abstractDifference, 1e-15)
        assertTrue(Explanation.compare(comparison).contains("-0.1667"))
        assertTrue(Explanation.compare(comparison).contains("0.2917"))
    }

    @Test
    fun `cross field overlap does not increase similarity`() {
        val engine = ArticleSimilarity(listOf(
            Article("X", "alpha", "beta"), Article("A", "beta", "alpha")
        ))
        assertEquals(0.0, engine.score("X", "A").similarity)
    }

    @Test
    fun `either empty processed field excludes article`() {
        val engine = ArticleSimilarity(listOf(
            Article("X", "alpha", "beta"), Article("A", "the", "alpha"),
            Article("B", "alpha", "and")
        ))
        assertEquals(listOf("A", "B"), engine.rank("X").exclusions.map { it.articleId })
        assertTrue(engine.rank("X").results.isEmpty())
    }

    @Test
    fun `duplicate or blank article IDs are rejected`() {
        assertFailsWith<IllegalArgumentException> {
            ArticleSimilarity(listOf(Article("A", "a", "b"), Article("A", "c", "d")))
        }
        assertFailsWith<IllegalArgumentException> { ArticleSimilarity(listOf(Article(" ", "a", "b"))) }
    }
}
