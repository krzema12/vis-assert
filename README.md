[![Build Status](https://travis-ci.com/krzema12/vis-assert.svg?branch=master)](https://travis-ci.com/krzema12/vis-assert) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/it.krzeminski.vis-assert/vis-assert/badge.svg)](https://maven-badges.herokuapp.com/maven-central/it.krzeminski.vis-assert/vis-assert) [![codecov](https://codecov.io/gh/krzema12/vis-assert/branch/master/graph/badge.svg)](https://codecov.io/gh/krzema12/vis-assert)

# 🧪 This library is experimental!

Its API is not stabilized yet, and writing tests is still a bit tedious. Use at your own risk. Looking forward to your feedback :)

# What is vis-assert?

It's a Kotlin library to write visually appealing ASCII-art-like test assertions for math functions. For example, you
can test that your `(Float) -> Float` function describing a sine wave produces proper values. Or if you have a game
where the player jumps, you can describe player's vertical position as a function of time - you could test this function
to make sure that the jump movement is fluent and fast enough.

Under the hood, each such ASCII visualisation is translated into a collection of *constraints*, where each constraint
looks at a single X value of the function and performs a certain check on its Y value at this point.

# Installation

In your `build.gradle` or `build.gradle.kts`:

```
repositories {
    jcenter()
}

dependencies {
    testCompile("it.krzeminski.vis-assert:vis-assert:0.4.0-beta")
}

// or

kotlin {
    sourceSets {
        val ...Test by getting {
            dependencies {
                implementation("it.krzeminski.vis-assert:vis-assert:0.4.0-beta")
            }
        }
    }
}
```

# Examples

```kotlin
@Test
fun sineWaveFor2HzOnePeriod() {
    assertFunctionConformsTo(
            functionUnderTest = sineWave(frequency = 2.0f),
            visualisation = {
                row(1.0f,   "        IIXII                            ")
                row(        "     III     III                         ")
                row(        "    I           I                        ")
                row(        "  II             II                      ")
                row(        " I                 I                     ")
                row(0.0f,   "X                   I                   I")
                row(        "                     I                 I ")
                row(        "                      II             II  ")
                row(        "                        I           I    ")
                row(        "                         III     III     ")
                row(-1.0f,  "                            IIIII        ")
                xAxis {
                    markers("|                   |                   |")
                    values( 0.0f,               0.25f,              0.5f)
                }
            })
}
```

or for high-frequency function and higher sampling:

```kotlin
@Test
fun assertFunctionConformsToForHighFrequencyFunctionWhenAssertionsAreFulfilledAndSamplingHigherThan1IsUsed() {
    assertFunctionConformsTo(
        functionUnderTest = { x: Float -> (sin(100*x) * sin(x) * x * 0.3).toFloat() },
        samplesPerCharacter = 100,
        visualisation = {
            row( 2.0f,  "                                                                   ")
            row(        "                                                                   ")
            row(        "                                               IIIIIIIIIIIIII      ")
            row( 1.0f,  "                                           IIIIIIIIIIIIIIIIIIIII   ")
            row(        "                   IIIIIIII             IIIIIIIIIIIIIIIIIIIIIIIIIII")
            row(        "         IIIIIIIIIIIIIIIIIIIIIIII   IIIIIIIIIIIIIIIIIIIIIIIIIIIIIII")
            row( 0.0f,  "IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII")
            row(        "         IIIIIIIIIIIIIIIIIIIIIIII    IIIIIIIIIIIIIIIIIIIIIIIIIIIIII")
            row(        "                  IIIIIIII              IIIIIIIIIIIIIIIIIIIIIIIIIII")
            row(-1.0f,  "                                            IIIIIIIIIIIIIIIIIIII   ")
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
```

Where:

* `I` characters mean that for a given X argument, the function's value can be in a certain range around a given Y
  value. Also, this constraint is "strict", which means that making it wider or narrower vertically would make the
  assertion fail. In this example, each `I` character has a tolerance of +/- 0.1. The tolerance is calculated based on
  the vertical axis description.
* `X` characters mean that for a given X argument, the function's value has to **exactly** match the given Y value.

There's also `i` constraint, which just checks that all values are in a certain range.

More examples can be found in unit tests for [krzema12/fsynth](https://github.com/krzema12/fsynth) - a project that
vis-assert was created for.

# Limitations

* the library performs sampling, as given by the `xAxis` description and `samplesPerCharacter` parameter. It means that
  if two subsequent X values are 0.2 and 0.3, and not enough sampling rate is given, the library may not check what
  happens for 0.25 or 0.20001. In most cases, such simple sampling is enough.
* only `(Float) -> Float` functions are currently supported. Mitigation: it's possible to assert on any other function,
  as long as it can be presented as a `(Float) -> Float` function. See [this example](https://github.com/krzema12/fsynth/blob/feb05893b14fba0f7a780dc546d1ad806bb2bfbf/core/src/test/kotlin/it/krzeminski/fsynth/RenderingTest.kt#L23)
  for adapting an `(Int) -> Float` function
* when assertions fail, the current message just says about failed first (x, y) constraint, going from the left. It's
  thus quite time-consuming to write a test. Ideally, if the assertion fails, vis-assert should show how the ASCII
  visualisation could look like.
