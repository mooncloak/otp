package com.mooncloak.kodetools.otp.core

import com.mooncloak.kodetools.otp.core.HOTPGenerator.Companion.DEFAULT_HMAC_ALGORITHM
import com.mooncloak.kodetools.otp.core.HOTPGenerator.Companion.DEFAULT_PASSWORD_LENGTH
import kotlinx.datetime.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

public object Otp {

    /**
     * Creates a [HOTPGenerator] instance with the provided values.
     */
    public fun hotp(
        secret: ByteArray,
        passwordLength: Int = DEFAULT_PASSWORD_LENGTH,
        algorithm: HMACAlgorithm = DEFAULT_HMAC_ALGORITHM
    ): HOTPGenerator = HOTPGenerator(
        secret = secret,
        passwordLength = passwordLength,
        algorithm = algorithm
    )

    /**
     * Creates a [TOTPGenerator] with the provided values.
     */
    public fun totp(
        secret: ByteArray,
        period: Duration = 30.seconds,
        clock: Clock = Clock.System,
        passwordLength: Int = DEFAULT_PASSWORD_LENGTH,
        algorithm: HMACAlgorithm = DEFAULT_HMAC_ALGORITHM
    ): TOTPGenerator = TOTPGenerator(
        secret = secret,
        period = period,
        clock = clock,
        passwordLength = passwordLength,
        algorithm = algorithm
    )
}
