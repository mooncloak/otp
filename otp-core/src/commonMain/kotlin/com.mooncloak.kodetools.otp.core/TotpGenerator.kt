package com.mooncloak.kodetools.otp.core

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * @see [Java Reference Implementation](https://github.com/BastiaanJansen/otp-java/blob/main/src/main/java/com/bastiaanjansen/otp/TOTPGenerator.java)
 */
public class TOTPGenerator public constructor(
    secret: ByteArray,
    @Suppress("MemberVisibilityCanBePrivate") public val period: Duration = 30.seconds,
    @Suppress("MemberVisibilityCanBePrivate") public val clock: Clock = Clock.System,
    @Suppress("MemberVisibilityCanBePrivate") public val passwordLength: Int = HOTPGenerator.DEFAULT_PASSWORD_LENGTH,
    @Suppress("MemberVisibilityCanBePrivate") public val algorithm: HMACAlgorithm = HOTPGenerator.DEFAULT_HMAC_ALGORITHM
) {

    private val hotpGenerator: HOTPGenerator = HOTPGenerator(
        secret = secret,
        passwordLength = passwordLength,
        algorithm = algorithm
    )

    init {
        require(period.inWholeSeconds >= 1) { "Period must be at least 1 second" }
    }

    @Throws(IllegalStateException::class)
    public fun now(clock: Clock = this.clock): String {
        val counter = calculateCounter(clock, period)

        return hotpGenerator.generate(counter)
    }

    @Throws(IllegalStateException::class)
    public fun at(instant: Instant): String =
        at(instant.epochSeconds)

    @Throws(IllegalStateException::class)
    public fun at(date: LocalDate): String {
        val secondsSince1970 = date.atStartOfDayIn(TimeZone.currentSystemDefault()).epochSeconds

        return at(secondsSince1970)
    }

    @Throws(IllegalArgumentException::class)
    public fun at(secondsPast1970: Long): String {
        require(validateTime(secondsPast1970)) { "Time must be above zero" }

        val counter = calculateCounter(secondsPast1970, period)

        return hotpGenerator.generate(counter)
    }

    /**
     * Checks whether a code is valid for a specific counter taking a delay window into account
     *
     * @param code an OTP code
     * @param delayWindow window in which a code can still be deemed valid
     *
     * @return a boolean, true if code is valid, otherwise false
     */
    public fun verify(code: String, delayWindow: Int = 0, clock: Clock = this.clock): Boolean {
        val counter = calculateCounter(clock, period)

        return hotpGenerator.verify(code, counter, delayWindow)
    }

    /**
     * Calculates time until next time window will be reached and a new totp should be generated
     *
     * @return a duration object with duration until next time window
     */
    public fun durationUntilNextTimeWindow(clock: Clock = this.clock): Duration {
        val timeInterval = period.inWholeMilliseconds

        return (timeInterval - clock.now().toEpochMilliseconds() % timeInterval).milliseconds
    }

    private fun calculateCounter(secondsPast1970: Long, period: Duration): Long =
        secondsPast1970.seconds.inWholeMilliseconds / period.inWholeMilliseconds

    private fun calculateCounter(clock: Clock, period: Duration): Long =
        clock.now().toEpochMilliseconds() / period.inWholeMilliseconds

    private fun validateTime(time: Long): Boolean =
        time > 0
}
