package dev.jakubolszewski

object DemoArticles {
    const val targetId = "X"
    const val candidateA = "A"
    const val candidateB = "B"

    val articles: List<Article> = listOf(
        Article("X", "Information retrieval", "Ranking search evaluation"),
        Article("A", "Information retrieval systems", "Ranking search evaluation retrieval"),
        Article("B", "Information retrieval", "Classification learning evaluation prediction"),
        Article("C", "Networks graph", "Algorithms search"),
        Article("D", "Missing abstract", null),
        Article("E", "The and", "of the")
    )
}
