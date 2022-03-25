package com.aeye.thirdeye

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView

class LicenseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_license)

        val webView = findViewById<WebView>(R.id.webView_license)
//        webView.loadUrl("file:///android_asset/html/LICENSE-2.0.txt")
        webView.loadUrl("file:///android_asset/html/apache2.html")
//        webView.loadUrl("file:///android_asset/html/apache2.0.html")
    }
}