package it.krzeminski.visassert

import it.krzeminski.visassert.types.AxisMarker
import it.krzeminski.visassert.types.RawVisualisation
import it.krzeminski.visassert.types.RawXAxis
import it.krzeminski.visassert.types.VisualisationRow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ReadingYAxisMarkersTest {
    @Test
    fun yAxisCommonCase() {
        assertEquals(
            readYAxisMarkers(
                RawVisualisation(
                    visualisationRows = listOf(
                        VisualisationRow("    X   ", 1.0f),
                        VisualisationRow("   I  I "),
                        VisualisationRow(" III  II", 0.0f),
                        VisualisationRow("   II I "),
                        VisualisationRow("    X   ", -1.0f)
                    ),
                    xAxis = RawXAxis(
                        markers = "|      |",
                        values = listOf(0.0f, 1.0f)
                    )
                )
            ),
            listOf(
                AxisMarker(value = 1.0f, characterIndex = 0),
                AxisMarker(value = 0.0f, characterIndex = 2),
                AxisMarker(value = -1.0f, characterIndex = 4)
            )
        )
    }

    @Test
    fun yAxisRepeatedValue() {
        assertFailsWith<IllegalArgumentException> {
            readYAxisMarkers(
                RawVisualisation(
                    visualisationRows = listOf(
                        VisualisationRow("    X   ", 1.0f),
                        VisualisationRow(" III  II", 1.0f)
                    ),
                    xAxis = RawXAxis(
                        markers = "|      |",
                        values = listOf(0.0f, 1.0f)
                    )
                )
            )
        }.let { e ->
            assertTrue(
                e.message in setOf(
                    "Given Y axis markers should have descending values (found: 1.0, 1.0)!",
                    "Given Y axis markers should have descending values (found: 1, 1)!"
                )
            )
        }
    }

    @Test
    fun yAxisIncorrectOrder() {
        assertFailsWith<IllegalArgumentException> {
            readYAxisMarkers(
                RawVisualisation(
                    visualisationRows = listOf(
                        VisualisationRow("    X   ", 4.0f),
                        VisualisationRow(" III  II", 9.0f),
                        VisualisationRow(" III  II", 2.0f)
                    ),
                    xAxis = RawXAxis(
                        markers = "|      |",
                        values = listOf(0.0f, 1.0f)
                    )
                )
            )
        }.let { e ->
            assertTrue(
                e.message in setOf(
                    "Given Y axis markers should have descending values (found: 4.0, 9.0)!",
                    "Given Y axis markers should have descending values (found: 4, 9)!"
                )
            )
        }
    }

    @Test
    fun yAxisNoMarkers() {
        assertFailsWith<IllegalArgumentException> {
            readYAxisMarkers(
                RawVisualisation(
                    visualisationRows = listOf(
                        VisualisationRow("    X   "),
                        VisualisationRow(" III  II")
                    ),
                    xAxis = RawXAxis(
                        markers = "|      |",
                        values = listOf(0.0f, 1.0f)
                    )
                )
            )
        }.let { e ->
            assertEquals("0 Y axis marker(s) found, and there should be at least two!", e.message)
        }
    }

    @Test
    fun yAxisOneMarker() {
        assertFailsWith<IllegalArgumentException> {
            readYAxisMarkers(
                RawVisualisation(
                    visualisationRows = listOf(
                        VisualisationRow("    X   "),
                        VisualisationRow(" III  II", 3.0f)
                    ),
                    xAxis = RawXAxis(
                        markers = "|      |",
                        values = listOf(0.0f, 1.0f)
                    )
                )
            )
        }.let { e ->
            assertEquals("1 Y axis marker(s) found, and there should be at least two!", e.message)
        }
    }

    @Test
    fun yAxisFirstColumnDoesNotHaveMarker() {
        assertFailsWith<IllegalArgumentException> {
            readYAxisMarkers(
                RawVisualisation(
                    visualisationRows = listOf(
                        VisualisationRow("    X   "),
                        VisualisationRow(" III  II", 1.0f),
                        VisualisationRow(" III  II", 0.0f)
                    ),
                    xAxis = RawXAxis(
                        markers = "|      |",
                        values = listOf(0.0f, 1.0f)
                    )
                )
            )
        }.let { e ->
            assertEquals("Y axis should have markers for first and last row!", e.message)
        }
    }

    @Test
    fun yAxisLastColumnDoesNotHaveMarker() {
        assertFailsWith<IllegalArgumentException> {
            readYAxisMarkers(
                RawVisualisation(
                    visualisationRows = listOf(
                        VisualisationRow("    X   ", 1.0f),
                        VisualisationRow(" III  II", 0.0f),
                        VisualisationRow(" III  II")
                    ),
                    xAxis = RawXAxis(
                        markers = "|      |",
                        values = listOf(0.0f, 1.0f)
                    )
                )
            )
        }.let { e ->
            assertEquals("Y axis should have markers for first and last row!", e.message)
        }
    }
}
