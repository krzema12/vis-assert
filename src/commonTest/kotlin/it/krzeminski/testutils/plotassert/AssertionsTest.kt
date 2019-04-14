package it.krzeminski.testutils.plotassert

import it.krzeminski.testutils.plotassert.exceptions.FailedConstraintException
import kotlin.math.sin
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/* ktlint-disable no-multi-spaces paren-spacing */

class AssertionsTest {
    @Test
    fun assertFunctionConformsToWhenAssertionsAreFulfilled() {
        assertFunctionConformsTo(
                functionUnderTest = { 1.0f },
                visualisation = {
                    row(1.0f,   "XXXXX")
                    row(0.0f,   "     ")
                    xAxis {
                        markers("|   |")
                        values(1.0f, 2.0f)
                    }
                }
        )
    }

    private val functionWithHighFrequency = { x: Float -> (sin(100*x) * sin(x) * x * 0.3).toFloat() }

    /**
     * The below test shows that for a high-frequency function, default 1-per-character sampling is too low to catch
     * the envelope of the function. The below assertion doesn't look as expected - it samples the function too rarely.
     */
    @Test
    fun assertFunctionConformsToForHighFrequencyFunctionWhenAssertionsAreFulfilledAndSamplingEqualTo1IsUsed() {
        assertFunctionConformsTo(
            functionUnderTest = functionWithHighFrequency,
            samplesPerCharacter = 1,
            visualisation = {
                row( 2.0f,  "                                                                   ")
                row(        "                                                                   ")
                row(        "                                                   I I             ")
                row( 1.0f,  "                                                            I I    ")
                row(        "                       I                  I I    I     I  I        ")
                row(        "            I I I    I   I I  I I       I     I                 I  ")
                row( 0.0f,  "IIIIIIIIIIII      II        II   IIIIII        I                  I")
                row(        "             I I I  I I   I    I       I        I       II       I ")
                row(        "                        I                I I I                 I   ")
                row(-1.0f,  "                                                  I   I    I I     ")
                row(        "                                                    I              ")
                row(        "                                                                   ")
                row(-2.0f,  "                                                                   ")
                xAxis {
                    markers("|          |          |          |          |          |          |")
                    values( 0.0f,      1.0f,      2.0f,      3.0f,      4.0f,      5.0f,      6.0f)
                }
            }
        )
    }

    /**
     * The below test shows, contrary to the previous test, that higher sampling for high-frequency function presents
     * a visualisation that reflects the nature of the function much better.
     */
    @Test
    fun assertFunctionConformsToForHighFrequencyFunctionWhenAssertionsAreFulfilledAndSamplingHigherThan1IsUsed() {
        assertFunctionConformsTo(
            functionUnderTest = functionWithHighFrequency,
            samplesPerCharacter = 100,
            visualisation = {
                row( 2.0f,  "                                                                   ")
                row(        "                                                                   ")
                row(        "                                               IIIIIIIIIIIIII      ")
                row( 1.0f,  "                                           IIIIIIIIIIIIIIIIIIIII   ")
                row(        "                  IIIIIIIII             IIIIIIIIIIIIIIIIIIIIIIIIIII")
                row(        "        IIIIIIIIIIIIIIIIIIIIIIIII   IIIIIIIIIIIIIIIIIIIIIIIIIIIIIII")
                row( 0.0f,  "IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII")
                row(        "        IIIIIIIIIIIIIIIIIIIIIIIII   IIIIIIIIIIIIIIIIIIIIIIIIIIIIIII")
                row(        "                  IIIIIIIII             IIIIIIIIIIIIIIIIIIIIIIIIIII")
                row(-1.0f,  "                                           IIIIIIIIIIIIIIIIIIIII   ")
                row(        "                                               IIIIIIIIIIIIII      ")
                row(        "                                                                   ")
                row(-2.0f,  "                                                                   ")
                xAxis {
                    markers("|          |          |          |          |          |          |")
                    values( 0.0f,      1.0f,      2.0f,      3.0f,      4.0f,      5.0f,      6.0f)
                }
            }
        )
    }

    @Test
    fun assertFunctionConformsToWhenOneAssertionFails() {
        assertFailsWith<FailedConstraintException> {
            assertFunctionConformsTo(
                    functionUnderTest = { 1.0f },
                    visualisation = {
                        row(1.0f,   "X XXX")
                        row(0.0f,   " X   ")
                        xAxis {
                            markers("|   |")
                            values(1.0f, 2.0f)
                        }
                    }
            )
        }.let { e ->
            assertTrue(e.message in setOf("For x=1.25: 1.0 is not equal to 0.0!", "For x=1.25: 1 is not equal to 0!"))
        }
    }
}

/* ktlint-disable no-multi-spaces paren-spacing */
