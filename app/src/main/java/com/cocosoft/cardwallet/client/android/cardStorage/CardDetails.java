//Class to see the card details

package com.cocosoft.cardwallet.client.android.cardStorage;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.cocosoft.cardwallet.client.android.R;

public class CardDetails extends Activity {

    private TextView compName;
   // private TextView lasName;
  //  private TextView emailAddress;
   // private TextView phoneNumber;
    private Long rowID;
    CardDB loyaltyCardDB = new CardDB(this);
    private Cursor CursorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_details);

        loyaltyCardDB.open();
        try
        {
            compName=(TextView)findViewById(R.id.txtFirstName);
      //      lastName=(TextView)findViewById(R.id.txtLastName);
        //    emailAddress=(TextView)findViewById(R.id.txtEmailID);
          //  phoneNumber=(TextView)findViewById(R.id.txtPhone);

            String position=getIntent().getStringExtra("posit");
            rowID=Long.parseLong(position);
            CursorList=loyaltyCardDB.getCards(rowID);
            compName.setText(CursorList.getString(1)+" ");

        }
        catch(Exception e)
        {
            Log.e("Phonebook_TAG","Got an error while displaying the card",e);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_card_details, menu);
        return true;
    }

    public void deleteCard(View view)
    {
        try
        {
            loyaltyCardDB.deleteCards(rowID);
            finish();
            loyaltyCardDB.close();

            Intent cardList = new Intent(this,CardList.class);
            startActivity(cardList);
        }
        catch(Exception e)
        {
            Log.e("Phonebook_TAG","Got an error while deleting the card",e);
        }
    }

    public void editCard(View view)
    {
        try
        {
            finish();
            loyaltyCardDB.close();

            Intent editCard = new Intent(this,EditCard.class);
            editCard.putExtra("posit",rowID.toString());
            startActivity(editCard);
        }
        catch(Exception e)
        {
            Log.e("Phonebook_TAG","Got an error while editing the card",e);
        }

    }

    @Override
    public void onBackPressed() {

        finish();

        loyaltyCardDB.close();
        startActivity(new Intent(this, CardList.class));
    }

}
