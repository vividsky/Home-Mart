package com.example.android.pets;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.example.android.pets.data.PetContract;
import com.example.android.pets.data.PetContract.PetEntry;
import com.example.android.pets.data.PetDbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = CatalogActivity.class.getSimpleName();

    PetDbHelper mDbHelper;
    ListView petListView;
    PetCursorAdapter petCursorAdapter;
    private static final int PET_LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);


        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
            startActivity(intent);
        });
        petListView = findViewById(R.id.text_view_pet);
        mDbHelper = new PetDbHelper(this);
        View emptyView = findViewById(R.id.empty_view);
        petListView.setEmptyView(emptyView);

        petCursorAdapter = new PetCursorAdapter(this, null);
        petListView.setAdapter(petCursorAdapter);

        //To map the catalog activity to editors activity on click of item
        // setData() sends the Uri to the editors activity
        petListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
            intent.setData(Uri.withAppendedPath(PetEntry.CONTENT_URI, String.valueOf(id)));
            startActivity(intent);
        });

        getSupportLoaderManager().initLoader(PET_LOADER_ID, null, this);
    }
    @Override
    protected void onStart() {
        super.onStart();

//        displayDatabaseInfo();
    }
    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    @SuppressLint("SetTextI18n")
    private void displayDatabaseInfo() {

        String[] projections = {
                PetEntry._ID,
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_BREED,
                PetEntry.COLUMN_PET_GENDER,
                PetEntry.COLUMN_PET_WEIGHT
        };
//        String selection = PetEntry.COLUMN_PET_GENDER + "=?";
//        String[] selectionArgs = {String.valueOf(PetEntry.GENDER_MALE)};

        //Perform a query on the provider using contentResolver
        // {@link PetEntry#CONTENT_URI}
        Cursor cursor = getContentResolver().query(PetEntry.CONTENT_URI,
                projections,
                null ,
                null,
                null,
                null);


    }

    private void insertPet() {

        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_PET_NAME, "Todo");
        values.put(PetEntry.COLUMN_PET_BREED, "labra");
        values.put(PetEntry.COLUMN_PET_GENDER, PetEntry.GENDER_MALE);
        values.put(PetEntry.COLUMN_PET_WEIGHT, 6);

        getContentResolver().insert(PetEntry.CONTENT_URI, values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertPet();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:

                AlertDialog.Builder dialogue = new AlertDialog.Builder(this);
                dialogue.setMessage(R.string.delete_all_pets_confirmation);
                dialogue.setPositiveButton("Delete", (dialog, which) -> {
                        getContentResolver().delete(PetEntry.CONTENT_URI, null, null);
                    Toast.makeText(this, R.string.delete_all_pets, Toast.LENGTH_SHORT).show();
                        });

                dialogue.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

//                dialogue.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

                dialogue.create();
                dialogue.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                PetEntry._ID,
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_BREED,
                PetEntry.COLUMN_PET_GENDER,
                PetEntry.COLUMN_PET_WEIGHT
        };
        return new CursorLoader( this, PetEntry.CONTENT_URI,
                projection, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

        petCursorAdapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        petCursorAdapter.changeCursor(null);
    }
}