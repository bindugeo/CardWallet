package com.cocosoft.cardwallet.client.android.cardStorage;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import com.cocosoft.cardwallet.client.android.R;

public class EditCard extends Activity {

    private EditText edtFirstName;
    private EditText edtLastName;
    private EditText edtEmailAddress;
    private EditText edtPhoneNumber;
    private Long rowID;
    CardDB PhonebookDB = new CardDB(this);
    private Cursor CursorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_card);
        PhonebookDB.open();

        edtFirstName=(EditText)findViewById(R.id.edtCompName);
        edtLastName=(EditText)findViewById(R.id.edtDetails);
        edtPhoneNumber=(EditText)findViewById(R.id.edtPhone);
        String position=getIntent().getStringExtra("posit");
        rowID=Long.parseLong(position);
        CursorList=PhonebookDB.getCards(rowID);
        edtFirstName.setText(CursorList.getString(1));
        edtLastName.setText(CursorList.getString(2));
        edtEmailAddress.setText(CursorList.getString(3));
        edtPhoneNumber.setText(CursorList.getString(4));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_edit_card, menu);
        return true;
    }

    //Save the edited the content
    public void saveCard(View view)
    {
        String strFirstName = edtFirstName.getText().toString();
        String strLastName = edtLastName.getText().toString();
        String strEmailID = edtEmailAddress.getText().toString();
        String strMobile01 = edtPhoneNumber.getText().toString();
        PhonebookDB.updateCards(rowID, strFirstName, strLastName, strEmailID, strMobile01);
        PhonebookDB.close();
        finish();

        Intent cardDetails = new Intent(this,CardDetails.class);
        cardDetails.putExtra("posit",rowID.toString());
        startActivity(cardDetails);
    }

    @Override
    public void onBackPressed() {
        try
        {
            finish();
            PhonebookDB.close();
            Intent cardDetails = new Intent(this, CardDetails.class);
            cardDetails.putExtra("posit",rowID.toString());
            startActivity(cardDetails);
        }
        catch(Exception e)
        {
        }
    }
}


