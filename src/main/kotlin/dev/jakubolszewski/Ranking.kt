package dev.jakubolszewski

data class Exclusion(val articleId: String, val reason: String)

data class Ranking(val results: List<SimilarityResult>, val exclusions: List<Exclusion>)
