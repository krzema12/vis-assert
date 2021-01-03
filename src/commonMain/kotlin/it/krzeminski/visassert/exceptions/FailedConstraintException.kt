package it.krzeminski.visassert.exceptions

class FailedConstraintException(override val message: String, override val cause: Throwable? = null) : Exception(message)
