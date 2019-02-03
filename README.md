[![Build Status](https://travis-ci.com/krzema12/PlotAssert.svg?branch=master)](https://travis-ci.com/krzema12/PlotAssert)

# What is PlotAssert?

It's a Kotlin library to write visually appealing ASCII-art-like test assertions for math functions. For example, you
can test that your `(Float) -> Float` function describing a sine wave produces proper values. Or if you have a game
where the player jumps, you can describe player's vertical position as a function of time - you could test this function
to make sure that the jump movement is fluent and fast enough.

Under the hood, each such ASCII visualisation is translated into a collection of *constraints*, where each constraint
looks at a single X value of the function and performs a certain check on its Y value at this point.

# Example

```kotlin
@Test
fun sineWaveFor2HzOnePeriod() {
    assertFunctionConformsTo(
            functionUnderTest = sineWaveOnePeriod(2.0f),
            visualisation = {
                row(1.0f,   "        IIXII                                                                    ")
                row(        "     III     III                                                                 ")
                row(        "    I           I                                                                ")
                row(        "  II             II                                                              ")
                row(        " I                 I                                                             ")
                row(0.0f,   "X                   I                   IXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")
                row(        "                     I                 I                                         ")
                row(        "                      II             II                                          ")
                row(        "                        I           I                                            ")
                row(        "                         III     III                                             ")
                row(-1.0f,  "                            IIIII                                                ")
                xAxis {
                    markers("|                   |                   |                   |                   |")
                    values( 0.0f,               0.25f,              0.5f,               0.75f,              1.0f)
                }
            })
}
```

Where:

* `I` characters mean that for a given X argument, the function's value can be in a certain range around a given Y
  value. In this example, each `I` character has a tolerance of +/- 0.1. The tolerance is calculated based on the
  vertical axis description.
* `X` characters mean that for a given X argument, the function's value has to **exactly** match the given Y value.

More examples can be found in unit tests for [krzema12/fsynth](https://github.com/krzema12/fsynth) - a project that
PlotAssert was created for.

# Limitations

* the library performs sampling, as given by the `xAxis` description. It means that if two subsequent X values are 0.2
  and 0.3, the library doesn't check what happens in between, for example for 0.25 or 0.20001. In most cases, though,
  it's enough (or rather way better than nothing).
* only `(Float) -> Float` functions are currently supported. Mitigation: it's possible to assert on any other function,
  as long as it can be presented as a `(Float) -> Float` function. See [this example](https://github.com/krzema12/fsynth/blob/feb05893b14fba0f7a780dc546d1ad806bb2bfbf/core/src/test/kotlin/it/krzeminski/fsynth/RenderingTest.kt#L23)
  for adapting an `(Int) -> Float` function
* when assertions fail, the current message just says about failed first (x, y) constraint, going from the left. It's
  thus quite time-consuming to write a test. Ideally, if the assertion fails, PlotAssert should show how the ASCII
  visualisation could look like.

# Installation

In your `build.gradle`:

```
repositories {
    maven {
        url "https://dl.bintray.com/krzema1212/it.krzeminski"
    }
}

dependencies {
    testCompile "it.krzeminski:PlotAssert:1.0.0"
}

```
