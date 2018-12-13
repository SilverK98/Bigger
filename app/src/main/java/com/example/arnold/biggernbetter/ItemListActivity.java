package com.example.arnold.biggernbetter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class ItemListActivity extends AppCompatActivity {

    public static ArrayList<String> itemArrayList = new ArrayList<>();
    public RecyclerView itemRecyclerView;
    public Toolbar toolbar;
    public static String selectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        toolbar = findViewById(R.id.toolbar);

        // set deleteItemButton and editItemButton to invisible
        final Button deleteItemButton = findViewById(R.id.deleteItemButton);
        deleteItemButton.setVisibility(View.GONE);
        final Button editItemButton = findViewById(R.id.editItemButton);
        editItemButton.setVisibility(View.GONE);

        // retrieve saved data from SharedPref
        itemArrayList = retrieveArrayList("itemArrayList");

        // set up AddItemButton to go to CreateItemActivity
        final Button addItemButton = findViewById(R.id.addItemButton);
        addItemButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ItemListActivity.this, CreateItemActivity.class);
                startActivity(intent);
            }
        });

        // set up cancelActionsButton to hide edit options and go back to original layout
        final Button cancelActionsButton = findViewById(R.id.cancelActionsButton);
        cancelActionsButton.setVisibility(View.GONE);
        cancelActionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItemButton.setVisibility(View.GONE);
                editItemButton.setVisibility(View.GONE);
                cancelActionsButton.setVisibility(View.GONE);
                addItemButton.setVisibility(View.VISIBLE);
            }
        });

        // set up RecyclerView by first setting lnearLayoutManager so that the order in the recyclerview is reverse
        // and then setting adapter using ItemRecyclerAdapter
        itemRecyclerView = findViewById(R.id.item_recycler_view);
        itemRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        itemRecyclerView.setLayoutManager(linearLayoutManager);
        final ItemRecyclerAdapter adapter = new ItemRecyclerAdapter(this, itemArrayList);
        itemRecyclerView.setAdapter(adapter);


        // ItemTouchListener detects when item in ItemRecyclerView is tapped, swtiching to ShowItemActivity
        itemRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, itemRecyclerView, new RecyclerItemClickListener.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(ItemListActivity.this, ShowItemActivity.class);
                        startActivity(intent);
                        selectedItem = itemArrayList.get(position);
                    }
                    @Override
                    public void onLongItemClick(View view, final int position) {
                        deleteItemButton.setVisibility(View.VISIBLE);
                        editItemButton.setVisibility(View.VISIBLE);
                        cancelActionsButton.setVisibility(View.VISIBLE);
                        addItemButton.setVisibility(View.GONE);
                        selectedItem = itemArrayList.get(position);

                        // set listener for deleteItemButton so that when deleteItemButton is pressed,
                        // the selected item in itemRecyclerView is deleted
                        deleteItemButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteItem(selectedItem);
                                adapter.notifyItemRemoved(position);

                                deleteItemButton.setVisibility(View.GONE);
                                editItemButton.setVisibility(View.GONE);
                                cancelActionsButton.setVisibility(View.GONE);
                                addItemButton.setVisibility(View.VISIBLE);
                            }
                        });

                        editItemButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(ItemListActivity.this, CreateItemActivity.class);
                                intent.putExtra("edit this text", selectedItem);
                                intent.putExtra("index", position);
                                startActivity(intent);
                            }
                        });
                    }
                }));

        // set up ClearListButton to clear the list
        Button clearListButton = findViewById(R.id.clearListButton);
        clearListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearList();
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            return gson.fromJson(json, type);
        } else {
            return new ArrayList<String>();
        }
    }

    // delete item from list and save edited itemArrayList
    public void deleteItem(String string) {
        itemArrayList.remove(string);
        saveArrayList(itemArrayList, "itemArrayList");
    }

    // edit item in the list and save edited itemArrayList
    public void editItem(int index, String newString) {
        itemArrayList.get(index);
        itemArrayList.set(index, newString);
        saveArrayList(itemArrayList, "itemArrayList");
    }

    // clears list
    public void clearList() {
        itemArrayList.clear();
        saveArrayList(itemArrayList, "itemArrayList");
    }

    public static String getSelectedItem() {
        return selectedItem;
    }
}
