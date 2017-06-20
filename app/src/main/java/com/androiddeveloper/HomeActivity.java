package com.androiddeveloper;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import im.delight.android.webview.AdvancedWebView;

/**
 * Created by @mohit on 13/6/17.
 */

@SuppressWarnings("ALL")
public class HomeActivity extends AppCompatActivity implements AdvancedWebView.Listener {
    private AdvancedWebView mWebView;
    private Button btn_Internet;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mWebView = (AdvancedWebView) findViewById(R.id.webview);
        btn_Internet = (Button) findViewById(R.id.btn_Internet);
        mWebView.setListener(this, this);
        mWebView.loadUrl(getString(R.string.url));
        mWebView.getSettings().setSupportMultipleWindows(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setScrollbarFadingEnabled(true);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setDesktopMode(true);
        // disable third-party cookies only
        mWebView.setThirdPartyCookiesEnabled(false);
        // or disable cookies in general
        mWebView.setCookiesEnabled(false);

        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                AdvancedWebView newWebView = new AdvancedWebView(HomeActivity.this);
                // myParentLayout.addView(newWebView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newWebView);
                resultMsg.sendToTarget();
                return true;
            }
        });

        showBannerAd();
        initInterstitialAd();
    }

    // Show Banner Ads
    private void showBannerAd() {
        mAdView = (AdView) findViewById(R.id.adView);
        // For Testing Purpose
//        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                // Check the LogCat to get your test device ID
//                .addTestDevice("9E847D99F08C0028B6613E597754B38A")
//                .build();
        mAdView.loadAd(new AdRequest.Builder().build());
        mAdView.setAdListener(new AdListener() {
            public void onAdLoaded() {
                Log.e("Banner","onAdLoaded");
            }

            @Override
            public void onAdClosed() {
                Log.e("Banner","onAdClosed");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.e("Banner","onAdFailedToLoad>>"+errorCode);
            }

            @Override
            public void onAdLeftApplication() {
                Log.e("Banner","onAdLeftApplication");
            }

            @Override
            public void onAdOpened() {
                Log.e("Banner","onAdOpened");
            }
        });
    }

    // Initialize InterstitialAd
    private void initInterstitialAd() {
        mInterstitialAd = new InterstitialAd(this);
        // set the ad unit ID
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));
        loadInterstitialAds();
    }

    // Show Interstitial Ads
    private void showInterstitial() {
        // Show Ads
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            loadInterstitialAds();
        }

        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                Log.e("Interstitial","onAdLoaded");
            }

            @Override
            public void onAdClosed() {
                Log.e("Interstitial","onAdClosed");
                // Load the next interstitial.
                loadInterstitialAds();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.e("Interstitial","onAdFailedToLoad>>"+errorCode);
            }

            @Override
            public void onAdLeftApplication() {
                Log.e("Interstitial","onAdLeftApplication");
            }

            @Override
            public void onAdOpened() {
                Log.e("Interstitial","onAdOpened");
            }
        });
    }

    private void loadInterstitialAds() {
        // Request a new ad if one isn't already loaded, hide the button, and kick off the timer.
        if (!mInterstitialAd.isLoading() && !mInterstitialAd.isLoaded()) {
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();
        if (!isNetworkAvailable()) {
            mWebView.setVisibility(View.GONE);
            btn_Internet.setVisibility(View.VISIBLE);
            mAdView.setVisibility(View.GONE);
            return;
        }
        mWebView.setVisibility(View.VISIBLE);
        btn_Internet.setVisibility(View.GONE);
        mAdView.setVisibility(View.VISIBLE);

        mWebView.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void onPause() {
        mWebView.onPause();
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mWebView.onDestroy();
        if (mAdView != null) {
            mAdView.destroy();
        }
        if (mInterstitialAd != null) {
            mInterstitialAd = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        mWebView.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public void onBackPressed() {
        if (!mWebView.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
    }

    @Override
    public void onPageFinished(String url) {
        showInterstitial();
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {
    }

    @Override
    public void onExternalPageRequest(String url) {
    }

    // Check Internet Available Or Not
    private boolean isNetworkAvailable() {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}