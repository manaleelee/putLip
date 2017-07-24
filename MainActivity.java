package com.example.harry.sheldon;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;

public class MainActivity extends Activity implements DialogInterface.OnClickListener{

    private shotPlane view=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        view=new shotPlane(this);
        setContentView(view);

//        while (true){
//            if(view.ispause()){
//                AlertDialog.Builder dialog=new AlertDialog.Builder(this).setTitle("Wanna try again?");
//                dialog.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {      }
//                });
//                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        android.os.Process.killProcess(android.os.Process.myPid());
//                    }
//                });
//                dialog.show();
//            }
//
//        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i("app.tager","按下键值----"+keyCode);
        if(keyCode==KeyEvent.KEYCODE_BACK){//键值为4；
            view.stop();
            AlertDialog.Builder alert= new AlertDialog.Builder(this);
            alert.setTitle("You wanna leave?");

            alert.setPositiveButton("Sorry, Yes.", this);
            alert.setNegativeButton("Let's continue!", this);
            alert.create().show();
            return false;
        }

        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onClick(DialogInterface dialog, int which) {
        Log.i("app.tager",which+"is mine.");
        if(which==-2){
            view.start();
        }else{
            android.os.Process.killProcess(android.os.Process.myPid());
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}

