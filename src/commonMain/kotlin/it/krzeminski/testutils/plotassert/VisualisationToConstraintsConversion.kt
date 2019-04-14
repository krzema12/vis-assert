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
    val yValueConstraint = mapVisualisationColumnToConstraint(visualisationColumn, yAxisMarkers)
    val xValueBounds = computeValueBounds(xAxisMarkers, xIndex)
    val evenlyDistributedXPoints = xValueBounds.evenlyDistributedPointsBetweenBounds(samplesPerCharacter)

    return evenlyDistributedXPoints.map { xPointBetweenBounds ->
        yValueConstraint?.let {
            Constraint(x = xPointBetweenBounds, yValueConstraint = yValueConstraint)
        }
    }
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
