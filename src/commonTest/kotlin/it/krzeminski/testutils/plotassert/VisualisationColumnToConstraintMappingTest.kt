package it.krzeminski.testutils.plotassert

import it.krzeminski.testutils.plotassert.types.AxisMarker
import it.krzeminski.testutils.plotassert.types.VisualisationColumn
import it.krzeminski.testutils.plotassert.types.constraints.Constraint
import it.krzeminski.testutils.plotassert.types.constraints.ConstraintBuilder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class VisualisationColumnToConstraintMappingTest {
    @Test
    fun oneConstraintMatches() {
        val constraintToReturn = object : Constraint {
            override val xValues = listOf(4.0f)
            override fun assertMatches(function: (Float) -> Float) {
                throw IllegalStateException("This method is irrelevant for this unit test.")
            }
        }
        val mockConstraintBuilder1 = object : ConstraintBuilder() {
            override fun columnMatchesThisConstraintType(column: VisualisationColumn) = true

            override fun buildConstraintFromColumn(xValues: List<Float>, column: VisualisationColumn, yAxisMarkers: List<AxisMarker>) =
                constraintToReturn
        }
        val mockConstraintBuilder2 = object : ConstraintBuilder() {
            override fun columnMatchesThisConstraintType(column: VisualisationColumn) = false

            override fun buildConstraintFromColumn(xValues: List<Float>, column: VisualisationColumn, yAxisMarkers: List<AxisMarker>): Constraint {
                throw IllegalStateException("This method is irrelevant for this unit test.")
            }
        }
        val mockGetAvailableConstraintBuilders = { listOf(mockConstraintBuilder1, mockConstraintBuilder2) }

        assertEquals(
            actual = mapVisualisationColumnToConstraint(
                listOf(4.0f),
                VisualisationColumn("MOCK VISUALISATION COLUMN"),
                listOf(AxisMarker(5.0f, 0), AxisMarker(1.0f, 4)),
                mockGetAvailableConstraintBuilders
            ),
            expected = constraintToReturn
        )
    }

    @Test
    fun noConstraintDesired() {
        assertEquals(
            actual = mapVisualisationColumnToConstraint(
                listOf(4.0f),
                VisualisationColumn("     "),
                listOf(AxisMarker(5.0f, 0), AxisMarker(1.0f, 4))
            ),
            expected = null
        )
    }

    @Test
    fun noConstraintsMatch() {
        val mockConstraintBuilder1 = object : ConstraintBuilder() {
            override fun columnMatchesThisConstraintType(column: VisualisationColumn) = false

            override fun buildConstraintFromColumn(xValues: List<Float>, column: VisualisationColumn, yAxisMarkers: List<AxisMarker>): Constraint {
                throw IllegalStateException("This method shouldn't be used  for this unit test.")
            }
        }
        val mockConstraintBuilder2 = object : ConstraintBuilder() {
            override fun columnMatchesThisConstraintType(column: VisualisationColumn) = false

            override fun buildConstraintFromColumn(xValues: List<Float>, column: VisualisationColumn, yAxisMarkers: List<AxisMarker>): Constraint {
                throw IllegalStateException("This method shouldn't be used  for this unit test.")
            }
        }
        val mockGetAvailableConstraintBuilders = { listOf(mockConstraintBuilder1, mockConstraintBuilder2) }

        assertFailsWith<IllegalArgumentException> {
            mapVisualisationColumnToConstraint(
                listOf(4.0f),
                VisualisationColumn("MOCK VISUALISATION COLUMN"),
                listOf(AxisMarker(5.0f, 0), AxisMarker(1.0f, 4)),
                mockGetAvailableConstraintBuilders
            )
        }.let { e ->
            assertEquals("No constraints match this visualisation column: MOCK VISUALISATION COLUMN", e.message)
        }
    }

    @Test
    fun multipleConstraintsMatch() {
        val mockConstraintBuilder1 = object : ConstraintBuilder() {
            override fun columnMatchesThisConstraintType(column: VisualisationColumn) = true

            override fun buildConstraintFromColumn(xValues: List<Float>, column: VisualisationColumn, yAxisMarkers: List<AxisMarker>): Constraint {
                throw IllegalStateException("This method shouldn't be used  for this unit test.")
            }
        }
        val mockConstraintBuilder2 = object : ConstraintBuilder() {
            override fun columnMatchesThisConstraintType(column: VisualisationColumn) = true

            override fun buildConstraintFromColumn(xValues: List<Float>, column: VisualisationColumn, yAxisMarkers: List<AxisMarker>): Constraint {
                throw IllegalStateException("This method shouldn't be used  for this unit test.")
            }
        }
        val mockGetAvailableConstraintBuilders = { listOf(mockConstraintBuilder1, mockConstraintBuilder2) }

        assertFailsWith<IllegalArgumentException> {
            mapVisualisationColumnToConstraint(
                listOf(4.0f),
                VisualisationColumn("MOCK VISUALISATION COLUMN"),
                listOf(AxisMarker(5.0f, 0), AxisMarker(1.0f, 4)),
                mockGetAvailableConstraintBuilders
            )
        }.let { e ->
            assertNotNull(e.message)
            assertTrue(e.message!!.startsWith("Ambiguous constraint; more than 1 constraint type matches:"))
        }
    }
}
