package com.cocosoft.cardwallet.client.android.cardStorage;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import com.cocosoft.cardwallet.client.android.R;

public class AddCard extends Activity {

    private EditText compName;
    private EditText Details;
    private EditText Format;
    final CardDB PhonebookDB = new CardDB(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        compName=(EditText)findViewById(R.id.compName);
        Details=(EditText)findViewById(R.id.details);
        Format=(EditText)findViewById(R.id.format);
         }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_add_card, menu);
        return true;
    }

    //Adding card to DB
    public void addToDB(View view)
    {
        try
        {
            long intNewID = 0;

            String strCompName = compName.getText().toString();
            String strDetails = Details.getText().toString();
            String strFormat = Format.getText().toString();

            PhonebookDB.open();
            intNewID = PhonebookDB.insertCards(strCompName, strDetails,strFormat);
            PhonebookDB.close();
            finish();

            Intent cardList = new Intent(this,CardList.class);
            startActivity(cardList);
        }
        catch(Exception e)
        {
            Log.e("Phonebook_TAG","Got an error while adding the card",e);
        }
    }

}
