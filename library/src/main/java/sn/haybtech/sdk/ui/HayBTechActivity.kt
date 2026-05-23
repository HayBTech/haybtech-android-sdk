package sn.haybtech.sdk.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import sn.haybtech.sdk.R

class HayBTechActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PAYMENT_URL = "extra_payment_url"
        const val RESULT_STATUS = "result_status"
        
        const val STATUS_SUCCESS = "success"
        const val STATUS_CANCELLED = "cancelled"
        const val STATUS_FAILED = "failed"
    }

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Dynamic layout creation to avoid XML dependency in library if possible,
        // but for a real SDK, a layout file is better.
        // For simplicity here, we assume a basic layout.
        setContentView(android.R.layout.activity_list_item) // Placeholder
        
        val url = intent.getStringExtra(EXTRA_PAYMENT_URL) ?: finish().run { return }

        webView = WebView(this).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    val currentUrl = request?.url?.toString() ?: ""
                    
                    if (currentUrl.contains("/success") || currentUrl.contains("status=success")) {
                        finishWithResult(STATUS_SUCCESS)
                        return true
                    } else if (currentUrl.contains("/cancel") || currentUrl.contains("status=cancelled")) {
                        finishWithResult(STATUS_CANCELLED)
                        return true
                    } else if (currentUrl.contains("/failed") || currentUrl.contains("status=failed")) {
                        finishWithResult(STATUS_FAILED)
                        return true
                    }
                    
                    return false
                }
            }
        }

        setContentView(webView)
        webView.loadUrl(url)
    }

    private fun finishWithResult(status: String) {
        val data = android.content.Intent().apply {
            putExtra(RESULT_STATUS, status)
        }
        setResult(RESULT_OK, data)
        finish()
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            finishWithResult(STATUS_CANCELLED)
        }
    }
}
