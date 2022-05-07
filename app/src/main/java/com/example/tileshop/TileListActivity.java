package com.example.tileshop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class TileListActivity extends AppCompatActivity {
    private final static String LOG_TAG = TileListActivity.class.getName();
    private FirebaseUser user;
    private FirebaseAuth auth;

    private RecyclerView recyclerView;
    private ArrayList<TileItems> itemList;
    private ShoppingTileItemAdapter adapter;
    private int gridNumber = 1;
    private int cartIcon = 0;
    private boolean view = true;
    private FrameLayout redCircle;
    private TextView contextTV;

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference item;
    private NotificationHelper notificationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tile_list);
        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            Log.d(LOG_TAG,"Authenticated user.");
        }else{
            Log.d(LOG_TAG,"Unauthenticated user.");
            finish();
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        itemList = new ArrayList<>();

        adapter = new ShoppingTileItemAdapter(this, itemList);
        recyclerView.setAdapter(adapter);

        firebaseFirestore = FirebaseFirestore.getInstance();
        item = firebaseFirestore.collection("Items");

        queryData();

        notificationHelper = new NotificationHelper(this);
    }

    private void queryData(){
        this.itemList.clear();

        item.orderBy("name", Query.Direction.ASCENDING).limit(17).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(QueryDocumentSnapshot document : queryDocumentSnapshots){
                TileItems tileItems = document.toObject(TileItems.class);
                tileItems.setId(document.getId());
                this.itemList.add(tileItems);
            }
            if(this.itemList.size() == 0){
                iniData();
                queryData();
            }
            adapter.notifyDataSetChanged();
        });
    }

    private void iniData() {
        String[] itemList = getResources().getStringArray(R.array.tile_item_names);
        String[] itemDescription = getResources().getStringArray(R.array.tile_item_description);
        String[] itemPrice = getResources().getStringArray(R.array.tile_item_price);
        TypedArray itemImage = getResources().obtainTypedArray(R.array.tile_item_images);
        TypedArray itemRate = getResources().obtainTypedArray(R.array.tile_item_rates);

        for (int i = 0; i < itemList.length; i++) {
            this.item.add(new TileItems(itemList[i], itemDescription[i], itemPrice[i], itemRate.getFloat(i, 0), itemImage.getResourceId(i, 0)));
        }

        itemImage.recycle();
    }

    public void delete(TileItems tileItem){
        DocumentReference ref = this.item.document(tileItem._getId());
        ref.delete().addOnSuccessListener(succes -> {
            Log.d(LOG_TAG, "Deleted done "+tileItem._getId());
        }).addOnFailureListener(fail -> {
            Toast.makeText(this, "Item "+tileItem._getId()+" cannot be deleted",Toast.LENGTH_LONG).show();
        });
        notificationHelper.cancelNoti();
        queryData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu,menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(LOG_TAG,s);
                adapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.log_out_button:
                Log.d(LOG_TAG,"Log out clicked");
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
            case R.id.cart:
                Log.d(LOG_TAG,"Cart button clicked");
                return true;
            case R.id.view_selector:
                Log.d(LOG_TAG,"Selected other view");
                if(view){
                    changeView(item, R.drawable.ic_view_grid, 1);
                }else{
                    changeView(item, R.drawable.ic_view_row, 2);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void changeView(MenuItem item, int drawable, int i) {
        view = !view;
        item.setIcon(drawable);
        GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        gridLayoutManager.setSpanCount(i);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem menuItem = menu.findItem(R.id.cart);
        FrameLayout frameLayout = (FrameLayout) menuItem.getActionView();
        redCircle = (FrameLayout) frameLayout.findViewById(R.id.view_alert_red_circle);
        contextTV = (TextView) frameLayout.findViewById(R.id.view_alert_count_textview);

        frameLayout.setOnClickListener(view -> onOptionsItemSelected(menuItem));
        return super.onPrepareOptionsMenu(menu);
    }

    public void updateCartIcon(TileItems tileItem){
        Log.d(LOG_TAG, "Cart update function");
        cartIcon = (cartIcon + 1);
        if(0 < cartIcon){
            contextTV.setText(String.valueOf(cartIcon));
            Log.d(LOG_TAG, String.valueOf(cartIcon));
        }else{
            contextTV.setText("");
        }
        redCircle.setVisibility((cartIcon > 0) ? VISIBLE : GONE);

        this.item.document(tileItem._getId()).update("count", tileItem.getCount()+1).addOnFailureListener(fail -> {
            Toast.makeText(this, "Item "+tileItem._getId()+" cannot be changed",Toast.LENGTH_LONG).show();
        });
        notificationHelper.send(tileItem.getName()+" hozzáadva a kosárhoz.");
        queryData();
    }
}