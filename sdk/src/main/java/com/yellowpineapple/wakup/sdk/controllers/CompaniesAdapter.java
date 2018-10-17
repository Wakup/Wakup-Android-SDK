package com.yellowpineapple.wakup.sdk.controllers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.yellowpineapple.wakup.sdk.models.Company;
import com.yellowpineapple.wakup.sdk.views.CompanyListView;

import java.util.List;

/***
 * ADAPTER
 */

public class CompaniesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Company> companies;
    private Company selectedCompany = null;
    private Context context;
    private Listener listener;
    private View headerView;

    public CompaniesAdapter(View headerView, final Context context) {
        super();
        this.headerView = headerView;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        CompanyListView view = new CompanyListView(context);
        view.setListener(new CompanyListView.Listener() {
            @Override
            public void onCompanySelected(Company company) {
                setSelectedCompany(company);
                if (listener != null) listener.onSelectedCompanyChanged(selectedCompany);
            }
        });
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        CompanyListView view = (CompanyListView)holder.itemView;
        Company company = getCompany(position);
        view.setCompany(company);
        view.setSelected(selectedCompany != null && company.getId() == selectedCompany.getId());
    }

    private Company getCompany(int position) {
        return companies.get(isHeaderPresent() ? position - 1 : position);
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (companies != null) {
            count = companies.size();
        }
        return count;
    }

    private boolean isHeaderPresent() {
        return headerView != null;
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View v) {
            super(v);
        }
    }

    public interface Listener {
        void onSelectedCompanyChanged(Company company);
    }

    public Listener getListener() {
        return listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setCompanies(List<Company> companies) {
        this.companies = companies;
    }

    public Context getContext() {
        return context;
    }

    public void setSelectedCompany(Company selectedCompany) {
        this.selectedCompany = selectedCompany;
        notifyDataSetChanged();
    }
}