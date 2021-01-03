package it.krzeminski.visassert.types.constraints

import it.krzeminski.visassert.computeValueBounds
import it.krzeminski.visassert.exceptions.FailedConstraintException
import it.krzeminski.visassert.types.AxisMarker
import it.krzeminski.visassert.types.VisualisationColumn

data class VerticalRangeLooseConstraint(
    override val xValues: List<Float>,
    private val minY: Float,
    private val maxY: Float
) : Constraint {
    override fun assertMatches(function: (Float) -> Float) {
        xValues.forEach { x ->
            val yValue = function(x)
            if (yValue !in minY..maxY) {
                throw FailedConstraintException("For x=$x: $yValue is not between $minY and $maxY!")
            }
        }
    }
}

object VerticalRangeLooseConstraintBuilder : ConstraintBuilder() {
    override fun columnMatchesThisConstraintType(column: VisualisationColumn): Boolean {
        val onlyLegalCharacters = setOf(' ', 'i').containsAll(column.characters.groupBy { it }.keys)
        val noGapsBetweenLetters =
            column.characters
                .mapIndexedNotNull { index, character -> if (character == 'i') index else null }
                .zipWithNext { a, b -> b - a }
                .all { difference -> difference == 1 }

        return onlyLegalCharacters && noGapsBetweenLetters
    }

    override fun buildConstraintFromColumn(
        xValues: List<Float>,
        column: VisualisationColumn,
        yAxisMarkers: List<AxisMarker>
    ): Constraint {
        val indexOfFirstCharacter = column.characters.indexOfFirst { it == 'i' }
        val indexOfLastCharacter = column.characters.indexOfLast { it == 'i' }
        val firstCharacterValueBounds = computeValueBounds(yAxisMarkers, indexOfFirstCharacter)
        val lastCharacterValueBounds = computeValueBounds(yAxisMarkers, indexOfLastCharacter)

        return VerticalRangeLooseConstraint(
            xValues,
            minY = lastCharacterValueBounds.lowerBound,
            maxY = firstCharacterValueBounds.upperBound
        )
    }
}
