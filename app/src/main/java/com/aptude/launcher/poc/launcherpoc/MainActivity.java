package com.aptude.launcher.poc.launcherpoc;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aptude.launcher.poc.launcherpoc.utils.ShellExecuter;

public class MainActivity extends AppCompatActivity {

    EditText input;
    TextView out;
    String command;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        input = (EditText)findViewById(R.id.txt);
//        btn = (Button)findViewById(R.id.btn);
        out = (TextView)findViewById(R.id.out);
    }

    public void launch(View v){
        Toast.makeText(this,"Launching...", Toast.LENGTH_SHORT).show();


//            ShellExecuter exe = new ShellExecuter();
//            command = input.getText().toString();
//
//            String outp = exe.Executer(command);
//            out.setText(outp);
//            Log.d("Output", outp);

        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);


    }

}
