package com.example.muyun.mycar;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    private DBOpenHelper dBopenHelper;
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dBopenHelper=new DBOpenHelper(LoginActivity.this,"userdb.db",null,1);
    }
    public void registerClicked(View view){
        Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
    }
    public void loginClick(View v){
        EditText ETusername=findViewById(R.id.username);
        username=ETusername.getText().toString();
        EditText ETpassword=findViewById(R.id.password);
        password=ETpassword.getText().toString();
        if(username.isEmpty()){
            Toast.makeText(LoginActivity.this,"username不能为空",Toast.LENGTH_SHORT).show();
        }
        else if(password.isEmpty()){
            Toast.makeText(LoginActivity.this,"password不能为空",Toast.LENGTH_SHORT).show();
        }
        else {
            Cursor cursor = dBopenHelper.getReadableDatabase().query("user", null, "username=?", new String[]{username}, null, null, null);
            if (cursor.getCount() <= 0) {
                Toast.makeText(LoginActivity.this, "用户名不存在", Toast.LENGTH_SHORT).show();
            } else {
                cursor.moveToFirst();
                String keypassword = cursor.getString(2);
                if (password.equals(keypassword)) {
                    login();
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    public void login(){
        Intent intent=new Intent(LoginActivity.this,ControlActivity.class);
        startActivity(intent);
    }
}
