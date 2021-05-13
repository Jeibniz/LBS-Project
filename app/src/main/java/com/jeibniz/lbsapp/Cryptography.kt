package com.jeibniz.lbsapp

import java.lang.Exception
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class Cryptography {

    private var secretKey: SecretKey? = null

    init {
        try {
            val keyGenerator: KeyGenerator = KeyGenerator.getInstance("AES")
            keyGenerator.init(256)

            secretKey = keyGenerator.generateKey()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
    }

    fun makeAes(rawMessage: ByteArray, cipherMode: Int): ByteArray? {
        return try {
            val cipher: Cipher = Cipher.getInstance("AES")
            cipher.init(cipherMode, this.secretKey)

            val output: ByteArray = cipher.doFinal(rawMessage)
            output
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}