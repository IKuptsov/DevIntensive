package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.softdesign.devintensive.R;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private Button mButtonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mButtonLogin=(Button)findViewById(R.id.login_enter_button);
        mButtonLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_enter_button:
                Intent loginIntent= new Intent(this,MainActivity.class);
                startActivity(loginIntent);
                break;
        }
    }
}

