package com.example.android.bookinventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import android.widget.Toast;

import com.example.android.bookinventory.data.BookContract;
import com.example.android.bookinventory.data.BookContract.BookEntry;


public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_BOOK_LOADER = 0;

    /**
     * Boolean flag that keeps track of whether the book has been edited (true) or not (false)
     */
    private boolean bookHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the bookHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            bookHasChanged = true;
            return false;
        }
    };

    /**
     * Content URI for the existing book (null if it's a new book)
     */
    private Uri mCurrentBookUri;

    private EditText productNameEditText;

    private EditText productPriceEditText;

    private EditText productQuantityEditText;

    private EditText productSupplierNameEditText;

    private EditText productSupplierPhoneEditText;

    private Spinner genreSpinner;

    private int mGenre = BookEntry.GENRE_UNKNOWN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new book or editing an existing one.
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();
        // If the intent DOES NOT contain a book content URI, then we know that we are
        // creating a new book.
        if (mCurrentBookUri == null) {
            // This is a new book, so change the app bar to say "Add a Book"
            setTitle(getString(R.string.editor_activity_title_new_book));

        } else {
            // Otherwise this is an existing book, so change app bar to say "Edit Book"
            setTitle(getString(R.string.editor_activity_title_edit_book));
            // Initialize a loader to read the book data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        productNameEditText = findViewById(R.id.product_name);
        productPriceEditText = findViewById(R.id.product_price);
        productQuantityEditText = findViewById(R.id.product_quantity);
        productSupplierNameEditText = findViewById(R.id.product_supplier_name);
        productSupplierPhoneEditText = findViewById(R.id.product_supplier_phone);
        genreSpinner = findViewById(R.id.product_genre);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        productNameEditText.setOnTouchListener(mTouchListener);
        productPriceEditText.setOnTouchListener(mTouchListener);
        productQuantityEditText.setOnTouchListener(mTouchListener);
        productSupplierNameEditText.setOnTouchListener(mTouchListener);
        productSupplierPhoneEditText.setOnTouchListener(mTouchListener);
        genreSpinner.setOnTouchListener(mTouchListener);

        setupSpinner();
    }

    private void saveBook() {

        String productName = productNameEditText.getText().toString().trim();
        String productPrice = productPriceEditText.getText().toString().trim();
        String productQuantity = productQuantityEditText.getText().toString().trim();
        String productSupplierName = productSupplierNameEditText.getText().toString().trim();
        String productSupplierPhone = productSupplierPhoneEditText.getText().toString().trim();

        // Check if this is supposed to be a new pet (mCurrentBookUri == null)
        // and check if all the fields in the editor are blank. Return if they are
        if (mCurrentBookUri == null &&
                TextUtils.isEmpty(productName) && TextUtils.isEmpty(productPrice) &&
                TextUtils.isEmpty(productQuantity) && TextUtils.isEmpty(productSupplierName)
                && TextUtils.isEmpty(productSupplierPhone) && mGenre == BookEntry.GENRE_UNKNOWN) {
            // Since no fields were modified, we can return early without creating a new pet.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        //Check for NOT NULL values
        if (!TextUtils.isEmpty(productName) && !TextUtils.isEmpty(productPrice) &&
                !TextUtils.isEmpty(productQuantity) && !TextUtils.isEmpty(productSupplierName)
                && !TextUtils.isEmpty(productSupplierPhone)) {

            int quantity = Integer.parseInt(productQuantity);

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(BookEntry.COLUMN_PRODUCT_NAME, productName);
            values.put(BookEntry.COLUMN_PRODUCT_PRICE, productPrice);
            values.put(BookEntry.COLUMN_PRODUCT_QUANTITY, quantity);
            values.put(BookEntry.COLUMN_PRODUCT_GENRE, mGenre);
            values.put(BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME, productSupplierName);
            values.put(BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE, productSupplierPhone);

            // Determine if this is a new or existing book by checking if mCurrentBookUri is null or not
            if (mCurrentBookUri == null) {
                //This is an insert, tell the content resolver to perform an insert
                Uri resultUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

                if (resultUri == null) {
                    Toast.makeText(this, getString(R.string.insert_error_message), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.insert_ok_message), Toast.LENGTH_SHORT).show();
                    //now return successfully to the previous screen
                    finish();
                }
            } else {
                //This is an update, tell the content resolver to perform an update
                int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);

                // Show a toast message depending on whether or not the update was successful.
                if (rowsAffected == 0) {
                    // If no rows were affected, then there was an error with the update.
                    Toast.makeText(this, getString(R.string.update_error_message),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the update was successful and we can display a toast.
                    Toast.makeText(this, getString(R.string.update_ok_message),
                            Toast.LENGTH_SHORT).show();
                    //now return successfully to the previous screen
                    finish();
                }
            }

        } else if (TextUtils.isEmpty(productName)) {

            Toast.makeText(this, getString(R.string.book_requires_product_name), Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(productPrice)) {

            Toast.makeText(this, getString(R.string.book_requires_valid_price), Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(productQuantity)) {

            Toast.makeText(this, getString(R.string.book_requires_valid_quantity), Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(productSupplierName)) {

            Toast.makeText(this, getString(R.string.book_requires_supplier_name), Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(productSupplierPhone)) {

            Toast.makeText(this, getString(R.string.book_requires_supplier_telephone), Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_save_current_record) {
            saveBook();
            return true;
        } else if (id == android.R.id.home) {
            if (!bookHasChanged) {
                finish();
                return true;
            }

            // Otherwise if there are unsaved changes, setup a dialog to warn the user.
            // Create a click listener to handle the user confirming that
            // changes should be discarded.
            DialogInterface.OnClickListener discardButtonClickListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // User clicked "Discard" button, close the current activity.
                            finish();
                        }
                    };

            // Show a dialog that notifies the user they have unsaved changes
            showUnsavedChangesDialog(discardButtonClickListener);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genreSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_genre_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genreSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        genreSpinner.setAdapter(genreSpinnerAdapter);

        // Set the integer mGender to the constant values
        genreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.genre_non_fiction))) {
                        mGenre = BookContract.BookEntry.GENRE_NON_FICTION;
                    } else if (selection.equals(getString(R.string.genre_fiction))) {
                        mGenre = BookContract.BookEntry.GENRE_FICTION;
                    } else if (selection.equals(getString(R.string.genre_tragedy))) {
                        mGenre = BookContract.BookEntry.GENRE_TRAGEDY;
                    } else if (selection.equals(getString(R.string.genre_tragic_comedy))) {
                        mGenre = BookContract.BookEntry.GENRE_TRAGIC_COMEDY;
                    } else if (selection.equals(getString(R.string.genre_fantasy))) {
                        mGenre = BookContract.BookEntry.GENRE_FANTASY;
                    } else if (selection.equals(getString(R.string.genre_mythology))) {
                        mGenre = BookContract.BookEntry.GENRE_MYTHOLOGY;
                    } else if (selection.equals(getString(R.string.genre_adventure))) {
                        mGenre = BookContract.BookEntry.GENRE_ADVENTURE;
                    } else if (selection.equals(getString(R.string.genre_mystery))) {
                        mGenre = BookContract.BookEntry.GENRE_MYSTERY;
                    } else if (selection.equals(getString(R.string.genre_science_fiction))) {
                        mGenre = BookContract.BookEntry.GENRE_SCIENCE_FICTION;
                    } else if (selection.equals(getString(R.string.genre_drama))) {
                        mGenre = BookContract.BookEntry.GENRE_SCIENCE_FICTION;
                    } else if (selection.equals(getString(R.string.genre_romance))) {
                        mGenre = BookContract.BookEntry.GENRE_ROMANCE;
                    } else if (selection.equals(getString(R.string.genre_action_adventure))) {
                        mGenre = BookContract.BookEntry.GENRE_ACTION_ADVENTURE;
                    } else if (selection.equals(getString(R.string.genre_satire))) {
                        mGenre = BookContract.BookEntry.GENRE_SATIRE;
                    } else if (selection.equals(getString(R.string.genre_horror))) {
                        mGenre = BookContract.BookEntry.GENRE_HORROR;
                    } else {
                        mGenre = BookContract.BookEntry.GENRE_UNKNOWN; // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGenre = BookContract.BookEntry.GENRE_UNKNOWN; // Unknown
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        // Since the detail screen shows all book attributes, define a projection that contains
        // all columns from the book table
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRODUCT_PRICE,
                BookEntry.COLUMN_PRODUCT_QUANTITY,
                BookEntry.COLUMN_PRODUCT_GENRE,
                BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentBookUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {

            // Now extract properties from cursor

            // Step a) Figure out the index of each column
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_QUANTITY);
            int genreColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_GENRE);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE);

            // step b) Use that index to extract the String or Int value of the book
            // at the current row the cursor is on.
            String currentName = cursor.getString(nameColumnIndex);
            String currentPrice = cursor.getString(priceColumnIndex);
            int currentQuantity = cursor.getInt(quantityColumnIndex);
            int currentGenre = cursor.getInt(genreColumnIndex);
            String currentSupplierName = cursor.getString(supplierNameColumnIndex);
            String currentSupplierPhone = cursor.getString(supplierPhoneColumnIndex);

            //Now set the values from the cursor to the UI elements
            productNameEditText.setText(currentName);
            productPriceEditText.setText(currentPrice);
            productQuantityEditText.setText(Integer.toString(currentQuantity));
            productSupplierNameEditText.setText(currentSupplierName);
            productSupplierPhoneEditText.setText(currentSupplierPhone);

            // Genre is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (see string-array name="array_genre_options")
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (currentGenre) {
                case BookEntry.GENRE_NON_FICTION:
                    genreSpinner.setSelection(1);
                    break;
                case BookEntry.GENRE_FICTION:
                    genreSpinner.setSelection(2);
                    break;
                case BookEntry.GENRE_TRAGEDY:
                    genreSpinner.setSelection(3);
                    break;
                case BookEntry.GENRE_TRAGIC_COMEDY:
                    genreSpinner.setSelection(4);
                    break;
                case BookEntry.GENRE_FANTASY:
                    genreSpinner.setSelection(5);
                    break;
                case BookEntry.GENRE_MYTHOLOGY:
                    genreSpinner.setSelection(6);
                    break;
                case BookEntry.GENRE_ADVENTURE:
                    genreSpinner.setSelection(7);
                    break;
                case BookEntry.GENRE_MYSTERY:
                    genreSpinner.setSelection(8);
                    break;
                case BookEntry.GENRE_SCIENCE_FICTION:
                    genreSpinner.setSelection(9);
                    break;
                case BookEntry.GENRE_DRAMA:
                    genreSpinner.setSelection(10);
                    break;
                case BookEntry.GENRE_ROMANCE:
                    genreSpinner.setSelection(11);
                    break;
                case BookEntry.GENRE_ACTION_ADVENTURE:
                    genreSpinner.setSelection(12);
                    break;
                case BookEntry.GENRE_SATIRE:
                    genreSpinner.setSelection(13);
                    break;
                case BookEntry.GENRE_HORROR:
                    genreSpinner.setSelection(14);
                    break;
                default:
                    genreSpinner.setSelection(0);
                    break;
            }

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the edit text fields.
        productNameEditText.setText("");
        productPriceEditText.setText("");
        productQuantityEditText.setText("");
        productSupplierNameEditText.setText("");
        productSupplierPhoneEditText.setText("");
        genreSpinner.setSelection(0);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the book hasn't changed, continue with handling back button press
        if (!bookHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
