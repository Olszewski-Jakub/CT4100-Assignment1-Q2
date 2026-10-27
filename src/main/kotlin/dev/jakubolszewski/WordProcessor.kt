package dev.jakubolszewski

import java.util.Locale

object WordProcessor {
    val stopWords: Set<String> = checkNotNull(
        WordProcessor::class.java.getResourceAsStream("/stopwords-onix.txt")
    ) { "Missing bundled stop-word list: stopwords-onix.txt" }
        .bufferedReader(Charsets.UTF_8)
        .use { reader -> reader.readLines().filter { it.isNotBlank() }.toSet() }
    private val word = Regex("[\\p{L}\\p{N}]+")

    fun words(text: String): Set<String> = word.findAll(text.lowercase(Locale.ROOT))
        .map { it.value }.filterNot { it in stopWords }.toSortedSet()
}
