package com.lakshitasuman.musicstation.ringtone.utils;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.lakshitasuman.musicstation.R;
import java.util.ArrayList;
import java.util.List;
public class AdsUtils {

    public static boolean isShowAds = true;
    static InterstitialAd mInterstitialAd;
    private RewardedAd rewardedAd;

    public static void initAd(Context context) {
        if (isShowAds) {
            List<String> testDevices = new ArrayList<>();
            testDevices.add("B3EEABB8EE11C2BE770B684D95219ECB");
            MobileAds.setRequestConfiguration(new RequestConfiguration.Builder().setTestDeviceIds(testDevices).build());
            MobileAds.initialize(context, (OnInitializationCompleteListener) new OnInitializationCompleteListener() {
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });
        }
    }


    public static void loadRewardedAd(Context context, Intent intent1) {

        if (isShowAds) {


            AdRequest adRequest = new AdRequest.Builder().build();
            final RewardedAd[] rewardedAd1 = {null};
            RewardedAd.load(
                    context,
                    "ca-app-pub-1669029486020807/6614345004",
                    adRequest,
                    new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error.
                            Log.d(TAG, loadAdError.getMessage());

                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            rewardedAd1[0] = rewardedAd;

                            if (rewardedAd1[0] != null) {
                                rewardedAd1[0].setFullScreenContentCallback(
                                        new FullScreenContentCallback() {
                                            @Override
                                            public void onAdShowedFullScreenContent() {
                                                // Called when ad is shown.
                                                Log.d(TAG, "onAdShowedFullScreenContent");

                                            }

                                            @Override
                                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                                // Called when ad fails to show.
                                                Log.d(TAG, "onAdFailedToShowFullScreenContent");
                                                // Don't forget to set the ad reference to null so you
                                                // don't show the ad a second time.


                                            }

                                            @Override
                                            public void onAdDismissedFullScreenContent() {
                                                // Called when ad is dismissed.
                                                // Don't forget to set the ad reference to null so you
                                                // don't show the ad a second time.
                                                Log.d(TAG, "onAdDismissedFullScreenContent");
                                                AdsUtils.startActivity(context, intent1);
                                                // Preload the next rewarded ad.
                                            }
                                        });
                                rewardedAd1[0].show(
                                        (Activity) context,
                                        new OnUserEarnedRewardListener() {
                                            @Override
                                            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                                // Handle the reward.
                                                Log.d("TAG", "The user earned the reward.");
                                                int rewardAmount = rewardItem.getAmount();
                                                String rewardType = rewardItem.getType();
                                            }
                                        });

                            }
                        }
                    });
        }
        else{
            AdsUtils.startActivity(context, intent1);
        }


    }

    public static void loadBannerAd(Context context, LinearLayout adContainer) {
        if (isShowAds) {
            AdView adView = new AdView(context);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.setAdSize(AdSize.BANNER);
            adView.setAdUnitId(context.getString(R.string.admob_banner_id));
            adView.loadAd(adRequest);
            adContainer.addView(adView);
        }
    }
    public static void loadLargeBannerAd(Context context, LinearLayout adContainer) {
        if (isShowAds) {
            AdView adView = new AdView(context);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.setAdSize(AdSize.BANNER);
            adView.setAdUnitId(context.getString(R.string.admob_banner_id));
            adView.loadAd(adRequest);
            adContainer.addView(adView);
        }
    }

    public static void loadInterAd(Context context) {
        if (isShowAds) {

            InterstitialAd.load(context, context.getString(R.string.admob_interstitial), new AdRequest.Builder().build(), new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(InterstitialAd interstitialAd) {
                    mInterstitialAd = interstitialAd;
                }

                @Override
                public void onAdFailedToLoad(LoadAdError loadAdError) {
                    mInterstitialAd = null;
                }
            });

        }
    }

    public static void showInterAd(final Context context, final Intent intent) {
        if (isShowAds) {
            if (mInterstitialAd != null) {
                mInterstitialAd.show((Activity) context);
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    public void onAdDismissedFullScreenContent() {
                        AdsUtils.startActivity(context, intent);
                        Log.d("TAG", "The ad was dismissed.");
                    }

                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        Log.d("TAG", "The ad failed to show.");
                    }

                    public void onAdShowedFullScreenContent() {
                        AdsUtils.mInterstitialAd = null;
                        Log.d("TAG", "The ad was shown.");
                    }
                });
            } else {
                startActivity(context, intent);
            }
        } else {
            startActivity(context, intent);
        }

        return;
    }

    static void startActivity(Context context, Intent intent) {
        if (intent != null) {
            context.startActivity(intent);
        }
    }


}
