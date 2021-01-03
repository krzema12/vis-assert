package it.krzeminski.visassert.types.constraints

interface Constraint {
    val xValues: List<Float>

    fun assertMatches(function: (Float) -> Float)
}
