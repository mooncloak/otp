package com.mooncloak.kodetools.otp.core

import org.kotlincrypto.endians.BigEndian
import org.kotlincrypto.endians.BigEndian.Companion.toBigEndian

internal fun Long.toByteArray(): ByteArray =
    this.toBigEndian().toByteArray()

internal fun ByteArray.toLong(): Long {
    require(this.size == 8) { "ByteArray must have a size of 8 to convert to Long." }

    val endian = BigEndian(
        this[0],
        this[1],
        this[2],
        this[3],
        this[4],
        this[5],
        this[6],
        this[7]
    )

    return endian.toLong()
}
