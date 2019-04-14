package it.krzeminski.testutils.plotassert

fun assertFunctionConformsTo(
    functionUnderTest: (Float) -> Float,
    visualisation: PlotConstraintsBuilder.() -> Unit
) {
    assertFunctionConformsTo(
        functionUnderTest = functionUnderTest,
        samplesPerCharacter = 1,
        visualisation = visualisation)
}

fun assertFunctionConformsTo(
    functionUnderTest: (Float) -> Float,
    samplesPerCharacter: Int,
    visualisation: PlotConstraintsBuilder.() -> Unit
) {
    val rawVisualisation = readRawVisualisation(visualisation)
    val constraints = rawVisualisation.toConstraints(samplesPerCharacter)
    constraints.map { constraint ->
        constraint.assertMatches(functionUnderTest)
    }
}
