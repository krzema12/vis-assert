package it.krzeminski.testutils.plotassert

import it.krzeminski.testutils.plotassert.types.RawVisualisation
import it.krzeminski.testutils.plotassert.types.RawXAxis
import it.krzeminski.testutils.plotassert.types.VisualisationRow
import it.krzeminski.testutils.plotassert.types.constraints.ExactValueConstraint
import it.krzeminski.testutils.plotassert.types.constraints.VerticalRangeLooseConstraint
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
                        ExactValueConstraint(xValues = listOf(-1.0f), y = 2.0f),
                        ExactValueConstraint(xValues = listOf(0.0f), y = 3.0f),
                        VerticalRangeLooseConstraint(xValues = listOf(1.0f), minY = 2.5f, maxY = 4.5f)
                )
        )
    }

    @Test
    fun simpleLinearFunctionForEvenSamplesPerCharacter() {
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
                ExactValueConstraint(xValues = listOf(-0.875f, -0.625f), y = 2.0f),
                ExactValueConstraint(xValues = listOf(-0.375f, -0.125f, 0.125f, 0.375f), y = 3.0f),
                VerticalRangeLooseConstraint(xValues = listOf(0.625f, 0.875f), minY = 2.5f, maxY = 4.5f)
            )
        )
    }

    @Test
    fun simpleLinearFunctionForOddSamplesPerCharacter() {
        assertEquals(
            actual = RawVisualisation(
                visualisationRows = listOf(
                    VisualisationRow("  I", 4.0f),
                    VisualisationRow(" XI"),
                    VisualisationRow("X  ", 2.0f)
                ),
                xAxis = RawXAxis(
                    markers =        "| |",
                    values = listOf(-6.0f, 6.0f)))
                .toConstraints(samplesPerCharacter = 3),
            expected = listOf(
                ExactValueConstraint(xValues = listOf(-6.0f, -4.0f), y = 2.0f),
                ExactValueConstraint(xValues = listOf(-2.0f, 0.0f, 2.0f), y = 3.0f),
                VerticalRangeLooseConstraint(xValues = listOf(4.0f, 6.0f), minY = 2.5f, maxY = 4.5f)
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
                        ExactValueConstraint(xValues = listOf(0.0f), y = 3.0f)
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
