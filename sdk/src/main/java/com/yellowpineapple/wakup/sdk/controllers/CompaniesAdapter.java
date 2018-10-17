package com.yellowpineapple.wakup.sdk.controllers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.yellowpineapple.wakup.sdk.models.CompanyDetail;
import com.yellowpineapple.wakup.sdk.views.CompanyListView;

import java.util.List;

/***
 * ADAPTER
 */

public class CompaniesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<CompanyDetail> companies;
    private CompanyDetail selectedCompany = null;
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
            public void onCompanySelected(CompanyDetail company) {
                setSelectedCompany(company);
                if (listener != null) listener.onSelectedCompanyChanged(selectedCompany);
            }
        });
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        CompanyListView view = (CompanyListView)holder.itemView;
        CompanyDetail company = getCompany(position);
        view.setCompany(company);
        view.setSelected(selectedCompany != null && company.getId() == selectedCompany.getId());
    }

    private CompanyDetail getCompany(int position) {
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
        void onSelectedCompanyChanged(CompanyDetail company);
    }

    public Listener getListener() {
        return listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setCompanies(List<CompanyDetail> companies) {
        this.companies = companies;
    }

    public Context getContext() {
        return context;
    }

    public void setSelectedCompany(CompanyDetail selectedCompany) {
        CompanyDetail prevSelectedCompany = this.selectedCompany;
        this.selectedCompany = selectedCompany;
        if (prevSelectedCompany != null) notifyItemChanged(companies.indexOf(prevSelectedCompany));
        if (selectedCompany != null) notifyItemChanged(companies.indexOf(selectedCompany));
    }
}