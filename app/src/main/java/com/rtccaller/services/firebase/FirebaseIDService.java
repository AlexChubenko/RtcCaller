package com.rtccaller.services.firebase;

import androidx.annotation.NonNull;

import com.google.firebase.inappmessaging.FirebaseInAppMessagingClickListener;
import com.google.firebase.inappmessaging.model.Action;
import com.google.firebase.inappmessaging.model.InAppMessage;


public class FirebaseIDService implements FirebaseInAppMessagingClickListener {
    @Override
    public void messageClicked(@NonNull InAppMessage inAppMessage, @NonNull Action action) {

    }
//    private static final String TAG = "FirebaseIDService";
//    PreferenceHelper helper;
//
//    @Override
//    public void onTokenRefresh() {
//        // Get updated InstanceID token.
//        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//        Log.d(TAG, "Refreshed token: " + refreshedToken);
//        helper = PreferenceHelper.get(getApplicationContext());
//        helper.putAccessToken(refreshedToken);
//
//    }

}
