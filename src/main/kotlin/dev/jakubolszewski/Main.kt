package dev.jakubolszewski

fun main() {
    val articles = DemoArticles.articles
    val targetId = DemoArticles.targetId
    val engine = ArticleSimilarity(articles)
    val ranking = engine.rank(targetId)
    val comparison = engine.compare(targetId, DemoArticles.candidateA, DemoArticles.candidateB)

    ConsoleReport.print(targetId, articles, ranking, comparison)
}
