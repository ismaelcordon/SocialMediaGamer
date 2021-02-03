package com.icdominguez.socialmediagamer.retrofit;

import com.icdominguez.socialmediagamer.models.FCMBody;
import com.icdominguez.socialmediagamer.models.FCMResponse;

import retrofit2.Call;

public class NotificationProvider {
    private String url = "https://fcm.googleapis.com";

    public NotificationProvider() {

    }

    public Call<FCMResponse> sendNotification(FCMBody body) {
        return RetrofitClient.getClient(url).create(IFCMApi.class).send(body);
    }
}
