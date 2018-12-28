package com.myapps.compilerfarsi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.kbiakov.codeview.CodeView;

public class ResultActivity extends AppCompatActivity {

    @BindView(R.id.code_view)
    CodeView codeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ButterKnife.bind(this);
        String text = getIntent().getStringExtra("TEXT");
        codeView.setCode(text);
    }
}
