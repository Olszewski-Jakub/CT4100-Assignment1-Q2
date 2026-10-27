package dev.jakubolszewski

data class FieldSimilarity(
    val sharedWords: Set<String>,
    val targetOnlyWords: Set<String>,
    val candidateOnlyWords: Set<String>,
    val unionCount: Int
) {
    val intersectionCount: Int get() = sharedWords.size
    val similarity: Double get() = intersectionCount.toDouble() / unionCount
}

data class SimilarityResult(
    val targetId: String,
    val candidateId: String,
    val title: FieldSimilarity,
    val abstract: FieldSimilarity
) {
    val titleContribution: Double get() = 0.5 * title.similarity
    val abstractContribution: Double get() = 0.5 * abstract.similarity
    val similarity: Double get() = titleContribution + abstractContribution
}
