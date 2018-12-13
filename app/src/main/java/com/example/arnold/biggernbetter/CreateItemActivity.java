package com.example.arnold.biggernbetter;

import android.app.ActionBar;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;


public final class CreateItemActivity extends AppCompatActivity {

    public static ArrayList<String> itemArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_item);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button goBackButton = findViewById(R.id.goBackButton);
        Button createEntryButton = findViewById(R.id.createEntryButton);
        final EditText editText = findViewById(R.id.editText);


        // if CreateItemActivity is opened because editItemButton in ItemListActivity is selected,
        // fill editText with selected item
        if (getIntent().getStringExtra("edit this text") != null) {
            editText.setText(getIntent().getStringExtra("edit this text"));
            goBackButton.setText("Cancel Edit");
            createEntryButton.setText("Edit Item");

        }

        // make snackbar
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Want to add what's on your clipboard to the list?", 10000);
        View view = snackbar.getView();


        // if there is something on the clipboard, show snackbar
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard.getText() != null) {
            snackbar.show();
        }

        // set up snackbar's layout margins
        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
        params.gravity = Gravity.TOP;
        params.setMargins(0,82,0,0);
        params.height = ActionBar.LayoutParams.WRAP_CONTENT;
        params.width = ActionBar.LayoutParams.MATCH_PARENT;
        view.setLayoutParams(params);

        // set up action on snackbar, which would create entry in ItemListActivity based on user's clipboard
        class listener implements View.OnClickListener{
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                String clipboardText = clipboard.getText().toString();
                if (clipboardText.length() != 0 && clipboardText.length() < 1000) {
                    itemArrayList.add(clipboardText);
                    saveArrayList(itemArrayList, "itemArrayList");
                    Intent intent = new Intent(CreateItemActivity.this, ItemListActivity.class);
                    startActivity(intent);
                }
            }
        }
        snackbar.setAction("Yes", new listener());


        // retrieve data for itemArrayList
        itemArrayList = retrieveArrayList("itemArrayList");

        // set goBackButton to go to ItemListActivity
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateItemActivity.this, ItemListActivity.class);
                startActivity(intent);
            }
        });

        // set createEntryButton to get new entry from EditText and add to itemArrayList,
        // then go to ItemListActivity
        createEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item = editText.getText().toString();
                if (item.length() != 0 && item.length() < 1000 && getIntent().getStringExtra("edit this text") == null) {
                    itemArrayList.add(item);
                    saveArrayList(itemArrayList, "itemArrayList");
                    Intent intent = new Intent(CreateItemActivity.this, ItemListActivity.class);
                    startActivity(intent);
                } else if (item.length() != 0 && item.length() < 1000 && getIntent().getStringExtra("edit this text") != null) {
                    int index = getIntent().getIntExtra("index", 0);
                    editItem(index, item);
                    Intent intent = new Intent(CreateItemActivity.this, ItemListActivity.class);
                    startActivity(intent);
                } else if (item.length() == 0) {
                    toastMessage("You must put something in the textfield!");
                } else {
                    toastMessage("Text is too long!");
                }
            }
        });
    }

    // to make toastMessage
    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // save ArrayList as a Json string in SharedPref
    public void saveArrayList(ArrayList<String> list, String key) {
        SharedPreferences preferences = getSharedPreferences("itemlist", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();
    }


    // retrieve ArrayList from SharedPreference
    public ArrayList<String> retrieveArrayList(String key) {
        SharedPreferences preferences = getSharedPreferences("itemlist", Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = preferences.getString(key, null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            return gson.fromJson(json, type);
        } else {
            return new ArrayList<String>();
        }
    }

    // edit item in the list and save edited itemArrayList
    public void editItem(int index, String newString) {
        itemArrayList.get(index);
        itemArrayList.set(index, newString);
        saveArrayList(itemArrayList, "itemArrayList");
    }

    // getter for itemArrayList
    public static ArrayList<String> getItemArrayList() {
        return itemArrayList;
    }


}
