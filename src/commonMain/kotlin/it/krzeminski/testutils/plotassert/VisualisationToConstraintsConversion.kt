package it.krzeminski.testutils.plotassert

import it.krzeminski.testutils.plotassert.types.AxisMarker
import it.krzeminski.testutils.plotassert.types.RawVisualisation
import it.krzeminski.testutils.plotassert.types.ValueBounds
import it.krzeminski.testutils.plotassert.types.VisualisationColumn
import it.krzeminski.testutils.plotassert.types.constraints.Constraint

fun RawVisualisation.toConstraints(samplesPerCharacter: Int = 1): List<Constraint> {
    validateAllStringsHaveTheSameLength(this)

    val xAxisMarkers = readXAxisMarkers(this)
    val yAxisMarkers = readYAxisMarkers(this)

    return this.columns.mapIndexed { xIndex, visualisationColumn ->
        buildConstraints(visualisationColumn, yAxisMarkers, xAxisMarkers, xIndex, samplesPerCharacter)
    }.flatten().filterNotNull()
}

private fun validateAllStringsHaveTheSameLength(rawVisualisation: RawVisualisation) {
    val firstRowLength = rawVisualisation.visualisationRows.first().characters.length
    val allRowsHaveTheSameLength = rawVisualisation.visualisationRows
            .map { it.characters.length }
            .all { it == firstRowLength }
    val xAxisMarkersStringHasTheSameLengthAsRows = rawVisualisation.xAxis.markers.length == firstRowLength

    require(allRowsHaveTheSameLength && xAxisMarkersStringHasTheSameLengthAsRows) {
        "Visualisation rows and the X axis markers string must have the same length!"
    }
}

private fun buildConstraints(
    visualisationColumn: VisualisationColumn,
    yAxisMarkers: List<AxisMarker>,
    xAxisMarkers: List<AxisMarker>,
    xIndex: Int,
    samplesPerCharacter: Int
): List<Constraint?>
{
    val buildConstraint = { xValue: Float ->
        mapVisualisationColumnToConstraint(xValue, visualisationColumn, yAxisMarkers) }
    val xValueBounds = computeValueBounds(xAxisMarkers, xIndex)
    val evenlyDistributedXPoints = xValueBounds.evenlyDistributedPointsBetweenBounds(samplesPerCharacter)

    val minX = computeValueBounds(xAxisMarkers, xAxisMarkers.first().characterIndex).center
    val maxX = computeValueBounds(xAxisMarkers, xAxisMarkers.last().characterIndex).center
    val isWithinXDomain = { x: Float -> x in minX..maxX }

    return evenlyDistributedXPoints
        .filter { isWithinXDomain(it) }
        .map { xPointBetweenBounds -> buildConstraint(xPointBetweenBounds) }
}

private fun ValueBounds.evenlyDistributedPointsBetweenBounds(numberOfPoints: Int): List<Float> {
    val difference = (this.upperBound - this.lowerBound) / numberOfPoints.toFloat() / 2.0f
    return (0..(numberOfPoints - 1)).map { pointIndex ->
        this.lowerBound + difference + pointIndex.toFloat() * difference * 2.0f
    }
}

private val RawVisualisation.columns: List<VisualisationColumn>
        get() {
            return visualisationRows.first().characters.mapIndexed { index, _ ->
                VisualisationColumn(visualisationRows
                        .map { it.characters[index] }
                        .joinToString(separator = ""))
            }
        }
