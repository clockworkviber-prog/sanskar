package com.sanskar.app.data

import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * PBKDF2-HMAC-SHA256 password hashing with a random per-user salt.
 * Plain-text passwords are never persisted — only the salt and derived hash.
 */
object PasswordHasher {

    private const val ITERATIONS = 120_000
    private const val KEY_LENGTH = 256

    private fun hex(bytes: ByteArray): String = bytes.joinToString("") { "%02x".format(it) }
    private fun unhex(s: String): ByteArray =
        ByteArray(s.length / 2) { i -> ((Character.digit(s[i * 2], 16) shl 4) + Character.digit(s[i * 2 + 1], 16)).toByte() }

    fun newSalt(): String {
        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)
        return hex(salt)
    }

    fun hash(password: String, saltHex: String): String {
        val spec = PBEKeySpec(password.toCharArray(), unhex(saltHex), ITERATIONS, KEY_LENGTH)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        return hex(factory.generateSecret(spec).encoded)
    }

    fun verify(password: String, saltHex: String, expectedHash: String): Boolean =
        hash(password, saltHex) == expectedHash
}
