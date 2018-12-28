package com.myapps.compilerfarsi;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.dd.morphingbutton.MorphingButton;
import com.dd.morphingbutton.impl.LinearProgressButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.krishna.fileloader.FileLoader;
import com.krishna.fileloader.listener.FileRequestListener;
import com.krishna.fileloader.pojo.FileResponse;
import com.krishna.fileloader.request.FileLoadRequest;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.kbiakov.codeview.CodeView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final int FILE_REQUEST_CODE = 100;
    @BindView(R.id.btnUpload)
    LinearProgressButton btnUpload;
    @BindView(R.id.mode1)
    RadioButton mode1;
    @BindView(R.id.mode2)
    RadioButton mode2;
    @BindView(R.id.groupMode)
    RadioGroup groupMode;
    @BindView(R.id.btnChooseFile)
    Button btnChooseFile;
    @BindView(R.id.previewCode)
    CodeView previewCode;
    private File file;
    private int mode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        morphToSquare(btnUpload, 0, "Upload");
        groupMode.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.mode1:
                    mode = 1;
                    break;
                case R.id.mode2:
                    mode = 2;
                    break;
            }
        });
    }

    private void morphToSquare(final MorphingButton btnMorph, int duration, String title) {
        MorphingButton.Params square = MorphingButton.Params.create()
                .duration(duration)
                .cornerRadius((int) getResources().getDimension(R.dimen.mb_corner_radius_2))
                .width((int) getResources().getDimension(R.dimen.mb_width_120))
                .height((int) getResources().getDimension(R.dimen.mb_height_56))
                .color(ContextCompat.getColor(this, android.R.color.holo_blue_bright))
                .colorPressed(ContextCompat.getColor(this, android.R.color.holo_blue_dark))
                .text(title);
        btnMorph.morph(square);
    }

    private void morphToSuccess(final MorphingButton btnMorph) {
        MorphingButton.Params circle = MorphingButton.Params.create()
                .duration(500)
                .cornerRadius((int) getResources().getDimension(R.dimen.mb_height_56))
                .width((int) getResources().getDimension(R.dimen.mb_height_56))
                .height((int) getResources().getDimension(R.dimen.mb_height_56))
                .color(ContextCompat.getColor(this, android.R.color.holo_green_light))
                .colorPressed(ContextCompat.getColor(this, android.R.color.holo_green_dark))
                .icon(R.drawable.ic_done_white_24dp);
        btnMorph.morph(circle);
    }

    private void morphToFailure(final MorphingButton btnMorph, int duration) {
        MorphingButton.Params circle = MorphingButton.Params.create()
                .duration(duration)
                .cornerRadius((int) getResources().getDimension(R.dimen.mb_height_56))
                .width((int) getResources().getDimension(R.dimen.mb_height_56))
                .height((int) getResources().getDimension(R.dimen.mb_height_56))
                .color(ContextCompat.getColor(this, android.R.color.holo_red_light))
                .colorPressed(ContextCompat.getColor(this, android.R.color.holo_red_dark))
                .icon(R.drawable.ic_refresh_white_24dp);
        btnMorph.morph(circle);
    }

    @OnClick(R.id.btnUpload)
    public void uploadFile() {
        if (file != null) {
            btnChooseFile.setEnabled(false);
            morphToSquare(btnUpload, 500, "Please Wait");
            sendFile();
        } else {
            Toast.makeText(this, "Please Choose File First", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            file = new File(filePath);
            previewCode();
        }
    }

    private void previewCode() {
        String ans = getText(file);
        previewCode.setCode(ans);
    }

    private void sendFile() {
        RequestBody reqFile = RequestBody.create(MediaType.parse("text/plain"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), reqFile);
        ApiRepository.getInstance().getApiService().postFile(mode, body).enqueue(new Callback<ImageRes>() {
            @Override
            public void onResponse(Call<ImageRes> call, Response<ImageRes> response) {
                if (response != null) {
                    morphToSuccess(btnUpload);
                    getFile();
                } else {
                    btnChooseFile.setEnabled(true);
                    morphToFailure(btnUpload, 500);
                    Log.e("TAAG", response.message());
                }
            }

            @Override
            public void onFailure(Call<ImageRes> call, Throwable t) {
                btnChooseFile.setEnabled(true);
                morphToFailure(btnUpload, 500);
                t.printStackTrace();
            }
        });
    }

    private void getFile() {
        FileLoader.with(this)
                .load("http://172.17.9.189:5000/dst.txt")
                .asFile(new FileRequestListener<File>() {
                    @Override
                    public void onLoad(FileLoadRequest request, FileResponse<File> response) {
                        File loadedFile = response.getBody();
                        String ans = getText(loadedFile);
                        Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                        intent.putExtra("TEXT", ans);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(FileLoadRequest request, Throwable t) {
                        morphToFailure(btnUpload, 500);
                    }
                });
    }

    private String getText(File file) {
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return text.toString();
    }

    @OnClick(R.id.btnChooseFile)
    public void onViewClicked() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        new MaterialFilePicker()
                                .withActivity(MainActivity.this)
                                .withRequestCode(FILE_REQUEST_CODE)
                                .withFilter(Pattern.compile(".*\\.txt$"))
                                .withHiddenFiles(true)
                                .start();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
    }

}