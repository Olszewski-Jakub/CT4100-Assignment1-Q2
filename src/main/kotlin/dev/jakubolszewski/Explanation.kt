package dev.jakubolszewski

import java.util.Locale

object Explanation {
    fun number(value: Double): String = String.format(Locale.ROOT, "%.4f", value)
    private fun words(values: Set<String>): String = values.sorted().joinToString(", ").ifEmpty { "(none)" }

    private fun field(label: String, result: FieldSimilarity): String = with(result) {
        "$label Jaccard: $intersectionCount/$unionCount = ${number(similarity)}.\n" +
            "Shared: ${words(sharedWords)}\n" +
            "Target only: ${words(targetOnlyWords)}\n" +
            "Candidate only: ${words(candidateOnlyWords)}"
    }

    fun describe(result: SimilarityResult): String = with(result) {
        "$candidateId compared with $targetId:\n" +
            "${field("Title", title)}\n${field("Abstract", abstract)}\n" +
            "Score: 0.5 × ${number(title.similarity)} + 0.5 × ${number(abstract.similarity)} " +
            "= ${number(similarity)}"
    }

    fun compare(comparison: CandidateComparison): String = with(comparison) {
        val outcome = winnerId?.let { "$it ranks higher by word overlap." } ?: "The candidates tie."
        "${describe(a)}\n\n${describe(b)}\n\n" +
            "Weighted title difference (${a.candidateId} - ${b.candidateId}): ${number(titleDifference)}\n" +
            "Weighted abstract difference (${a.candidateId} - ${b.candidateId}): ${number(abstractDifference)}\n" +
            "Positive contributions favour ${a.candidateId}; negative contributions favour ${b.candidateId}.\n" +
            "Score difference (${a.candidateId} - ${b.candidateId}): ${number(scoreDifference)}. $outcome"
    }
}
