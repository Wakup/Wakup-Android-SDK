package com.yellowpineapple.wakup.sdk.activities;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yellowpineapple.wakup.sdk.R;
import com.yellowpineapple.wakup.sdk.Wakup;
import com.yellowpineapple.wakup.sdk.communications.requests.offers.GetCategoriesRequest;
import com.yellowpineapple.wakup.sdk.controllers.CategoriesAdapter;
import com.yellowpineapple.wakup.sdk.controllers.CompaniesAdapter;
import com.yellowpineapple.wakup.sdk.controllers.OfferCategory;
import com.yellowpineapple.wakup.sdk.models.Category;
import com.yellowpineapple.wakup.sdk.models.CompanyDetail;
import com.yellowpineapple.wakup.sdk.models.Offer;
import com.yellowpineapple.wakup.sdk.utils.IntentBuilder;
import com.yellowpineapple.wakup.sdk.views.PullToRefreshLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class CategoriesActivity extends OfferListActivity {

    // Models
    private List<Category> categories = null;
    private List<CompanyDetail> defaultCompanies = null;
    private Category selectedCategory = null;
    private CompanyDetail selectedCompany = null;
    private boolean alreadyRegistered = false;

    // Controllers
    CompaniesAdapter companiesAdapter;
    CategoriesAdapter categoriesAdapter;

    // Views
    private View navigationView;
    private PullToRefreshLayout ptrLayout;
    private View emptyView;
    private RecyclerView recyclerView;
    private RecyclerView categoriesRV;
    private RecyclerView companiesRV;
    private FloatingActionButton btnMap;

    // Offer categories
    private OfferCategory MAIN_CATEGORY;
    private OfferCategory RELATED_CATEGORY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.wk_activity_categories);

        MAIN_CATEGORY = new OfferCategory(0, null);
        RELATED_CATEGORY = new OfferCategory(1, getString(R.string.wk_related_offers));

        injectViews();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(getPersistence().getOptions().showBackInRoot());
        }
    }

    protected void injectViews() {
        super.injectViews();
        emptyView = findViewById(R.id.emptyView);
        recyclerView = findViewById(R.id.recycler_view);
        navigationView = findViewById(R.id.navigationView);
        ptrLayout = findViewById(R.id.ptr_layout);
        categoriesRV = findViewById(R.id.categoriesRV);
        companiesRV = findViewById(R.id.companiesRV);
        btnMap = findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapButtonPressed();
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    // scrolling down
                    btnMap.hide();
                } else {
                    // scrolling up
                    btnMap.show();
                }
            }
        });
        View toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setClickable(true);
            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedCategory == null && selectedCompany == null) {
                        recyclerView.smoothScrollToPosition(0);
                    } else {
                        resetSelection();
                    }
                }
            });
        }

        afterViews();
    }

    void resetSelection() {
        recyclerView.scrollToPosition(0);
        selectedCategory = null;
        selectedCompany = null;
        categoriesRV.scrollToPosition(0);
        categoriesAdapter.setSelectedCategory(null);
        categoriesAdapter.notifyDataSetChanged();
        companiesRV.scrollToPosition(0);
        companiesAdapter.setSelectedCompany(null);
        companiesAdapter.setCompanies(defaultCompanies);
        companiesAdapter.notifyDataSetChanged();
        reloadOffers();
    }

    void afterViews() {
        final List<OfferCategory> offerCategories = Arrays.asList(MAIN_CATEGORY, RELATED_CATEGORY);
        loadCategories(new GetCategoriesRequest.Listener() {
            @Override
            public void onSuccess(List<Category> categories) {
                CategoriesActivity.this.categories = categories;
                updateDefaultCompanies(categories);
                setupCategoriesSelector();
                setupCompaniesSelector();
                setupOffersGrid(null, recyclerView, offerCategories, navigationView, emptyView);
            }

            @Override
            public void onError(Exception exception) {
                setLoading(false);
                displayErrorDialog(getString(R.string.wk_connection_error_message));
                setEmptyViewVisible(true);
            }
        });
        setupOffersGrid(null, recyclerView, offerCategories, navigationView, emptyView);
    }

    void setupCategoriesSelector() {
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        categoriesRV.setLayoutManager(layoutManager);
        categoriesRV.setItemAnimator(null);
        categoriesAdapter = new CategoriesAdapter(null, this);
        categoriesAdapter.setListener(new CategoriesAdapter.Listener() {
            @Override
            public void onSelectedCategoryChanged(Category category) {
                selectedCategory = category;
                selectedCompany = null;
                companiesAdapter.setSelectedCompany(null);
                if (category == null) {
                    companiesAdapter.setCompanies(defaultCompanies);
                } else {
                    companiesAdapter.setCompanies(category.getCompanies());
                }
                companiesRV.scrollToPosition(0);
                companiesAdapter.notifyDataSetChanged();
                if (selectedCategory != null) scrollToCenterPosition(categoriesRV, categories.indexOf(selectedCategory));
                reloadOffers();
            }
        });
        categoriesAdapter.setCategories(categories);
        categoriesRV.setAdapter(categoriesAdapter);
    }

    void setupCompaniesSelector() {
        final LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        companiesRV.setLayoutManager(layoutManager);
        companiesRV.setItemAnimator(null);

        companiesAdapter = new CompaniesAdapter(null, this);
        companiesAdapter.setListener(new CompaniesAdapter.Listener() {
            @Override
            public void onSelectedCompanyChanged(CompanyDetail company) {
                selectedCompany = company;
                if (selectedCompany != null) {
                    final int index = companiesAdapter.getCompanies().indexOf(selectedCompany);
                    scrollToCenterPosition(companiesRV, index);
                }
                reloadOffers();
            }
        });
        companiesAdapter.setCompanies(defaultCompanies);
        companiesRV.setAdapter(companiesAdapter);
    }

    final LinearSnapHelper snapHelper = new LinearSnapHelper();
    void scrollToCenterPosition(@NonNull final RecyclerView recyclerView, final int position) {
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        layoutManager.scrollToPosition(position);
        snapHelper.attachToRecyclerView(recyclerView);
        new Handler().post(
                new Runnable() {
                    @Override
                    public void run() {
                        View view = layoutManager.findViewByPosition(position);
                        if (view == null) {
                            snapHelper.attachToRecyclerView(null);
                            return;
                        }

                        int[] snapDistance = snapHelper.calculateDistanceToFinalSnap(layoutManager, view);
                        snapHelper.attachToRecyclerView(null);
                        if (snapDistance != null && snapDistance.length > 1) {
                            if (snapDistance[0] != 0 || snapDistance[1] != 0) {
                                recyclerView.smoothScrollBy(snapDistance[0], snapDistance[1]);
                            }
                        }
                    }
                });
    }

    // Creates a list of companies based on companies assigned to different offerCategories
    void updateDefaultCompanies(List<Category> categories) {
        List<Integer> includedCompanies = new ArrayList<>();
        defaultCompanies = new ArrayList<>();
        List<LinkedList<CompanyDetail>> companyMap = new ArrayList<>();
        for (Category category : categories) {
            companyMap.add(new LinkedList<>(category.getCompanies()));
        }
        while (companyMap.size() > 0) {
            for (int i=0; i < companyMap.size(); i++) {
                LinkedList<CompanyDetail> companyList = companyMap.get(i);
                CompanyDetail company = companyList.pollFirst();
                if (company == null) {
                    companyMap.remove(companyList);
                } else if (!includedCompanies.contains(company.getId())) {
                    includedCompanies.add(company.getId());
                    defaultCompanies.add(company);
                }
            }
        }
    }

    void loadCategories(@Nullable final GetCategoriesRequest.Listener listener) {
        getRequestClient().getCategories(listener);
    }

    @Override
    void onRequestOffers(final OfferCategory category, final int page, final Location location) {
        if (alreadyRegistered) {
            if (category.equals(MAIN_CATEGORY)) {
                offersRequest = getRequestClient().findCategoryOffers(currentLocation, selectedCategory,
                        selectedCompany, page, getOfferListRequestListener());

            } else {
                Category offerCategory = selectedCategory;
                if (selectedCompany != null && selectedCategory == null) {
                    // Find category related to selected company
                    for (Category mCategory : categories) {
                        if (mCategory.getCompanies().contains(selectedCompany)) {
                            offerCategory = mCategory;
                            break;
                        }
                    }
                }
                // If category is not present, there will be no related offers
                if (selectedCompany == null || offerCategory == null) {
                    getOfferListRequestListener().onSuccess(new ArrayList<Offer>());
                } else {
                    offersRequest = getRequestClient().findCategoryRelatedOffers(currentLocation, offerCategory,
                            selectedCompany, page, getOfferListRequestListener());
                }
            }
        } else {
            getWakup().register(new Wakup.RegisterListener() {
                @Override
                public void onSuccess() {
                    alreadyRegistered = true;
                    onRequestOffers(category, page, location);
                }

                @Override
                public void onError(Exception exception) {
                    getOfferListRequestListener().onError(exception);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (selectedCompany != null || selectedCategory != null) {
            resetSelection();
        } else {
            finish();
        }
    }

    void mapButtonPressed() {
        int MAX_MAP_OFFERS = 20;
        List<Offer> offersSet = offers.get(offerCategories.get(0));
        List<Offer> mapOffers = new ArrayList<>(offersSet.subList(0, Math.min(MAX_MAP_OFFERS,
                offersSet.size())));
        OfferMapActivity.intent(this).offers(mapOffers).location(currentLocation).start();
        slideInTransition();
    }

    @Override
    public PullToRefreshLayout getPullToRefreshLayout() {
        return ptrLayout;
    }

    void menuSearchSelected() {
        SearchActivity.intent(this).location(currentLocation).start();
        slideInTransition();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.wk_main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean handled = super.onOptionsItemSelected(item);
        if (handled) {
            return true;
        }
        int itemId_ = item.getItemId();
        if (itemId_ == R.id.menu_search) {
            menuSearchSelected();
            return true;
        }
        return false;
    }

    // Builder
    public static Builder intent(Context context) {
        return new Builder(context);
    }

    public static class Builder extends IntentBuilder<CategoriesActivity> {
        public Builder(Context context) {
            super(CategoriesActivity.class, context);
        }
    }
}