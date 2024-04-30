package com.app.ctatracker.adapters;

import android.widget.Filter;

import com.app.ctatracker.models.Route;

public interface RoutesViewAdapterFilter {
    Filter getFilter();

    Route getItem(int position);
}
