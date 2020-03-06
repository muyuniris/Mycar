package com.example.muyun.mycar;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {
    private DBOpenHelper dBopenHelper;
    private String username;
    private String password;
    private String confirmpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar mToolbar =  (Toolbar)findViewById(R.id.control_toolbar);
        //设置Toolbar
        setSupportActionBar(mToolbar);
        //显示NavigationIcon,这个方法是ActionBar的方法.Toolbar没有这个方法.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //设置icon
        mToolbar.setNavigationIcon(R.drawable.iconreturn);
        //设置监听.必须在setSupportActionBar()之后调
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        dBopenHelper=new DBOpenHelper(RegisterActivity.this,"userdb.db",null,1);
    }
    public void submit(View v){
        EditText ETusername=findViewById(R.id.new_username);
        username=ETusername.getText().toString();
        EditText ETpassword=findViewById(R.id.new_password);
        password=ETpassword.getText().toString();
        EditText ETconfirmpassword=findViewById(R.id.confirm_password);
        confirmpassword=ETconfirmpassword.getText().toString();
        if(username.isEmpty()){
            Toast.makeText(RegisterActivity.this,"username不能为空",Toast.LENGTH_SHORT).show();
        }
        else if(password.isEmpty()){
            Toast.makeText(RegisterActivity.this,"password不能为空",Toast.LENGTH_SHORT).show();
        }
        else if(confirmpassword.isEmpty()){
            Toast.makeText(RegisterActivity.this,"confirmpassword不能为空",Toast.LENGTH_SHORT).show();
        } else if(!(password.equals(confirmpassword))){
            Toast.makeText(RegisterActivity.this,"password与confirmpassword不相同,请重新输入",Toast.LENGTH_SHORT).show();
        }
        else if(CheckIsDataAlreadyInDBorNot()){
            insert(dBopenHelper.getReadableDatabase(), username, password);
            Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
            finish();
        }

    }
    //向数据库插入数据
    public void insert(SQLiteDatabase db, String username, String password){
        ContentValues values=new ContentValues();
        values.put("username",username);
        values.put("password",password);
        db.insert("user",null,values);
    }
    //检验用户名是否已存在
    public boolean CheckIsDataAlreadyInDBorNot(){
        Cursor cursor = dBopenHelper.getReadableDatabase().query("user", null, "username=?", new String[]{username}, null, null, null);
        if (cursor.getCount() > 0) {
            Toast.makeText(RegisterActivity.this, "用户名已存在,请重新输入", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
