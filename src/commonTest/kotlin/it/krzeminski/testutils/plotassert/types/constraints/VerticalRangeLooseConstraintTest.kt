package it.krzeminski.testutils.plotassert.types.constraints

import it.krzeminski.testutils.plotassert.exceptions.FailedConstraintException
import it.krzeminski.testutils.plotassert.types.AxisMarker
import it.krzeminski.testutils.plotassert.types.VisualisationColumn
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VerticalRangeLooseConstraintTest {
    @Test
    fun assertMatchesWhenInTheMiddleOfRange() {
        VerticalRangeLooseConstraint(xValues = listOf(1.0f), minY = 1.0f, maxY = 2.0f)
            .assertMatches { 1.5f }
    }

    @Test
    fun assertMatchesWhenOnLowerBoundOfRange() {
        VerticalRangeLooseConstraint(xValues = listOf(1.0f), minY = 1.0f, maxY = 2.0f)
            .assertMatches { 1.0f }
    }

    @Test
    fun assertMatchesWhenOnUpperBoundOfRange() {
        VerticalRangeLooseConstraint(xValues = listOf(1.0f), minY = 1.0f, maxY = 2.0f)
            .assertMatches { 2.0f }
    }

    @Test
    fun assertDoesNotMatchBecauseAboveRange() {
        assertFailsWith<FailedConstraintException> {
            VerticalRangeLooseConstraint(xValues = listOf(1.0f), minY = 1.0f, maxY = 2.0f)
                .assertMatches { 3.0f }
        }.let { e ->
            assertTrue(e.message in setOf("For x=1.0: 3.0 is not between 1.0 and 2.0!", "For x=1: 3 is not between 1 and 2!"))
        }
    }

    @Test
    fun assertDoesNotMatchBecauseBelowRange() {
        assertFailsWith<FailedConstraintException> {
            VerticalRangeLooseConstraint(xValues = listOf(1.0f), minY = 1.0f, maxY = 2.0f)
                .assertMatches { 0.0f }
        }.let { e ->
            assertTrue(e.message in setOf("For x=1.0: 0.0 is not between 1.0 and 2.0!", "For x=1: 0 is not between 1 and 2!"))
        }
    }

    @Test
    fun assertDoesNotMatchOnSecondXValue() {
        assertFailsWith<FailedConstraintException> {
            VerticalRangeLooseConstraint(xValues = listOf(1.0f, 2.0f), minY = 1.0f, maxY = 2.0f)
                .assertMatches {
                    x ->
                    when (x) {
                        1.0f -> 1.0f
                        else -> 123.0f
                    }
                }
        }.let { e ->
            assertTrue(
                e.message in setOf(
                    "For x=2.0: 123.0 is not between 1.0 and 2.0!",
                    "For x=2: 123 is not between 1 and 2!",
                )
            )
        }
    }

    @Test
    fun singleSmallICharacterCheckIfMatches() {
        assertTrue(
            VerticalRangeLooseConstraintBuilder.columnMatchesThisConstraintType(
                VisualisationColumn("   i ")
            )
        )
    }

    @Test
    fun manySubsequentSmallICharactersCheckIfMatches() {
        assertTrue(
            VerticalRangeLooseConstraintBuilder.columnMatchesThisConstraintType(
                VisualisationColumn(" iii ")
            )
        )
    }

    @Test
    fun manySubsequentSmallICharactersWithoutSpaceCheckIfMatches() {
        assertTrue(
            VerticalRangeLooseConstraintBuilder.columnMatchesThisConstraintType(
                VisualisationColumn("iiiii")
            )
        )
    }

    @Test
    fun smallICharactersDividedBySpaceCheckIfMatches() {
        assertFalse(
            VerticalRangeLooseConstraintBuilder.columnMatchesThisConstraintType(
                VisualisationColumn(" i ii")
            )
        )
    }

    @Test
    fun mixedCharactersCheckIfMatches() {
        assertFalse(
            VerticalRangeLooseConstraintBuilder.columnMatchesThisConstraintType(
                VisualisationColumn(" iiX ")
            )
        )
    }

    @Test
    fun noSmallICharacterCheckIfMatches() {
        assertFalse(
            VerticalRangeLooseConstraintBuilder.columnMatchesThisConstraintType(
                VisualisationColumn("   X ")
            )
        )
    }

    @Test
    fun singleSmallICharacterBuildConstraint() {
        assertEquals(
            actual = VerticalRangeLooseConstraintBuilder.buildConstraintFromColumn(
                xValues = listOf(1.23f),
                column = VisualisationColumn("   i "),
                yAxisMarkers = listOf(AxisMarker(5.0f, 0), AxisMarker(1.0f, 4))
            ),
            expected = VerticalRangeLooseConstraint(xValues = listOf(1.23f), minY = 1.5f, maxY = 2.5f)
        )
    }

    @Test
    fun manySubsequentSmallICharactersBuildConstraint() {
        assertEquals(
            actual = VerticalRangeLooseConstraintBuilder.buildConstraintFromColumn(
                xValues = listOf(1.23f),
                column = VisualisationColumn(" iii "),
                yAxisMarkers = listOf(AxisMarker(5.0f, 0), AxisMarker(1.0f, 4))
            ),
            expected = VerticalRangeLooseConstraint(xValues = listOf(1.23f), minY = 1.5f, maxY = 4.5f)
        )
    }

    @Test
    fun manySubsequentSmallICharactersWithoutSpaceBuildConstraint() {
        assertEquals(
            actual = VerticalRangeLooseConstraintBuilder.buildConstraintFromColumn(
                xValues = listOf(1.23f),
                column = VisualisationColumn("iiiii"),
                yAxisMarkers = listOf(AxisMarker(5.0f, 0), AxisMarker(1.0f, 4))
            ),
            expected = VerticalRangeLooseConstraint(xValues = listOf(1.23f), minY = 0.5f, maxY = 5.5f)
        )
    }
}
