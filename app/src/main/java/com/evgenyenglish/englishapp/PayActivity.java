package com.evgenyenglish.englishapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class PayActivity extends AppCompatActivity {
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        
        Button button = (Button) findViewById(R.id.button_pay);
        Button button_back = (Button) findViewById(R.id.go_back_pay);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.evgenyenglishpro.englishapp")); // Open precisely @link SmartBooks
                boolean tryAgain = false; // Flag to denote that normal attempt to launch GooglePlay update failed

                try
                {
                    startActivity(intent);
                }
                catch(Exception e)
                {
                    tryAgain = true;
                }

                if (!tryAgain) return;

                // Try to launch GooglePlay with SB in browser !
                try
                {
                    intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.sbm.bc.smartbooksmobile"));
                    startActivity(intent);
                }
                catch (Exception e)
                {

                }
                Toast.makeText(PayActivity.this, "У вас нет Google Play", Toast.LENGTH_SHORT).show();

                // No need to exit the app, as it already exits
                //finishAffinity();  // this requires  API level > 16
                //finish();
                //System.exit(0);
            }
        });

        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PayActivity.this, LessonsActivity.class);
                startActivity(intent);
            }
        });
    }
}
