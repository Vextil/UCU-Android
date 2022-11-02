package com.ucu.marvelheroes.extensions

import java.nio.charset.StandardCharsets.UTF_8
import java.security.MessageDigest

fun String.md5(): ByteArray = MessageDigest.getInstance("MD5").digest(this.toByteArray(UTF_8))
fun ByteArray.toHex() = joinToString(separator = "") { byte -> "%02x".format(byte) }
