package sn.haybtech.sdk

import android.content.Context
import android.content.Intent
import sn.haybtech.sdk.ui.HayBTechActivity

/**
 * HayBTech Android SDK - Hardened for Security.
 */
object HayBTech {
    private var publicKey: String? = null

    /**
     * SECURITY WARNING: Never use your Secret Key (sk_...) in an Android app.
     * All sensitive operations must be done on your backend server.
     */
    fun init(publicKey: String) {
        if (!publicKey.startsWith("pk_")) {
            throw IllegalArgumentException(
                "[HayBTech] Invalid Public Key. For security reasons, the Android SDK only accepts Public Keys (pk_...). " +
                "Do NOT use your Secret Key in mobile apps as it can be easily extracted by decompiling the APK."
            )
        }
        this.publicKey = publicKey
    }

    /**
     * Start the payment checkout process.
     */
    fun checkout(context: Context, paymentUrl: String) {
        if (publicKey == null) {
            throw IllegalStateException("[HayBTech] SDK not initialized. Call HayBTech.init(publicKey) first.")
        }

        if (!paymentUrl.startsWith("https://")) {
            throw IllegalArgumentException("[HayBTech] paymentUrl must be a secure HTTPS URL.")
        }

        val intent = Intent(context, HayBTechActivity::class.java).apply {
            putExtra(HayBTechActivity.EXTRA_PAYMENT_URL, paymentUrl)
        }
        context.startActivity(intent)
    }
}
