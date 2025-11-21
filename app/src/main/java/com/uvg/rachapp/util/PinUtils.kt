package com.uvg.rachapp.util

fun hashPin(pin: String): String {
    val md = java.security.MessageDigest.getInstance("SHA-256")
    val bytes = md.digest(pin.toByteArray(Charsets.UTF_8))
    return bytes.joinToString("") { "%02x".format(it) }
}