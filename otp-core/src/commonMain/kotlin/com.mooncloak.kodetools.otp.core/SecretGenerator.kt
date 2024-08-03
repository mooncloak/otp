package com.mooncloak.kodetools.otp.core

import io.matthewnelson.encoding.base32.Base32Default
import io.matthewnelson.encoding.core.Encoder.Companion.encodeToByteArray
import org.kotlincrypto.SecureRandom
import kotlin.random.Random

/**
 * A secret generator to generate OTP secrets
 *
 * @see [Java Reference Implementation](https://github.com/BastiaanJansen/otp-java/blob/main/src/main/java/com/bastiaanjansen/otp/SecretGenerator.java)
 */
public object SecretGenerator {

    /**
     * Default amount of bits for secret generation
     */
    private const val DEFAULT_BITS: Int = 160

    private val encoder = Base32Default()

    /**
     * Generate an OTP base32 secret with default amount of bits
     *
     * @param bits length, this should be greater than or equal to the length of the HMAC
     * algorithm type:
     *     - SHA1: 160 bits
     *     - SHA256: 256 bits
     *     - SHA512: 512 bits
     *
     * @return generated secret
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public fun generate(bits: Int = DEFAULT_BITS): ByteArray {
        require(bits > 0) { "Bits must be greater than or equal to 0" }

        val bytes = ByteArray(bits / Byte.SIZE_BITS)

        SecureRandom().nextBytesCopyTo(bytes)

        return bytes.encodeToByteArray(encoder)
    }

    public fun generate(algorithm: HMACAlgorithm): ByteArray =
        when (algorithm) {
            HMACAlgorithm.SHA1 -> generate(bits = 160)
            HMACAlgorithm.SHA224 -> generate(bits = 224)
            HMACAlgorithm.SHA256 -> generate(bits = 256)
            HMACAlgorithm.SHA384 -> generate(bits = 384)
            HMACAlgorithm.SHA512 -> generate(bits = 512)
        }
}

/**
 * Generates a secret [ByteArray] value for use with the OTP code generators, for this
 * [HMACAlgorithm].
 */
public fun HMACAlgorithm.generateSecret(): ByteArray =
    SecretGenerator.generate(algorithm = this)
