package com.cocosoft.cardwallet.client.android.cardStorage;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.cocosoft.cardwallet.client.android.Intents;
import com.cocosoft.cardwallet.client.android.R;
import com.cocosoft.cardwallet.core.BarcodeFormat;
import com.cocosoft.cardwallet.core.Result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CardList extends Activity {

    private EditText cardName;
    private Cursor CursorList;
    private ListView CardsListView;
    private String rowID;
    int count;
    private HashMap<Integer, Integer> getRowID = new HashMap<Integer, Integer>();
    private List<HashMap<String, String>> listCard = new ArrayList<HashMap<String, String>>();
    CardDB loyaltyCardDB = new CardDB(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_list);
        cardName = (EditText) findViewById(R.id.search_card);
        CardsListView = (ListView) findViewById(R.id.lstViewCards);

        loyaltyCardDB.open();
        CursorList = loyaltyCardDB.getAllCards();
        count = 0;
        if (CursorList.moveToFirst()) {
            do {
                HashMap<String, String> cardDet = new HashMap<String, String>();
                Integer rowID = CursorList.getInt(0);
                String cardCompName = CursorList.getString(1).toString();
                cardDet.put("name", cardCompName);
                listCard.add(cardDet);
                getRowID.put(count, rowID);
                count++;
            } while (CursorList.moveToNext());
        }
        String[] itemControl = {"name"};
        int[] itemLayout = {R.id.name};
        listCard = sortCard(listCard);
        SimpleAdapter adapter = new SimpleAdapter(this.getBaseContext(), listCard, R.layout.list_card_layout, itemControl, itemLayout);
        CardsListView.setAdapter(adapter);

        //To view the card details
        try {
            CardsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @SuppressWarnings("rawtypes")
                public void onItemClick(AdapterView parent, View v, int position, long id) {
                   loyaltyCardDB.open();
                   Cursor cursorItem = loyaltyCardDB.getCards(getRowID.get(position));

                    Result result = getResult(cursorItem);
                    Intent intent = new Intent(Intents.Encode.ACTION);
                    intent.putExtra(Intents.Encode.FORMAT,result.getBarcodeFormat().toString());
                    intent.putExtra(Intents.Encode.DATA,result.getText());
                    intent.putExtra(Intents.Encode.SHOW_CONTENTS,true);
                    startActivity(intent);
                    loyaltyCardDB.close();

                }
            });
        } catch (Exception e) {
            Log.e("Phonebook_TAG", "I got an error on clicking the card name", e);
        }

        //Will be called when we search for a card
        try {
            cardName.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {

                    SimpleAdapter adapter = searchViewAdapter(cardName.getText().toString(), CursorList);
                    CardsListView.setAdapter(null);
                    CardsListView.setAdapter(adapter);

                    return false;
                }
            });
        } catch (Exception e) {
            Log.e("Phonebook_TAG", "I got an error while searching", e);
        }
        loyaltyCardDB.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_card_list, menu);
        return true;
    }

    //to Add card
    public void addCard(View view) {
        finish();
        Intent addContent = new Intent(this, SelectComp.class);
        startActivity(addContent);

        /*loyaltyCardDB.open();
        loyaltyCardDB.insertCards("Tesco","25228359",BarcodeFormat.EAN_8.toString());
        */

    }

    public Result getResult(Cursor cursor){


        String text = cursor.getString(2);
        String format = cursor.getString(3);
        Result result = new Result(text, null, null, BarcodeFormat.valueOf(format), 0);

        return  result;
    }
    //Return updated search list view adapter after search
    public SimpleAdapter searchViewAdapter(String search, Cursor crList) {
        count = 0;
        listCard = new ArrayList<HashMap<String, String>>();
        if (crList.moveToFirst()) {
            do {
                HashMap<String, String> cardDet = new HashMap<String, String>();
                Integer rowID = crList.getInt(0);
                String compName = crList.getString(1).toString();

                if (compName.toLowerCase().contains(search.toLowerCase()) && search != "") {
                    cardDet.put("name", compName);
                    listCard.add(cardDet);
                    getRowID.put(count, rowID);

                    count++;
                }  else if (search == "") {
                    cardDet.put("name", compName);
                    listCard.add(cardDet);
                    getRowID.put(count, rowID);

                    count++;
                }

            } while (crList.moveToNext());
        }

        String[] itemControl = {"name"};
        int[] itemLayout = {R.id.name};
        listCard = sortCard(listCard);
        SimpleAdapter adapter = new SimpleAdapter(this.getBaseContext(), listCard, R.layout.list_card_layout, itemControl, itemLayout);
        return adapter;
    }

    //To sort the cards
    public List<HashMap<String, String>> sortCard(List<HashMap<String, String>> cards) {

        List<String> lst = new ArrayList<String>();
        List<HashMap<String, String>> sortCards = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < cards.size(); i++) {
            lst.add(cards.get(i).get("name") + "," + getRowID.get(i));
        }
        Collections.sort(lst);
        getRowID = new HashMap<Integer, Integer>();
        for (int i = 0; i < lst.size(); i++) {
            HashMap<String, String> hashCards = new HashMap<String, String>();
            String splitData[] = lst.get(i).split(",");
            hashCards.put("name", splitData[0]);
            sortCards.add(hashCards);
            getRowID.put(i, Integer.valueOf(splitData[splitData.length - 1]));
        }
        return sortCards;
    }

  }



