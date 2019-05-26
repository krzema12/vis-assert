package it.krzeminski.testutils.plotassert.types.constraints

import it.krzeminski.testutils.plotassert.exceptions.FailedConstraintException
import it.krzeminski.testutils.plotassert.types.AxisMarker
import it.krzeminski.testutils.plotassert.types.VisualisationColumn
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ExactValueConstraintTest {
    @Test
    fun assertMatchesWhenEqualValues() {
        ExactValueConstraint(x = 0.0f, y = 1.0f)
                .assertMatches { 1.0f }
    }

    @Test
    fun assertDoesNotMatchWhenReallyCloseValues() {
        assertFailsWith<FailedConstraintException> {
            ExactValueConstraint(x = 1.23f, y = 1.0f)
                    .assertMatches { 1.0000001f }
        }.let { e ->
            assertTrue(e.message in setOf("For x=1.23: 1.0000001 is not equal to 1.0!", "For x=1.23: 1.0000001 is not equal to 1!"))
        }
    }

    @Test
    fun assertDoesNotMatch() {
        assertFailsWith<FailedConstraintException> {
            ExactValueConstraint(x = 1.23f, y = 1.0f)
                    .assertMatches { 3.0f }
        }.let { e ->
            assertTrue(e.message in setOf("For x=1.23: 3.0 is not equal to 1.0!", "For x=1.23: 3 is not equal to 1!"))
        }
    }

    @Test
    fun singleXCharacterCheckIfMatches() {
        assertTrue(ExactValueConstraintBuilder.columnMatchesThisConstraintType(
                VisualisationColumn("   X ")))
    }

    @Test
    fun manyXCharactersCheckIfMatches() {
        assertFalse(ExactValueConstraintBuilder.columnMatchesThisConstraintType(
                VisualisationColumn("  XX ")))
    }

    @Test
    fun mixedCharactersCheckIfMatches() {
        assertFalse(ExactValueConstraintBuilder.columnMatchesThisConstraintType(
                VisualisationColumn(" IIX ")))
    }

    @Test
    fun noXCharacterCheckIfMatches() {
        assertFalse(ExactValueConstraintBuilder.columnMatchesThisConstraintType(
                VisualisationColumn("   I ")))
    }

    @Test
    fun singleXCharacterBuildConstraint() {
        assertEquals(
                actual = ExactValueConstraintBuilder.buildConstraintFromColumn(
                        x = 1.23f,
                        column = VisualisationColumn("   X "),
                        yAxisMarkers = listOf(AxisMarker(5.0f, 0), AxisMarker(1.0f, 4))),
                expected = ExactValueConstraint(x = 1.23f, y = 2.0f))
    }
}
