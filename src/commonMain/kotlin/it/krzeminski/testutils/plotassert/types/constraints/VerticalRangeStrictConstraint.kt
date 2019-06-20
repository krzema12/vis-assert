package it.krzeminski.testutils.plotassert.types.constraints

import it.krzeminski.testutils.plotassert.computeValueBounds
import it.krzeminski.testutils.plotassert.exceptions.FailedConstraintException
import it.krzeminski.testutils.plotassert.types.AxisMarker
import it.krzeminski.testutils.plotassert.types.VisualisationColumn

data class VerticalRangeStrictConstraint(
    override val xValues: List<Float>,
    private val minY: Float,
    private val maxY: Float,
    private val innerMinY: Float,
    private val innerMaxY: Float
) : Constraint {
    override fun assertMatches(function: (Float) -> Float) {
        xValues.forEach { x ->
            val yValue = function(x)
            if (yValue !in minY..maxY) {
                throw FailedConstraintException("For x=$x: $yValue is not between $minY and $maxY!")
            }
        }
        val anyAboveInnerMaxY = xValues.any { x ->
            val yValue = function(x)
            yValue > innerMaxY
        }
        val anyBelowInnerMinY = xValues.any { x ->
            val yValue = function(x)
            yValue < innerMinY
        }
        if (!(anyAboveInnerMaxY && anyBelowInnerMinY)) {
            val whereValuesMissing = listOfNotNull(
                if (anyAboveInnerMaxY) null else "above",
                if (anyBelowInnerMinY) null else "below"
            ).joinToString(" and ")
            throw FailedConstraintException(
                "For a column with X values in range ${xValues.first()} to ${xValues.last()}, " +
                        "with minY=$minY and maxY=$maxY, values $whereValuesMissing the inner range are missing!")
        }
    }
}

object VerticalRangeStrictConstraintBuilder : ConstraintBuilder() {
    override fun columnMatchesThisConstraintType(column: VisualisationColumn): Boolean {
        val onlyLegalCharacters = setOf(' ', 'I').containsAll(column.characters.groupBy { it }.keys)
        val noGapsBetweenLetters =
            column.characters
                    .mapIndexedNotNull { index, character -> if (character == 'I') index else null }
                    .zipWithNext { a, b -> b - a }
                    .all { difference -> difference == 1 }

        return onlyLegalCharacters && noGapsBetweenLetters
    }

    override fun buildConstraintFromColumn(
        xValues: List<Float>,
        column: VisualisationColumn,
        yAxisMarkers: List<AxisMarker>
    ): Constraint
    {
        if (column.characters.count { it == 'I' } == 1) {
            return VerticalRangeLooseConstraintBuilder.buildConstraintFromColumn(
                xValues, column.copy(characters = column.characters.replace('I', 'i')), yAxisMarkers)
        }

        val indexOfFirstCharacter = column.characters.indexOfFirst { it == 'I' }
        val indexOfLastCharacter = column.characters.indexOfLast { it == 'I' }

        val firstCharacterValueBounds = computeValueBounds(yAxisMarkers, indexOfFirstCharacter)
        val secondCharacterValueBounds = computeValueBounds(yAxisMarkers, indexOfFirstCharacter + 1)
        val secondToLastCharacterValueBounds = computeValueBounds(yAxisMarkers, indexOfLastCharacter - 1)
        val lastCharacterValueBounds = computeValueBounds(yAxisMarkers, indexOfLastCharacter)

        return VerticalRangeStrictConstraint(
            xValues,
            minY = lastCharacterValueBounds.lowerBound,
            maxY = firstCharacterValueBounds.upperBound,
            innerMinY = secondToLastCharacterValueBounds.lowerBound,
            innerMaxY = secondCharacterValueBounds.upperBound)
    }
}
