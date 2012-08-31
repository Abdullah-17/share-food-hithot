package com.hobom.mobile.ui;

import android.content.SearchRecentSuggestionsProvider;
import android.database.Cursor;
import android.net.Uri;

public class SearchSuggestionProvider extends
        SearchRecentSuggestionsProvider {

    final static String AUTHORITY = "com.hobom.mobile.ui.SearchSuggestionProvider";
    final static int    MODE      = DATABASE_MODE_QUERIES;

    public SearchSuggestionProvider() {
        super();
        setupSuggestions(AUTHORITY, MODE);
    }
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, 
            String sortOrder) {
	  
	  Cursor cursor=super.query(uri, projection, selection, selectionArgs, sortOrder);
	 
	  return cursor;
  }
}
