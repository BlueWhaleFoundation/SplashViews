package foundation.bluewhale.splashviews.security

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import foundation.bluewhale.splashviews.fingerprint.FingerPrintSaver
import java.security.Key
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.GCMParameterSpec

class FingerPrintTool(context: Context) {
    private val KEYSTORE = "AndroidKeyStore"
    private val KEY_ALIAS = "splash__key"
    private var keyStore: KeyStore? = null
    private var IVByte: ByteArray? = null

    init {
        if (isContainsAlias() && FingerPrintSaver.getRandomIv(context) != null)
            setIVByte(FingerPrintSaver.getRandomIv(context))
        else if (isContainsAlias() && FingerPrintSaver.getRandomIv(context) == null) {
            FingerPrintSaver.setRandomIv(context, getKeyStoreIV())
            setIVByte(getKeyStoreIV())
            FingerPrintSaver.setEncryptedKey(context, null)
            FingerPrintSaver.setUseFingerPrint(context, false)
        } else {
            FingerPrintSaver.setRandomIv(context, getNewIV())
            setIVByte(getKeyStoreIV())
            FingerPrintSaver.setEncryptedKey(context, null)
            FingerPrintSaver.setUseFingerPrint(context, false)
        }
    }

    @Throws(Exception::class)
    fun encrypt(plainTxt: String): String {
        val cipher = getCipher()
        cipher?.let {
            it.init(Cipher.ENCRYPT_MODE, getKey(), GCMParameterSpec(128, IVByte!!))

            val plainByte = plainTxt.toByteArray(charset("UTF-8"))
            //byte[] plainByte = Base64.decode(plainTxt, Base64.DEFAULT);
            val encryptedByte = cipher.doFinal(plainByte)
            return Base64.encodeToString(encryptedByte, Base64.DEFAULT)
        }
        return ""
    }

    fun decrypt(encryptedTxt: String): String? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val cipher = getCipher()
            cipher?.also {
                it.init(Cipher.DECRYPT_MODE, getKey(), GCMParameterSpec(128, IVByte!!))
                val encryptedByte = Base64.decode(encryptedTxt, Base64.DEFAULT)
                val plainByte = it.doFinal(encryptedByte)
                //return Base64.encodeToString(plainByte, Base64.DEFAULT);

                return String(plainByte, Charsets.UTF_8)
                //return String(plainByte, "UTF-8")
            }
        }

        return null
    }

    @Throws(Exception::class)
    fun getCipher(): Cipher? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            //cipher.init(Cipher.ENCRYPT_MODE, getKey())
            return cipher
        } else
            return null
    }

    fun getCipherInEcryptMode(): Cipher? {
        val cipher = getCipher()
        cipher?.let {
            it.init(Cipher.ENCRYPT_MODE, getKey())
            return it
        }
        return null
    }

    @Throws(Exception::class)
    fun isContainsAlias(): Boolean {
        keyStore = KeyStore.getInstance(KEYSTORE)
        keyStore!!.load(null)
        return keyStore!!.containsAlias(KEY_ALIAS)
    }

    @Throws(Exception::class)
    fun getNewIV(): String? {
        val cipher = getCipher()
        cipher?.also {
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE)
            keyGenerator.init(
                KeyGenParameterSpec.Builder(KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setRandomizedEncryptionRequired(false)
                    .build()
            )
            val key = keyGenerator.generateKey()

            it.init(Cipher.ENCRYPT_MODE, key)
            return Base64.encodeToString(it.iv, Base64.DEFAULT)
        }

        return null
    }

    fun setIVByte(IVString: String?) {
        this.IVByte = Base64.decode(IVString, Base64.DEFAULT)
    }

    @Throws(Exception::class)
    fun getKeyStoreIV(): String? {
        val cipher = getCipher()
        cipher?.also {
            it.init(Cipher.ENCRYPT_MODE, getKey())
            return Base64.encodeToString(it.iv, Base64.DEFAULT)
        }
        return null
    }

    @Throws(Exception::class)
    fun getKey(): Key {
        keyStore = KeyStore.getInstance(KEYSTORE)
        keyStore!!.load(null)
        return keyStore!!.getKey(KEY_ALIAS, null)
    }

    fun saveValidatedPassword(context: Context, password: String) {
        FingerPrintSaver.setEncryptedKey(context, encrypt(password))
        FingerPrintSaver.setUseFingerPrint(context, true)
    }
}