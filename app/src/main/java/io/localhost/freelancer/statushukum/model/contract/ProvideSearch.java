package io.localhost.freelancer.statushukum.model.contract;

public interface ProvideSearch
{
    boolean onQueryTextSubmit(String query);
    boolean onQueryTextChange(String newText);
    void onSearchFocusChanged(boolean isOnFocus);
}