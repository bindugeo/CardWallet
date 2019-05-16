package com.cocosoft.cardwallet.client.android.cardStorage;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AlphabetIndexer;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.cocosoft.cardwallet.client.android.R;

import java.util.ArrayList;

public class CardList_new extends Activity implements SearchView.OnQueryTextListener {


    protected static final String TAG = "MainActivity";
    private LinearLayout mIndexerLayout;
    private ListView mListView;
    private FrameLayout mTitleLayout;
    private TextView mTitleText;
    private RelativeLayout mSectionToastLayout;
    private TextView mSectionToastText;
    private ArrayList<Glossary> glossaries = new ArrayList<Glossary>();
    private String alphabet = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private AlphabetIndexer mIndexer;
    private ContactListAdapter mAdapter;
    private int lastSelectedPosition = -1;

    //    private EditText etInput;
    private SearchView mSearchView;

    CardDB loyaltyCardDB = new CardDB(this);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_listnew);

        initView();
    }

    @SuppressWarnings("deprecation")
    private void initView() {
//    	etInput = (EditText) findViewById(R.id.et_input);
        mIndexerLayout = (LinearLayout) findViewById(R.id.indexer_layout);
        mListView = (ListView) findViewById(R.id.contacts_list);
        mTitleLayout = (FrameLayout) findViewById(R.id.title_layout);
        mTitleText = (TextView) findViewById(R.id.title_text);
        mSectionToastLayout = (RelativeLayout) findViewById(R.id.section_toast_layout);
        mSectionToastText = (TextView) findViewById(R.id.section_toast_text);
        for (int i = 0; i < alphabet.length(); i++) {
            TextView letterTextView = new TextView(this);
            letterTextView.setText(alphabet.charAt(i) + "");
            letterTextView.setTextSize(10f);
            letterTextView.setGravity(Gravity.CENTER);
            ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(28, 0, 1.0f);
            letterTextView.setLayoutParams(params);
            letterTextView.setPadding(4, 0, 2, 0);
            mIndexerLayout.addView(letterTextView);
            mIndexerLayout.setBackgroundResource(R.drawable.letterslist_bg);
        }

        loyaltyCardDB.open();
        Cursor cursor = loyaltyCardDB.getAllCards();
        if (cursor.moveToFirst()) {
            do {
                String rowId = cursor.getString(0).toString();
                String name = cursor.getString(1);
                String sortKey = getSortKey(cursor.getString(1));
                Log.e("sortKey from cursor", "" + sortKey);
                Glossary glossary = new Glossary();
                glossary.setrowId(rowId);
                glossary.setName(name);
                glossary.setSortKey(sortKey);
                glossaries.add(glossary);
            } while (cursor.moveToNext());
        }

        mAdapter = new ContactListAdapter(this, glossaries);
       // startManagingCursor(cursor);
        mIndexer = new AlphabetIndexer(cursor, 1, alphabet);
        mAdapter.setIndexer(mIndexer);

        if (glossaries != null && glossaries.size() > 0) {
            mListView.setAdapter(mAdapter);
            mListView.setOnScrollListener(mOnScrollListener);
            mListView.setOnItemClickListener(mItemClickListener);
            mIndexerLayout.setOnTouchListener(mOnTouchListener);
        }
    }

        private String getSortKey(String sortKeyString) {
            String key = sortKeyString.substring(0, 1).toUpperCase();
            if (key.matches("[A-Z]")) {
                return key;
            }
            return "#";
        }

    private AbsListView.OnScrollListener mOnScrollListener = new AbsListView.OnScrollListener() {

        private int lastFirstVisibleItem = -1;

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if(scrollState == SCROLL_STATE_IDLE) {
                //mIndexerLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                //mIndexerLayout.setBackgroundResource(R.drawable.letterslist_bg);
            } else {
                //mIndexerLayout.setBackgroundResource(R.drawable.letterslist_bg);
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                             int totalItemCount) {
            // firstVisibleItem corresponding to the index of AlphabetIndexer(eg, B-->Alphabet index is 2)
            int sectionIndex = mIndexer.getSectionForPosition(firstVisibleItem);
            //next section Index corresponding to the positon in the listview
            int nextSectionPosition = mIndexer.getPositionForSection(sectionIndex + 1);
            Log.d(TAG, "onScroll()-->firstVisibleItem="+firstVisibleItem+", sectionIndex="
                    +sectionIndex+", nextSectionPosition="+nextSectionPosition);
            if(firstVisibleItem != lastFirstVisibleItem) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mTitleLayout.getLayoutParams();
                params.topMargin = 0;
                mTitleLayout.setLayoutParams(params);
                mTitleText.setText(String.valueOf(alphabet.charAt(sectionIndex)));
                ((TextView) mIndexerLayout.getChildAt(sectionIndex)).setBackgroundColor(getResources().getColor(R.color.letter_bg_color));
                lastFirstVisibleItem = firstVisibleItem;
            }

            // update AlphabetIndexer background
            if(sectionIndex != lastSelectedPosition) {
                if(lastSelectedPosition != -1) {
                    ((TextView) mIndexerLayout.getChildAt(lastSelectedPosition)).setBackgroundColor(getResources().getColor(android.R.color.transparent));
                }
                lastSelectedPosition = sectionIndex;
            }

            if(nextSectionPosition == firstVisibleItem + 1) {
                View childView = view.getChildAt(0);
                if(childView != null) {
                    int sortKeyHeight = mTitleLayout.getHeight();
                    int bottom = childView.getBottom();
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mTitleLayout.getLayoutParams();
                    /*if(bottom < sortKeyHeight) {
                        float pushedDistance = bottom - sortKeyHeight;
                        params.topMargin = (int) pushedDistance;
                        mTitleLayout.setLayoutParams(params);
                    } else {*/
                    if(params.topMargin != 0) {
                        params.topMargin = 0;
                        mTitleLayout.setLayoutParams(params);
                    }
//                    }
                }
            }

        }

    };

    private AbsListView.OnItemClickListener mItemClickListener = new AbsListView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Intent cardDetails = new Intent(CardList_new.this, CardDetails.class);
            Glossary glsitem = (Glossary) mAdapter.getItem(position);
            cardDetails.putExtra("posit",glsitem.getRowId());
            finish();
            startActivity(cardDetails);
        }
    };

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            float alphabetHeight = mIndexerLayout.getHeight();
            float y = event.getY();
            int sectionPosition = (int) ((y / alphabetHeight) / (1f / 27f));
            if (sectionPosition < 0) {
                sectionPosition = 0;
            } else if (sectionPosition > 26) {
                sectionPosition = 26;
            }
            if(lastSelectedPosition != sectionPosition) {
                if(-1 != lastSelectedPosition){
                    ((TextView) mIndexerLayout.getChildAt(lastSelectedPosition)).setBackgroundColor(getResources().getColor(android.R.color.transparent));
                }
                lastSelectedPosition = sectionPosition;
            }
            String sectionLetter = String.valueOf(alphabet.charAt(sectionPosition));
            int position = mIndexer.getPositionForSection(sectionPosition);
            TextView textView = (TextView) mIndexerLayout.getChildAt(sectionPosition);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mIndexerLayout.setBackgroundResource(R.drawable.letterslist_bg);
                    textView.setBackgroundColor(getResources().getColor(R.color.letter_bg_color));
                    mSectionToastLayout.setVisibility(View.VISIBLE);
                    mSectionToastText.setText(sectionLetter);
                    mListView.smoothScrollToPositionFromTop(position,0,1);
                    break;
                case MotionEvent.ACTION_MOVE:
                    mIndexerLayout.setBackgroundResource(R.drawable.letterslist_bg);
                    textView.setBackgroundColor(getResources().getColor(R.color.letter_bg_color));
                    mSectionToastLayout.setVisibility(View.VISIBLE);
                    mSectionToastText.setText(sectionLetter);
                    mListView.smoothScrollToPositionFromTop(position,0,1);
                    break;
                case MotionEvent.ACTION_UP:
                    //mIndexerLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    mSectionToastLayout.setVisibility(View.GONE);
                default:
                    mSectionToastLayout.setVisibility(View.GONE);
                    break;
            }
            return true;
        }

    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_card_list, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchItem.getActionView();
        setupSearchView(searchItem);
        return true;
    }

    /**
     * On selecting action bar icons
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.action_search:
                // search action
                // Associate searchable configuration with the SearchView
//            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//            SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
//                    .getActionView();
//            searchView.setSearchableInfo(searchManager
//                    .getSearchableInfo(getComponentName()));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupSearchView(MenuItem searchItem) {

//        if (isAlwaysExpanded()) {
//            mSearchView.setIconifiedByDefault(false);
//        } else {
//            searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM
//                    | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
//        }

//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        if (searchManager != null) {
//            List<SearchableInfo> searchables = searchManager.getSearchablesInGlobalSearch();
//
//            SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
//            for (SearchableInfo inf : searchables) {
//                if (inf.getSuggestAuthority() != null
//                        && inf.getSuggestAuthority().startsWith("applications")) {
//                    info = inf;
//                }
//            }
//            mSearchView.setSearchableInfo(info);
//        }

        mSearchView.setOnQueryTextListener(this);
    }

    public boolean onQueryTextChange(String newText) {
        // mStatusView.setText("Query = " + newText);
        Log.i("Action Search Query", newText);
        mAdapter.getFilter().filter(newText);
        Log.i("mAdapter", ""+newText);
        return false;
    }

    public boolean onQueryTextSubmit(String query) {
        //mStatusView.setText("Query = " + query + " : submitted");
        Log.i("Action Search Query", query);
        return false;
    }

    public boolean onClose() {
        // mStatusView.setText("Closed!");
        Log.i("Action Search Query", "^%^%^%^%^");
        return false;
    }

    protected boolean isAlwaysExpanded() {
        return false;
    }



}

  /*      private EditText cardName;
    private Cursor CursorList;
    private ListView CardsListView;
    private String rowID;
    int count;
    private HashMap<Integer, String> getRowID=new HashMap<Integer, String>();
    private List<HashMap<String, String>> listCard=new ArrayList<HashMap<String, String>>();
    CardDB PhonebookDB = new CardDB(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_list);
        cardName=(EditText)findViewById(R.id.search_card);
        CardsListView=(ListView)findViewById(R.id.lstViewCards);

        PhonebookDB.open();
        CursorList = PhonebookDB.getAllCards();
        count=0;
        if (CursorList.moveToFirst())
        {
            do
            {
                HashMap<String, String> cardDet=new HashMap<String, String>();
                String rowID=CursorList.getString(0).toString();
                String cardFirstName=CursorList.getString(1).toString();
                String cardLastName=CursorList.getString(2).toString();
                cardDet.put("name",cardFirstName+" "+cardLastName);
                listCard.add(cardDet);
                getRowID.put(count, rowID);
                count++;
            }while (CursorList.moveToNext());
        }
        String[] itemControl = {"name"};
        int[] itemLayout={R.id.name};
        listCard=sortCard(listCard);
        SimpleAdapter adapter = new SimpleAdapter(this.getBaseContext(),listCard,R.layout.list_card_layout,itemControl,itemLayout);
        CardsListView.setAdapter(adapter);

        //To view the card details
        try
        {
            CardsListView.setOnItemClickListener(new OnItemClickListener()
            {
                @SuppressWarnings("rawtypes")
                public void onItemClick(AdapterView parent, View v, int position, long id)
                {

                    Intent cardDetails = new Intent(CardList.this, CardDetails.class);
                    cardDetails.putExtra("posit",getRowID.get(position));
                    finish();
                    startActivity(cardDetails);

                }
            });
        }
        catch(Exception e)
        {
            Log.e("Phonebook_TAG","I got an error on clicking the card name",e);
        }

        //Will be called when we search for a card
        try
        {
            cardName.setOnKeyListener(new OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {

                    SimpleAdapter adapter=searchViewAdapter(cardName.getText().toString(),CursorList);
                    CardsListView.setAdapter(null);
                    CardsListView.setAdapter(adapter);

                    return false;
                }
            });
        }
        catch(Exception e)
        {
            Log.e("Phonebook_TAG","I got an error while searching",e);
        }
        PhonebookDB.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_card_list, menu);
        return true;
    }

    //to Add card
    public void addCard(View view)
    {
        finish();
        Intent addContent=new Intent(this,AddCard.class);
        startActivity(addContent);

    }

    //Return updated search list view adapter after search
    public SimpleAdapter searchViewAdapter(String search,Cursor crList)
    {
        count=0;
        listCard=new ArrayList<HashMap<String,String>>();
        if (crList.moveToFirst())
        {
            do
            {
                HashMap<String, String> cardDet=new HashMap<String, String>();
                String rowID=crList.getString(0).toString();
                String fullName=crList.getString(1).toString()+" "+crList.getString(2).toString();
                String emailAdd=crList.getString(3).toString();
                String phoneNumber=crList.getString(4).toString();

                if(fullName.toLowerCase().contains(search.toLowerCase()) && search!="")
                {
                    cardDet.put("name",fullName);
                    listCard.add(cardDet);
                    getRowID.put(count, rowID);

                    count++;
                }
                else if(phoneNumber.toLowerCase().contains(search.toLowerCase()) && search!="")
                {
                    cardDet.put("name",fullName);
                    listCard.add(cardDet);
                    getRowID.put(count, rowID);

                    count++;
                }
                else if(emailAdd.toLowerCase().contains(search.toLowerCase()) && search!="")
                {
                    cardDet.put("name",fullName);
                    listCard.add(cardDet);
                    getRowID.put(count, rowID);

                    count++;
                }
                else if(search=="")
                {
                    cardDet.put("name",fullName);
                    listCard.add(cardDet);
                    getRowID.put(count, rowID);

                    count++;
                }

            }while (crList.moveToNext());
        }

        String[] itemControl = {"name"};
        int[] itemLayout={R.id.name};
        listCard=sortCard(listCard);
        SimpleAdapter adapter = new SimpleAdapter(this.getBaseContext(),listCard,R.layout.list_card_layout,itemControl,itemLayout);
        return adapter;
    }

    //To sort the cards
    public List<HashMap<String, String>> sortCard(List<HashMap<String, String>> cards)
    {

        List<String> lst=new ArrayList<String>();
        List<HashMap<String, String>> sortCards=new ArrayList<HashMap<String,String>>();
        for(int i=0;i<cards.size();i++)
        {
            lst.add(cards.get(i).get("name")+","+getRowID.get(i));
        }
        Collections.sort(lst);
        getRowID=new HashMap<Integer, String>();
        for(int i=0;i<lst.size();i++)
        {
            HashMap<String, String> hashCards=new HashMap<String, String>();
            String splitData[]=lst.get(i).split(",");
            hashCards.put("name",splitData[0]);
            sortCards.add(hashCards);
            getRowID.put(i, splitData[splitData.length-1]);
        }
        return sortCards;
    }
    */


