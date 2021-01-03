package it.krzeminski.visassert.types.constraints

import it.krzeminski.visassert.exceptions.FailedConstraintException
import it.krzeminski.visassert.types.AxisMarker
import it.krzeminski.visassert.types.VisualisationColumn
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VerticalRangeStrictConstraintTest {
    @Test
    fun assertMatchesWhenInInnerAndOuterRanges() {
        VerticalRangeStrictConstraint(
            xValues = listOf(1.0f, 2.0f, 3.0f),
            minY = 1.0f,
            innerMinY = 2.0f,
            innerMaxY = 3.0f,
            maxY = 4.0f
        )
            .assertMatches { x ->
                when (x) {
                    1.0f -> 1.5f
                    2.0f -> 2.5f
                    3.0f -> 3.5f
                    else -> 0.0f
                }
            }
    }

    @Test
    fun assertMatchesWhenOnBoundariesOfOuterRanges() {
        VerticalRangeStrictConstraint(
            xValues = listOf(1.0f, 2.0f, 3.0f),
            minY = 1.0f,
            innerMinY = 2.0f,
            innerMaxY = 3.0f,
            maxY = 4.0f
        )
            .assertMatches { x ->
                when (x) {
                    1.0f -> 1.0f
                    2.0f -> 2.5f
                    3.0f -> 4.0f
                    else -> 0.0f
                }
            }
    }

    @Test
    fun assertMatchesWhenOnlyInOuterRanges() {
        VerticalRangeStrictConstraint(
            xValues = listOf(1.0f, 2.0f, 3.0f),
            minY = 1.0f,
            innerMinY = 2.0f,
            innerMaxY = 3.0f,
            maxY = 4.0f
        )
            .assertMatches { x ->
                when (x) {
                    1.0f -> 1.0f
                    2.0f -> 3.5f
                    3.0f -> 4.0f
                    else -> 0.0f
                }
            }
    }

    @Test
    fun assertDoesNotMatchWhenInTheMiddleOfInnerRange() {
        assertFailsWith<FailedConstraintException> {
            VerticalRangeStrictConstraint(
                xValues = listOf(1.0f, 2.0f, 3.0f),
                minY = 1.0f,
                innerMinY = 2.0f,
                innerMaxY = 3.0f,
                maxY = 4.0f
            )
                .assertMatches { x ->
                    when (x) {
                        1.0f -> 2.1f
                        2.0f -> 2.4f
                        3.0f -> 2.8f
                        else -> 0.0f
                    }
                }
        }.let { e ->
            assertTrue(
                e.message in setOf(
                    "For a column with X values in range 1.0 to 3.0, with minY=1.0 and maxY=4.0, values above and below " +
                        "the inner range are missing!",
                    "For a column with X values in range 1 to 3, with minY=1 and maxY=4, values above and below " +
                        "the inner range are missing!"
                )
            )
        }
    }

    @Test
    fun assertDoesNotMatchWhenOnBoundariesOfInnerRange() {
        assertFailsWith<FailedConstraintException> {
            VerticalRangeStrictConstraint(
                xValues = listOf(1.0f, 2.0f, 3.0f),
                minY = 1.0f,
                innerMinY = 2.0f,
                innerMaxY = 3.0f,
                maxY = 4.0f
            )
                .assertMatches { x ->
                    when (x) {
                        1.0f -> 2.0f
                        2.0f -> 2.4f
                        3.0f -> 3.0f
                        else -> 0.0f
                    }
                }
        }.let { e ->
            assertTrue(
                e.message in setOf(
                    "For a column with X values in range 1.0 to 3.0, with minY=1.0 and maxY=4.0, values above and below " +
                        "the inner range are missing!",
                    "For a column with X values in range 1 to 3, with minY=1 and maxY=4, values above and below " +
                        "the inner range are missing!"
                )
            )
        }
    }

    @Test
    fun assertDoesNotMatchWhenNotInUpperOuterRange() {
        assertFailsWith<FailedConstraintException> {
            VerticalRangeStrictConstraint(
                xValues = listOf(1.0f, 2.0f, 3.0f),
                minY = 1.0f,
                innerMinY = 2.0f,
                innerMaxY = 3.0f,
                maxY = 4.0f
            )
                .assertMatches { x ->
                    when (x) {
                        1.0f -> 1.5f
                        2.0f -> 2.5f
                        3.0f -> 2.6f
                        else -> 0.0f
                    }
                }
        }.let { e ->
            assertTrue(
                e.message in setOf(
                    "For a column with X values in range 1.0 to 3.0, with minY=1.0 and maxY=4.0, values above " +
                        "the inner range are missing!",
                    "For a column with X values in range 1 to 3, with minY=1 and maxY=4, values above " +
                        "the inner range are missing!"
                )
            )
        }
    }

    @Test
    fun assertDoesNotMatchWhenNotInLowerOuterRange() {
        assertFailsWith<FailedConstraintException> {
            VerticalRangeStrictConstraint(
                xValues = listOf(1.0f, 2.0f, 3.0f),
                minY = 1.0f,
                innerMinY = 2.0f,
                innerMaxY = 3.0f,
                maxY = 4.0f
            )
                .assertMatches { x ->
                    when (x) {
                        1.0f -> 2.4f
                        2.0f -> 2.5f
                        3.0f -> 3.5f
                        else -> 0.0f
                    }
                }
        }.let { e ->
            assertTrue(
                e.message in setOf(
                    "For a column with X values in range 1.0 to 3.0, with minY=1.0 and maxY=4.0, values below " +
                        "the inner range are missing!",
                    "For a column with X values in range 1 to 3, with minY=1 and maxY=4, values below " +
                        "the inner range are missing!"
                )
            )
        }
    }

    @Test
    fun assertDoesNotMatchWhenSomePointAboveOuterRange() {
        assertFailsWith<FailedConstraintException> {
            VerticalRangeStrictConstraint(
                xValues = listOf(1.0f, 2.0f, 3.0f),
                minY = 1.0f,
                innerMinY = 2.0f,
                innerMaxY = 3.0f,
                maxY = 4.0f
            )
                .assertMatches { x ->
                    when (x) {
                        1.0f -> 1.5f
                        2.0f -> 2.5f
                        3.0f -> 6.5f
                        else -> 0.0f
                    }
                }
        }.let { e ->
            assertTrue(
                e.message in setOf(
                    "For x=3.0: 6.5 is not between 1.0 and 4.0!",
                    "For x=3: 6.5 is not between 1 and 4!"
                )
            )
        }
    }

    @Test
    fun assertDoesNotMatchWhenSomePointBelowOuterRange() {
        assertFailsWith<FailedConstraintException> {
            VerticalRangeStrictConstraint(
                xValues = listOf(1.0f, 2.0f, 3.0f),
                minY = 1.0f,
                innerMinY = 2.0f,
                innerMaxY = 3.0f,
                maxY = 4.0f
            )
                .assertMatches { x ->
                    when (x) {
                        1.0f -> 0.5f
                        2.0f -> 2.5f
                        3.0f -> 3.5f
                        else -> 0.0f
                    }
                }
        }.let { e ->
            assertTrue(
                e.message in setOf(
                    "For x=1.0: 0.5 is not between 1.0 and 4.0!",
                    "For x=1: 0.5 is not between 1 and 4!"
                )
            )
        }
    }

    @Test
    fun singleCapitalICharacterCheckIfMatches() {
        assertTrue(
            VerticalRangeStrictConstraintBuilder.columnMatchesThisConstraintType(
                VisualisationColumn("   I ")
            )
        )
    }

    @Test
    fun manySubsequentCapitalICharactersCheckIfMatches() {
        assertTrue(
            VerticalRangeStrictConstraintBuilder.columnMatchesThisConstraintType(
                VisualisationColumn(" III ")
            )
        )
    }

    @Test
    fun manySubsequentCapitalICharactersWithoutSpaceCheckIfMatches() {
        assertTrue(
            VerticalRangeStrictConstraintBuilder.columnMatchesThisConstraintType(
                VisualisationColumn("IIIII")
            )
        )
    }

    @Test
    fun capitalICharactersDividedBySpaceCheckIfMatches() {
        assertFalse(
            VerticalRangeStrictConstraintBuilder.columnMatchesThisConstraintType(
                VisualisationColumn(" I II")
            )
        )
    }

    @Test
    fun mixedCharactersCheckIfMatches() {
        assertFalse(
            VerticalRangeStrictConstraintBuilder.columnMatchesThisConstraintType(
                VisualisationColumn(" IIX ")
            )
        )
    }

    @Test
    fun noCapitalICharacterCheckIfMatches() {
        assertFalse(
            VerticalRangeStrictConstraintBuilder.columnMatchesThisConstraintType(
                VisualisationColumn("   X ")
            )
        )
    }

    @Test
    fun singleCapitalICharacterBuildConstraint() {
        assertEquals(
            actual = VerticalRangeStrictConstraintBuilder.buildConstraintFromColumn(
                xValues = listOf(1.23f),
                column = VisualisationColumn("   I "),
                yAxisMarkers = listOf(AxisMarker(5.0f, 0), AxisMarker(1.0f, 4))
            ),
            expected = VerticalRangeLooseConstraint(xValues = listOf(1.23f), minY = 1.5f, maxY = 2.5f)
        )
    }

    @Test
    fun manySubsequentCapitalICharactersBuildConstraint() {
        assertEquals(
            actual = VerticalRangeStrictConstraintBuilder.buildConstraintFromColumn(
                xValues = listOf(1.23f),
                column = VisualisationColumn(" III "),
                yAxisMarkers = listOf(AxisMarker(5.0f, 0), AxisMarker(1.0f, 4))
            ),
            expected = VerticalRangeStrictConstraint(
                xValues = listOf(1.23f),
                minY = 1.5f,
                maxY = 4.5f,
                innerMinY = 2.5f,
                innerMaxY = 3.5f
            )
        )
    }

    @Test
    fun manySubsequentCapitalICharactersWithoutSpaceBuildConstraint() {
        assertEquals(
            actual = VerticalRangeStrictConstraintBuilder.buildConstraintFromColumn(
                xValues = listOf(1.23f),
                column = VisualisationColumn("IIIII"),
                yAxisMarkers = listOf(AxisMarker(5.0f, 0), AxisMarker(1.0f, 4))
            ),
            expected = VerticalRangeStrictConstraint(
                xValues = listOf(1.23f),
                minY = 0.5f,
                maxY = 5.5f,
                innerMinY = 1.5f,
                innerMaxY = 4.5f
            )
        )
    }
}
