package it.krzeminski.testutils.plotassert.types.constraints

import it.krzeminski.testutils.plotassert.computeValueBounds
import it.krzeminski.testutils.plotassert.exceptions.FailedConstraintException
import it.krzeminski.testutils.plotassert.types.AxisMarker
import it.krzeminski.testutils.plotassert.types.VisualisationColumn

/**
 * Performs an "exactly equal" comparison. If the value differs even on the least significant digit, it's still reported
 * as not equal.
 */
data class ExactValueConstraint(
    override val xValues: List<Float>,
    private val y: Float
) : Constraint {
    override fun assertMatches(function: (Float) -> Float) {
        xValues.forEach { x ->
            val yValue = function(x)
            if (y != yValue) {
                throw FailedConstraintException("For x=$x: $yValue is not equal to $y!")
            }
        }
    }
}

object ExactValueConstraintBuilder : ConstraintBuilder() {
    override fun columnMatchesThisConstraintType(column: VisualisationColumn): Boolean =
            with(column.characters.groupBy { it }) {
                return keys == setOf(' ', 'X') && this.getValue('X').size == 1
            }

    override fun buildConstraintFromColumn(
        xValues: List<Float>,
        column: VisualisationColumn,
        yAxisMarkers: List<AxisMarker>
    ): Constraint
    {
        val indexOfXCharacter = column.characters.indexOf('X')
        val valueBounds = computeValueBounds(yAxisMarkers, indexOfXCharacter)

        return ExactValueConstraint(xValues, valueBounds.center)
    }
}
