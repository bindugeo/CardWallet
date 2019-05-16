package com.cocosoft.cardwallet.client.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by bindugeorge on 1/8/15.
 */
public class List extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);

    }

    public void addCard(View view){
        Intent intent = new Intent(this, CaptureActivity.class);
        intent.setAction("SCAN");
        startActivity(intent);

    }
}