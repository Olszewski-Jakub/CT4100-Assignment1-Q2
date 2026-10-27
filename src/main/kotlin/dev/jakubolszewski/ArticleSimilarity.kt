package dev.jakubolszewski

class ArticleSimilarity(articles: List<Article>) {
    val articles: List<Article> = articles.toList()

    init {
        require(this.articles.all { it.id.isNotBlank() }) { "Article IDs must not be blank" }
        require(this.articles.map { it.id }.distinct().size == this.articles.size) {
            "Article IDs must be unique"
        }
    }

    private data class ProcessedArticle(val title: Set<String>, val abstract: Set<String>)

    private val processed = this.articles.associate { article ->
        article.id to ProcessedArticle(
            WordProcessor.words(article.title.orEmpty()),
            WordProcessor.words(article.abstract.orEmpty())
        )
    }
    val exclusions: List<Exclusion> = this.articles.mapNotNull { article ->
        when {
            article.title.isNullOrBlank() || article.abstract.isNullOrBlank() ->
                Exclusion(article.id, "insufficient text: title and abstract are both required")
            processed.getValue(article.id).let { it.title.isEmpty() || it.abstract.isEmpty() } ->
                Exclusion(article.id, "insufficient text: title and abstract must each retain words after preprocessing")
            else -> null
        }
    }
    private val exclusionsById = exclusions.associateBy { it.articleId }

    private fun validWords(id: String): ProcessedArticle {
        val words = requireNotNull(processed[id]) { "Unknown article ID: $id" }
        val exclusion = exclusionsById[id]
        require(exclusion == null) {
            "$id: ${exclusion?.reason}"
        }
        return words
    }

    fun score(targetId: String, candidateId: String): SimilarityResult {
        require(targetId != candidateId) { "Target and candidate must be different articles" }
        val target = validWords(targetId)
        val candidate = validWords(candidateId)
        return SimilarityResult(
            targetId = targetId,
            candidateId = candidateId,
            title = fieldScore(target.title, candidate.title),
            abstract = fieldScore(target.abstract, candidate.abstract)
        )
    }

    private fun fieldScore(target: Set<String>, candidate: Set<String>) = FieldSimilarity(
        sharedWords = (target intersect candidate).toSortedSet(),
        targetOnlyWords = (target - candidate).toSortedSet(),
        candidateOnlyWords = (candidate - target).toSortedSet(),
        unionCount = (target union candidate).size
    )

    fun rank(targetId: String): Ranking {
        validWords(targetId)
        val results = articles.filter { it.id != targetId && it.id !in exclusionsById }
            .map { score(targetId, it.id) }
            .sortedWith(compareByDescending<SimilarityResult> { it.similarity }.thenBy { it.candidateId })
        return Ranking(results, exclusions)
    }

    fun compare(targetId: String, candidateA: String, candidateB: String): CandidateComparison {
        require(candidateA != candidateB) { "Choose two different candidates" }
        return CandidateComparison(score(targetId, candidateA), score(targetId, candidateB))
    }
}
