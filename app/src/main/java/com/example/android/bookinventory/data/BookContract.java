package com.example.android.bookinventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Eli on 28-Jul-18.
 */

public final class BookContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty private constructor.
    private BookContract() {
    }

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.bookinventory";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.bookinventory/books/ is a valid path for
     * looking at book data.
     */
    public static final String PATH_BOOKS = "books";


    public static abstract class BookEntry implements BaseColumns {

        /**
         * The content URI to access the book data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of books.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single book.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        //Name of the table
        public static final String TABLE_NAME = "books";

        //Names of the columns
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "name";
        public static final String COLUMN_PRODUCT_PRICE = "price";
        public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
        public static final String COLUMN_PRODUCT_GENRE = "genre";
        public static final String COLUMN_PRODUCT_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_PRODUCT_SUPPLIER_PHONE = "supplier_phone";

        //A list of predefined genres will show up in a spinner
        public static final int GENRE_UNKNOWN = 0;
        public static final int GENRE_TRAGEDY = 1;
        public static final int GENRE_TRAGIC_COMEDY = 2;
        public static final int GENRE_FANTASY = 3;
        public static final int GENRE_MYTHOLOGY = 4;
        public static final int GENRE_ADVENTURE = 5;
        public static final int GENRE_MYSTERY = 6;
        public static final int GENRE_SCIENCE_FICTION = 7;
        public static final int GENRE_DRAMA = 8;
        public static final int GENRE_ROMANCE = 9;
        public static final int GENRE_ACTION_ADVENTURE = 10;
        public static final int GENRE_SATIRE = 11;
        public static final int GENRE_HORROR = 12;
        public static final int GENRE_NON_FICTION = 13;
        public static final int GENRE_FICTION = 14;

        /*
        * Note:
        *
        * The classic major genres of literature are:
        * Tragedy
        * Tragic comedy
        * Fantasy
        * Mythology
        * Adventure
        * Mystery
        *
        * Other major book genres include
        * Science fiction
        * Drama
        * Romance
        * Action / Adventure
        * Satire
        * Horror
        *
        * Other: Fiction, Non Fiction
        *
        * Source: https://en.wikipedia.org/wiki/List_of_writing_genres
        *
        * */

        /**
         * Returns whether or not the given genre is valid.
         */
        public static boolean isValidGenre(int genre) {
            return (genre == GENRE_UNKNOWN || genre == GENRE_TRAGEDY || genre == GENRE_TRAGIC_COMEDY
                    || genre == GENRE_FANTASY || genre == GENRE_MYTHOLOGY || genre == GENRE_ADVENTURE
                    || genre == GENRE_MYSTERY || genre == GENRE_SCIENCE_FICTION || genre == GENRE_DRAMA
                    || genre == GENRE_ROMANCE || genre == GENRE_ACTION_ADVENTURE || genre == GENRE_SATIRE
                    || genre == GENRE_HORROR || genre == GENRE_NON_FICTION || genre == GENRE_FICTION);
        }

    }
}
