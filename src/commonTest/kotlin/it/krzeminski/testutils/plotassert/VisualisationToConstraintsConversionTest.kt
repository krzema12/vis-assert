package it.krzeminski.testutils.plotassert

import it.krzeminski.testutils.plotassert.types.RawVisualisation
import it.krzeminski.testutils.plotassert.types.RawXAxis
import it.krzeminski.testutils.plotassert.types.VisualisationRow
import it.krzeminski.testutils.plotassert.types.constraints.Constraint
import it.krzeminski.testutils.plotassert.types.constraints.ExactValueConstraint
import it.krzeminski.testutils.plotassert.types.constraints.VerticalRangeConstraint
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/* ktlint-disable no-multi-spaces paren-spacing */

class VisualisationToConstraintsConversionTest {
    @Test
    fun simpleLinearFunction() {
        assertEquals(
                actual = RawVisualisation(
                        visualisationRows = listOf(
                                VisualisationRow("  I", 4.0f),
                                VisualisationRow(" XI"),
                                VisualisationRow("X  ", 2.0f)
                        ),
                        xAxis = RawXAxis(
                                markers =        "| |",
                                values = listOf(-1.0f, 1.0f)))
                        .toConstraints(),
                expected = listOf(
                        Constraint(x = -1.0f, yValueConstraint = ExactValueConstraint(y = 2.0f)),
                        Constraint(x = 0.0f, yValueConstraint = ExactValueConstraint(y = 3.0f)),
                        Constraint(x = 1.0f, yValueConstraint = VerticalRangeConstraint(minY = 2.5f, maxY = 4.5f))
                )
        )
    }

    @Test
    fun simpleLinearFunctionForMultipleSamplesPerCharacter() {
        assertEquals(
            actual = RawVisualisation(
                visualisationRows = listOf(
                    VisualisationRow("  I", 4.0f),
                    VisualisationRow(" XI"),
                    VisualisationRow("X  ", 2.0f)
                ),
                xAxis = RawXAxis(
                    markers =        "| |",
                    values = listOf(-1.0f, 1.0f)))
                .toConstraints(samplesPerCharacter = 4),
            expected = listOf(
                Constraint(x = -1.375f, yValueConstraint = ExactValueConstraint(y = 2.0f)),
                Constraint(x = -1.125f, yValueConstraint = ExactValueConstraint(y = 2.0f)),
                Constraint(x = -0.875f, yValueConstraint = ExactValueConstraint(y = 2.0f)),
                Constraint(x = -0.625f, yValueConstraint = ExactValueConstraint(y = 2.0f)),

                Constraint(x = -0.375f, yValueConstraint = ExactValueConstraint(y = 3.0f)),
                Constraint(x = -0.125f, yValueConstraint = ExactValueConstraint(y = 3.0f)),
                Constraint(x = 0.125f, yValueConstraint = ExactValueConstraint(y = 3.0f)),
                Constraint(x = 0.375f, yValueConstraint = ExactValueConstraint(y = 3.0f)),

                Constraint(x = 0.625f, yValueConstraint = VerticalRangeConstraint(minY = 2.5f, maxY = 4.5f)),
                Constraint(x = 0.875f, yValueConstraint = VerticalRangeConstraint(minY = 2.5f, maxY = 4.5f)),
                Constraint(x = 1.125f, yValueConstraint = VerticalRangeConstraint(minY = 2.5f, maxY = 4.5f)),
                Constraint(x = 1.375f, yValueConstraint = VerticalRangeConstraint(minY = 2.5f, maxY = 4.5f))
            )
        )
    }

    @Test
    fun someColumnsMissingConstraints() {
        assertEquals(
                actual = RawVisualisation(
                        visualisationRows = listOf(
                                VisualisationRow("   ", 4.0f),
                                VisualisationRow(" X "),
                                VisualisationRow("   ", 2.0f)
                        ),
                        xAxis = RawXAxis(
                                markers =        "| |",
                                values = listOf(-1.0f, 1.0f)))
                        .toConstraints(),
                expected = listOf(
                        Constraint(x = 0.0f, yValueConstraint = ExactValueConstraint(y = 3.0f))
                )
        )
    }

    @Test
    fun rowsHaveDifferentNumberOfCharacters() {
        assertFailsWith<IllegalArgumentException> {
            RawVisualisation(
                    visualisationRows = listOf(
                            VisualisationRow(" ", 4.0f),
                            VisualisationRow(" X"),
                            VisualisationRow("X  ", 2.0f)
                    ),
                    xAxis = RawXAxis(
                            markers = "| |",
                            values = listOf(-1.0f, 1.0f)))
                    .toConstraints()
        }.let { e ->
            assertEquals("Visualisation rows and the X axis markers string must have the same length!", e.message)
        }
    }

    @Test
    fun xAxisMarkersStringHasDifferentNumberOfCharacters() {
        assertFailsWith<IllegalArgumentException> {
            RawVisualisation(
                    visualisationRows = listOf(
                            VisualisationRow("  X", 4.0f),
                            VisualisationRow(" X "),
                            VisualisationRow("X  ", 2.0f)
                    ),
                    xAxis = RawXAxis(
                            markers =        "|    |",
                            values = listOf(-1.0f, 1.0f)))
                    .toConstraints()
        }.let { e ->
            assertEquals("Visualisation rows and the X axis markers string must have the same length!", e.message)
        }
    }
}

/* ktlint-disable no-multi-spaces paren-spacing */
