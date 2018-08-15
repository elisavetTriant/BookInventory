package com.example.android.bookinventory;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.android.bookinventory.data.BookContract.BookEntry;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Adapter for the ListView
     */
    BookCursorAdapter mCursorAdapter;

    /**
     * Identifier for the book data loader
     */
    private static final int BOOK_LOADER = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                // Launch the {@link EditorActivity} to insert a new book
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the pet data
        ListView bookListView = findViewById(R.id.list_view);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        TextView emptyView = findViewById(R.id.empty_list_info);
        bookListView.setEmptyView(emptyView);

        // Attach cursor adapter to the ListView
        // There is no book data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new BookCursorAdapter(this, null);
        bookListView.setAdapter(mCursorAdapter);

        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link DetailActivity}
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                // Form the content URI that represents the specific book that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link BookEntry#CONTENT_URI}.
                // For example, the URI would be "content://com.example.android.bookinventory/books/2"
                // if the book with ID 2 was clicked on.
                Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentBookUri);
                // Launch the {@link DetailActivity} to display the data for the current book.
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(BOOK_LOADER, null, this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_insert_sample_data) {
            insertBook();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRODUCT_PRICE,
                BookEntry.COLUMN_PRODUCT_QUANTITY
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                BookEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Update {@link PetCursorAdapter} with this new cursor containing updated book data
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }

    /**
     * Helper method to insert hardcoded book data into the database.
     */
    private void insertBook() {
        try {
            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(BookEntry.COLUMN_PRODUCT_NAME, getString(R.string.dummy_product_name));
            values.put(BookEntry.COLUMN_PRODUCT_PRICE, getString(R.string.dummy_product_price));
            values.put(BookEntry.COLUMN_PRODUCT_QUANTITY, 3);
            values.put(BookEntry.COLUMN_PRODUCT_GENRE, BookEntry.GENRE_NON_FICTION);
            values.put(BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME, getString(R.string.dummy_supplier_name));
            values.put(BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE, getString(R.string.dummy_supplier_phone));


            // Insert the new row
            Uri resultUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

            if (resultUri != null) {
                Toast.makeText(this, getString(R.string.insert_ok_message), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.insert_error_message), Toast.LENGTH_SHORT).show();
            }

        } catch (IllegalArgumentException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

}