package it.krzeminski.testutils.plotassert.types.constraints

interface Constraint {
    val xValues: List<Float>

    fun assertMatches(function: (Float) -> Float)
}
