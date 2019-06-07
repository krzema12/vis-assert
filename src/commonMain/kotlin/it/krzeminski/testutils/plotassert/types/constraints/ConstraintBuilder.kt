package it.krzeminski.testutils.plotassert.types.constraints

import it.krzeminski.testutils.plotassert.types.AxisMarker
import it.krzeminski.testutils.plotassert.types.VisualisationColumn

abstract class ConstraintBuilder {
    abstract fun columnMatchesThisConstraintType(column: VisualisationColumn): Boolean
    /**
     * This method is expected to build a correct constraint object only if [columnMatchesThisConstraintType] returned
     * true for the given [column].
     */
    abstract fun buildConstraintFromColumn(
        xValues: List<Float>,
        column: VisualisationColumn,
        yAxisMarkers: List<AxisMarker>
    ): Constraint
}

fun getAvailableConstraintBuilders() =
        listOf(
                ExactValueConstraintBuilder,
                VerticalRangeConstraintBuilder)
