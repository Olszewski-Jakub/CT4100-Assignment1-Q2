package dev.jakubolszewski

data class CandidateComparison(val a: SimilarityResult, val b: SimilarityResult) {
    val titleDifference: Double get() = a.titleContribution - b.titleContribution
    val abstractDifference: Double get() = a.abstractContribution - b.abstractContribution
    val scoreDifference: Double get() = a.similarity - b.similarity
    val winnerId: String? get() = when {
        scoreDifference > 0 -> a.candidateId
        scoreDifference < 0 -> b.candidateId
        else -> null
    }
}
