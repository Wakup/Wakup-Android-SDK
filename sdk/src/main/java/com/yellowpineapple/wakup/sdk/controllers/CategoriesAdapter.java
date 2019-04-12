package com.yellowpineapple.wakup.sdk.controllers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yellowpineapple.wakup.sdk.models.Category;
import com.yellowpineapple.wakup.sdk.views.CategoryListView;

import java.util.List;

/***
 * ADAPTER
 */

public class CategoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Category> categories;
    private Category selectedCategory = null;
    private Context context;
    private Listener listener;
    private View headerView;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    public CategoriesAdapter(View headerView, final Context context) {
        super();
        this.headerView = headerView;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case TYPE_HEADER:
                return new HeaderViewHolder(headerView);
            default:
                CategoryListView view = new CategoryListView(context);
                view.setListener(new CategoryListView.Listener() {
                    @Override
                    public void onCategorySelected(Category category) {
                        selectedCategory = category;
                        notifyDataSetChanged();
                        if (listener != null) listener.onSelectedCategoryChanged(selectedCategory);
                    }
                });
                return new HeaderViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return isHeaderPresent() && position == 0 ? TYPE_HEADER : TYPE_ITEM;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(getItemViewType(position) != TYPE_HEADER) {
            CategoryListView view = (CategoryListView)holder.itemView;
            Category category = getCategory(position);
            view.setCategory(category);
            view.setSelected(selectedCategory != null && category.getId() == selectedCategory.getId());
        }

    }

    private Category getCategory(int position) {
        return categories.get(isHeaderPresent() ? position - 1 : position);
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (categories != null) {
            count = categories.size();
        }
        if (isHeaderPresent()) {
            count++;
        }

        return count;
    }

    private boolean isHeaderPresent() {
        return headerView != null;
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {

        HeaderViewHolder(View v) {
            super(v);
        }
    }

    public interface Listener {
        void onSelectedCategoryChanged(Category category);
    }

    public Listener getListener() {
        return listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public Context getContext() {
        return context;
    }

    public void setSelectedCategory(Category selectedCategory) {
        this.selectedCategory = selectedCategory;
    }
}