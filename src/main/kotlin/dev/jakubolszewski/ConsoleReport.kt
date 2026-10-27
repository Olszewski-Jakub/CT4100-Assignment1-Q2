package dev.jakubolszewski

object ConsoleReport {
    fun print(
        targetId: String,
        articles: List<Article>,
        ranking: Ranking,
        comparison: CandidateComparison
    ) {
        ranking.exclusions.forEach { println("Excluded ${it.articleId}: ${it.reason}") }
        printRanking(targetId, ranking)
        println("\nCandidate comparison:\n${Explanation.compare(comparison)}")
        printOriginalText(articles, targetId, ranking.results)
        println(
            "Scores measure distinct word overlap only; they do not establish " +
                "shared methods, conclusions, or scientific equivalence."
        )
    }

    private fun printRanking(targetId: String, ranking: Ranking) {
        println("\nRecommendations for $targetId:")
        if (ranking.results.isEmpty()) {
            println("No eligible candidates.")
            return
        }

        val scoreCounts = ranking.results.groupingBy { it.similarity }.eachCount()
        var rank = 0
        ranking.results.forEachIndexed { index, result ->
            if (index == 0 || result.similarity != ranking.results[index - 1].similarity) {
                rank = index + 1
            }
            val tieLabel = if (scoreCounts.getValue(result.similarity) > 1) {
                " (tied score; ordered by ID)"
            } else {
                ""
            }
            println("\n$rank. ${result.candidateId}$tieLabel")
            println(Explanation.describe(result))
        }
    }

    private fun highlight(text: String, matches: Set<String>): String =
        Regex("[\\p{L}\\p{N}]+").replace(text) { match ->
            if (match.value.lowercase(java.util.Locale.ROOT) in matches) "[${match.value}]" else match.value
        }

    private fun printOriginalText(articles: List<Article>, targetId: String, results: List<SimilarityResult>) {
        println("\nOriginal article text:")
        println("Matching words are enclosed in [brackets] relative to $targetId.")
        articles.forEach { article ->
            val relevant = results.filter { article.id == it.targetId || article.id == it.candidateId }
            val titleMatches = relevant.flatMap { it.title.sharedWords }.toSet()
            val abstractMatches = relevant.flatMap { it.abstract.sharedWords }.toSet()
            println(article.id)
            println("Title: ${highlight(article.title.orEmpty(), titleMatches)}")
            println("Abstract: ${highlight(article.abstract.orEmpty(), abstractMatches)}")
            println()
        }
    }
}
