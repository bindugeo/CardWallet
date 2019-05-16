package com.cocosoft.cardwallet.client.android.cardStorage;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import com.cocosoft.cardwallet.client.android.Intents;
import com.cocosoft.cardwallet.client.android.R;

public class SelectComp extends Activity {

    private EditText compName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_comp);
        compName=(EditText)findViewById(R.id.compName);
         }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_add_card, menu);
        return true;
    }

    //Adding card to DB
    public void proceed_to_scan(View view)
    {

            Intent proceed = new Intent(this,com.cocosoft.cardwallet.client.android.CaptureActivity.class);
            // proceed.setAction(Intents.Scan.ACTION);
            proceed.putExtra(Intents.Scan.COMPANY,compName.getText().toString());
            startActivity(proceed);


    }

}
