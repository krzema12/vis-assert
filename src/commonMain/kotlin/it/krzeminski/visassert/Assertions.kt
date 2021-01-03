package it.krzeminski.visassert

import it.krzeminski.visassert.exceptions.FailedConstraintException

fun assertFunctionConformsTo(
    functionUnderTest: (Float) -> Float,
    visualisation: PlotConstraintsBuilder.() -> Unit
) {
    assertFunctionConformsTo(
        functionUnderTest = functionUnderTest,
        samplesPerCharacter = 1,
        visualisation = visualisation
    )
}

fun assertFunctionConformsTo(
    functionUnderTest: (Float) -> Float,
    samplesPerCharacter: Int,
    visualisation: PlotConstraintsBuilder.() -> Unit
) {
    val rawVisualisation = readRawVisualisation(visualisation)
    val constraints = rawVisualisation.toConstraints(samplesPerCharacter)
    val functionUnderTestWithExceptionInterception = { x: Float ->
        try {
            functionUnderTest(x)
        } catch (e: Exception) {
            throw FailedConstraintException("For x=$x: the function throws an exception!", e)
        }
    }
    constraints.map { constraint ->
        constraint.assertMatches(functionUnderTestWithExceptionInterception)
    }
}
