/*
 * Copyright (C) 2013 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.gms.example.interstitialexample;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressLint("SetTextI18n")
public class MenuInata extends AppCompatActivity {

    private static final long GAME_LENGTH_MILLISECONDS = 3000;
    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712";
    private static final String TAG = "MenuInata";

    private final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);
    private GoogleMobileAdsConsentManager googleMobileAdsConsentManager;
    private InterstitialAd interstitialAd;
    private CountDownTimer countDownTimer;
    private Button retryButton;
    private boolean gamePaused;
    private boolean gameOver;
    private boolean adIsLoading;
    private long timerMilliseconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        Log.d(TAG, "Google Mobile Ads SDK Version: " + MobileAds.getVersion());

        googleMobileAdsConsentManager =
            GoogleMobileAdsConsentManager.getInstance(getApplicationContext());
        googleMobileAdsConsentManager.gatherConsent(
            this,
            consentError -> {
                if (consentError != null) {
                    Log.w(
                        TAG,
                        String.format(
                            "%s: %s",
                            consentError.getErrorCode(),
                            consentError.getMessage()));
                }

                startGame();

                if (googleMobileAdsConsentManager.canRequestAds()) {
                    initializeMobileAdsSdk();
                }

                if (googleMobileAdsConsentManager.isPrivacyOptionsRequired()) {
                    invalidateOptionsMenu();
                }
            });

        if (googleMobileAdsConsentManager.canRequestAds()) {
            initializeMobileAdsSdk();
        }

        retryButton = findViewById(R.id.retry_button);
        retryButton.setVisibility(View.INVISIBLE);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInterstitial();
            }
        });
    }

    private void loadAd() {
    if (adIsLoading || interstitialAd != null) {
      return;
    }
    adIsLoading = true;
    AdRequest adRequest = new AdRequest.Builder().build();
    InterstitialAd.load(
        this,
        AD_UNIT_ID,
        adRequest,
        new InterstitialAdLoadCallback() {
          @Override
          public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
            // The mInterstitialAd reference will be null until
            // an ad is loaded.
            MenuInata.this.interstitialAd = interstitialAd;
            adIsLoading = false;
            Log.i(TAG, "onAdLoaded");
            Toast.makeText(MenuInata.this, "onAdLoaded()", Toast.LENGTH_SHORT).show();
            interstitialAd.setFullScreenContentCallback(
                new FullScreenContentCallback() {
                  @Override
                  public void onAdDismissedFullScreenContent() {
                    // Called when fullscreen content is dismissed.
                    // Make sure to set your reference to null so you don't
                    // show it a second time.
                    MenuInata.this.interstitialAd = null;
                    Log.d("TAG", "The ad was dismissed.");
                  }

                  @Override
                  public void onAdFailedToShowFullScreenContent(AdError adError) {
                    // Called when fullscreen content failed to show.
                    // Make sure to set your reference to null so you don't
                    // show it a second time.
                    MenuInata.this.interstitialAd = null;
                    Log.d("TAG", "The ad failed to show.");
                  }

                  @Override
                  public void onAdShowedFullScreenContent() {
                    // Called when fullscreen content is shown.
                    Log.d("TAG", "The ad was shown.");
                  }
                });
          }

          @Override
          public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
            // Handle the error
            Log.i(TAG, loadAdError.getMessage());
            interstitialAd = null;
            adIsLoading = false;

            String error =
                String.format(
                    java.util.Locale.US,
                    "domain: %s, code: %d, message: %s",
                    loadAdError.getDomain(),
                    loadAdError.getCode(),
                    loadAdError.getMessage());
            Toast.makeText(
                    MenuInata.this, "onAdFailedToLoad() with error: " + error, Toast.LENGTH_SHORT)
                .show();
          }
        });
  }

    private void createTimer(final long milliseconds) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        final TextView textView = findViewById(R.id.timer);

        countDownTimer = new CountDownTimer(milliseconds, 50) {
            @Override
            public void onTick(long millisUnitFinished) {
                timerMilliseconds = millisUnitFinished;
                textView.setText("seconds remaining: " + ((millisUnitFinished / 1000) + 1));
            }

            @Override
            public void onFinish() {
                gameOver = true;
                textView.setText("done!");
                retryButton.setVisibility(View.VISIBLE);
            }
        };

        countDownTimer.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        resumeGame();
    }

    @Override
    public void onPause() {
        super.onPause();
        pauseGame();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        MenuItem moreMenu = menu.findItem(R.id.action_more);
        moreMenu.setVisible(googleMobileAdsConsentManager.isPrivacyOptionsRequired());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        View menuItemView = findViewById(item.getItemId());
        PopupMenu popup = new PopupMenu(this, menuItemView);
        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(
            popupMenuItem -> {
                if (popupMenuItem.getItemId() == R.id.privacy_settings) {
                    pauseGame();
                    googleMobileAdsConsentManager.showPrivacyOptionsForm(
                          this,
                          formError -> {
                              if (formError != null) {
                                  Toast.makeText(
                                      this,
                                      formError.getMessage(),
                                      Toast.LENGTH_SHORT).show();
                              }
                              resumeGame();
                          });
                    return true;
                }
                return false;
            });
        return super.onOptionsItemSelected(item);
    }

    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise restart the game.
        if (interstitialAd != null) {
            interstitialAd.show(this);
        } else {
            startGame();
            if (googleMobileAdsConsentManager.canRequestAds()) {
                loadAd();
            }
        }
    }

    private void startGame() {
        // Hide the button, and kick off the timer.
        retryButton.setVisibility(View.INVISIBLE);
        createTimer(GAME_LENGTH_MILLISECONDS);
        gamePaused = false;
        gameOver = false;
    }

    private void resumeGame() {
        if (gameOver || !gamePaused) {
          return;
        }
        // Create a new timer for the correct length.
        gamePaused = false;
        createTimer(timerMilliseconds);
    }

    private void pauseGame() {
        if (gameOver || gamePaused) {
          return;
        }
        countDownTimer.cancel();
        gamePaused = true;
    }

    private void initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
          return;
        }

        MobileAds.initialize(
              this,
              new OnInitializationCompleteListener() {
                  @Override
                  public void onInitializationComplete(InitializationStatus initializationStatus) {
                      // Load an ad.
                      loadAd();
                  }
              });
    }
}
