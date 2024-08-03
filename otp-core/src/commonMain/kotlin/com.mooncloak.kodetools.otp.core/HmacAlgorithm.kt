package com.mooncloak.kodetools.otp.core

import org.kotlincrypto.macs.hmac.Hmac
import org.kotlincrypto.macs.hmac.sha1.HmacSHA1
import org.kotlincrypto.macs.hmac.sha2.HmacSHA224
import org.kotlincrypto.macs.hmac.sha2.HmacSHA256
import org.kotlincrypto.macs.hmac.sha2.HmacSHA384
import org.kotlincrypto.macs.hmac.sha2.HmacSHA512

/**
 * HMAC algorithm enum
 *
 * @see [Java Reference Implementation](https://github.com/BastiaanJansen/otp-java/blob/main/src/main/java/com/bastiaanjansen/otp/HMACAlgorithm.java)
 */
public enum class HMACAlgorithm(
    public val hMACName: String
) {

    @Deprecated("")
    SHA1(hMACName = "HmacSHA1"),

    SHA224(hMACName = "HmacSHA224"),

    SHA256(hMACName = "HmacSHA256"),

    SHA384(hMACName = "HmacSHA384"),

    SHA512(hMACName = "HmacSHA512")
}

@Suppress("DEPRECATION")
public fun HMACAlgorithm.getMac(key: ByteArray): Hmac =
    when (this) {
        HMACAlgorithm.SHA1 -> HmacSHA1(key)
        HMACAlgorithm.SHA224 -> HmacSHA224(key)
        HMACAlgorithm.SHA256 -> HmacSHA256(key)
        HMACAlgorithm.SHA384 -> HmacSHA384(key)
        HMACAlgorithm.SHA512 -> HmacSHA512(key)
    }
