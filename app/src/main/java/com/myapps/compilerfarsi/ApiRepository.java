package com.myapps.compilerfarsi;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.krishna.fileloader.FileLoader;
import com.krishna.fileloader.listener.FileRequestListener;
import com.krishna.fileloader.pojo.FileResponse;
import com.krishna.fileloader.request.FileLoadRequest;

import java.io.File;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiRepository {
    private final String BASE_URL = "http://172.17.9.189:5000/";

    private static ApiRepository apiRepository;
    protected Apis apiService;

    private ApiRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(Apis.class);

    }

    public synchronized static ApiRepository getInstance() {
        if (apiRepository == null) {
            if (apiRepository == null) {
                apiRepository = new ApiRepository();
            }
        }
        return apiRepository;
    }

    public Apis getApiService() {
        return apiService;
    }

    private void SaveFile(Context context, String url) {
        FileLoader.with(context)
                .load("http://172.17.9.189:5000/dst.txt")
                .fromDirectory("MyResponse", FileLoader.DIR_INTERNAL)
                .asFile(new FileRequestListener<File>() {
                    @Override
                    public void onLoad(FileLoadRequest request, FileResponse<File> response) {
                        File loadedFile = response.getBody();

                    }

                    @Override
                    public void onError(FileLoadRequest request, Throwable t) {
                    }
                });
    }
}
