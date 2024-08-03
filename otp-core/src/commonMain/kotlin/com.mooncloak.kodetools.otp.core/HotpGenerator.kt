package com.mooncloak.kodetools.otp.core

import io.matthewnelson.encoding.base32.Base32Default
import io.matthewnelson.encoding.core.Decoder.Companion.decodeToByteArray
import kotlin.math.pow

/**
 *
 * @see [Java Reference Implementation](https://github.com/BastiaanJansen/otp-java/blob/main/src/main/java/com/bastiaanjansen/otp/HOTPGenerator.java)
 */
public class HOTPGenerator public constructor(
    private val secret: ByteArray,
    public val passwordLength: Int = DEFAULT_PASSWORD_LENGTH,
    public val algorithm: HMACAlgorithm = DEFAULT_HMAC_ALGORITHM
) {

    init {
        require(secret.isNotEmpty()) { "Secret must not be empty" }

        require(passwordLengthIsValid(passwordLength)) { "Password length must be between 6 and 8 digits" }
    }

    public fun verify(code: String, counter: Long, delayWindow: Int = 0): Boolean {
        if (code.length != passwordLength) return false

        for (i in -delayWindow..delayWindow) {
            val currentCode = generate(counter + i)
            if (code == currentCode) return true
        }

        return false
    }

    @Throws(IllegalStateException::class)
    public fun generate(counter: Long): String {
        require(counter >= 0) { "Counter must be greater than or equal to 0" }

        val secretBytes = decodeBase32(secret)
        val counterBytes = longToBytes(counter)

        val hash = generateHash(secretBytes, counterBytes)

        return getCodeFromHash(hash)
    }

    /**
     * Decode a base32 value to bytes array
     *
     * @param value base32 value
     * @return bytes array
     */
    private fun decodeBase32(value: ByteArray): ByteArray =
        value.decodeToByteArray(Base32Default())

    private fun longToBytes(value: Long): ByteArray =
        value.toByteArray()

    private fun generateHash(secret: ByteArray, data: ByteArray): ByteArray {
        // Create a secret key with correct SHA algorithm
        // FIXME:
        // Old Java Impl: val signKey = SecretKeySpec(secret, "RAW")

        // Mac is 'message authentication code' algorithm (RFC 2104)
        val mac = algorithm.getMac(secret)

        // Hash data with generated sign key
        return mac.doFinal(data)
    }

    private fun getCodeFromHash(hash: ByteArray): String {
        /* Find mask to get last 4 digits:
        1. Set all bits to 1: ~0 -> 11111111 -> 255 decimal -> 0xFF
        2. Shift n (in this case 4, because we want the last 4 bits) bits to left with <<
        3. Negate the result: 1111 1100 -> 0000 0011
         */
        val mask = (0.inv() shl 4).inv()

        /* Get last 4 bits of hash as offset:
        Use the bitwise AND (&) operator to select last 4 bits
        Mask should be 00001111 = 15 = 0xF
        Last byte of hash & 0xF = last 4 bits:
        Example:
        Input: decimal 219 as binary: 11011011 &
        Mask: decimal 15 as binary:   00001111
        -----------------------------------------
        Output: decimal 11 as binary: 00001011
         */
        val lastByte = hash[hash.size - 1]
        val offset = lastByte.toInt() and mask

        // Get 4 bytes from hash from offset to offset + 3
        val truncatedHashInBytes = byteArrayOf(
            hash[offset],
            hash[offset + 1], hash[offset + 2], hash[offset + 3]
        )

        // Wrap in ByteBuffer to convert bytes to long
        var truncatedHash = truncatedHashInBytes.toLong()

        // Mask most significant bit
        truncatedHash = truncatedHash and 0x7FFFFFFFL

        // Modulo (%) truncatedHash by 10^passwordLength
        truncatedHash %= 10.0.pow(passwordLength).toLong()

        // Left pad with 0s for an n-digit code
        return truncatedHash.toString().padStart(
            length = passwordLength,
            padChar = '0'
        )
    }

    private fun passwordLengthIsValid(passwordLength: Int): Boolean =
        passwordLength in 6..8

    public companion object {

        internal const val DEFAULT_PASSWORD_LENGTH = 6
        internal val DEFAULT_HMAC_ALGORITHM = HMACAlgorithm.SHA256
    }
}
