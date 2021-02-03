package com.icdominguez.socialmediagamer.retrofit;

import com.icdominguez.socialmediagamer.models.FCMBody;
import com.icdominguez.socialmediagamer.models.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {

    @Headers({
            "Content-Type: application/json",
            "Authorization:key=AAAAotxx29g:APA91bH0WfysR-StgfzyOIpTrHzBg2wO_FFiVtWwi-R5MQEcjH6U9kzObYU6ryTD3GIOHgu-YqvD1G-i976yZJ9crZQ1R0tCJvahU4AJ5iuBpBe6Ar-vJPWb2BeL0sE7VHwDl6oeribG"
    })
    @POST("fcm/send")
    Call<FCMResponse> send(@Body FCMBody body);
}
