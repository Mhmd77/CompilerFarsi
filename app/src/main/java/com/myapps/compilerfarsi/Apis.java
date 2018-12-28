package com.myapps.compilerfarsi;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface Apis {
    @Multipart
    @POST("/{mode}")
    Call<ImageRes> postFile(@Path("mode") int mode, @Part MultipartBody.Part file);

}
