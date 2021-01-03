package it.krzeminski.visassert.types.constraints

import it.krzeminski.visassert.types.AxisMarker
import it.krzeminski.visassert.types.VisualisationColumn

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
        VerticalRangeStrictConstraintBuilder,
        VerticalRangeLooseConstraintBuilder
    )
