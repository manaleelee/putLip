package com.example.harry.sheldon;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Hermione on 2016/2/16.
 */
public class Login_UI extends Activity {
    private SharedPreferences settings=null;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_ui_layout);
        Button confirm_button = (Button) findViewById(R.id.confirm_button);
        Button resign_button = (Button) findViewById(R.id.resign_button);
        final EditText name_edittext= (EditText) findViewById(R.id.name_edittext);
        final EditText passwords_editext= (EditText) findViewById(R.id.passwords_editext);


        confirm_button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                String string=name_edittext.getText().toString();
                settings= getSharedPreferences("Myaccout",0);
                String getpasswords=settings.getString(string, "");
                String passwords=passwords_editext.getText().toString();
                Log.i("app.tager", "getbyname:" + getpasswords + ",  inputname:" + string + ", inputPasswords: " + passwords + ",   inputPass_int:");
                passwords_editext.setText("");
                if(getpasswords.equals(passwords)){
                    Intent intent = new Intent();
                    intent.setClass(Login_UI.this, MainActivity.class);
                    startActivity(intent);
                    Login_UI.this.finish();

                }
            }
        });
        resign_button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                settings= getSharedPreferences("Myaccout",0);
                settings.edit().putString(name_edittext.getText().toString(),
                        passwords_editext.getText().toString()).commit();
                Toast.makeText(getBaseContext(), "注册成功!", Toast.LENGTH_SHORT).show();

//                Log.i("app.tager","注册成功！"+",  测试get null:"+settings.getString("nihao",""));
                name_edittext.setText("");
                passwords_editext.setText("");

            }
        });
    }
}
