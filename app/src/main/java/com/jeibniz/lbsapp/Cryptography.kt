package com.jeibniz.lbsapp

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import java.io.ByteArrayOutputStream
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec


class Cryptography {

    fun generateSecretKey() {

        // Define key specs
        val spec = KeyGenParameterSpec
            // Name of the the key in the storage and state that is it for encryption/decryption
            .Builder(KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT
                    or KeyProperties.PURPOSE_DECRYPT)
            // Set block mode for symmetric encryption, CBC
            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            // Padding schema, PKCS7
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            // Key size = 128
            .setKeySize(128)
            // Create an instance of KeyGenParameterSpec
            .build()

        // Create a KeyGenerator object made for AES Algorithm with the name of the key provider
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEY_STORE)
        // Initialize the key generator with spec parameter sets
        keyGenerator.init(spec)
        // Generate a key, it will be automatically stored in KEY_STORE
        keyGenerator.generateKey()
    }

    private fun getSecretKey(): SecretKey {
        // Get KEY_STORE keystore instance, with null password
        val keyStore = KeyStore.getInstance(KEY_STORE).apply { load(null) }
        // Fetch the key by its name, with null password
        val secretKeyEntry = keyStore.getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry
        // Return key
        return secretKeyEntry.secretKey
    }

    fun makeAes(rawMessage: ByteArray, cipherMode: Int): ByteArray? {
        return try {
            // Create a cipher that applies INSTANCE_MODE transformation to the input
            val cipher: Cipher = Cipher.getInstance(INSTANCE_MODE)
            val output: ByteArray

            // If cipherMode is set to decryption
            if(cipherMode == 2) {
                // Get the IV from the input
                val ivParameterSpec = IvParameterSpec(rawMessage.take(16).toByteArray())
                // Initialize the cipherMode with decryption, secretkey, and the IV
                cipher.init(cipherMode, this.getSecretKey(), ivParameterSpec)
                // Return the last 16 bytes, which include the message, first 16 are the IV
                output = cipher.doFinal(rawMessage.copyOfRange(16, rawMessage.size))
                output
            // If cipherMode is set to Encryption
            } else {
                // Initialize with encryption and our secret key
                cipher.init(cipherMode, this.getSecretKey())
                // Encryption
                output = cipher.doFinal(rawMessage)

                // Putting the IV at the beginning of the message for later decryption
                var outputStream = ByteArrayOutputStream()
                outputStream.write(cipher.iv)
                outputStream.write(output)
                // Converting to ByteArray and outputing
                outputStream.toByteArray()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    companion object {
        const val KEY_ALIAS: String = "demoKey"
        const val KEY_STORE: String = "AndroidKeyStore"
        const val INSTANCE_MODE: String = "AES/CBC/PKCS7Padding"
    }

}