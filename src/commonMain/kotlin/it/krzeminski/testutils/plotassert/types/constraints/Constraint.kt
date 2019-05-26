package it.krzeminski.testutils.plotassert.types.constraints

interface Constraint {
    val x: Float

    fun assertMatches(function: (Float) -> Float)
}
