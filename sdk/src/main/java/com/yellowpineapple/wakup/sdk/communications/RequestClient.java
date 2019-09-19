package com.yellowpineapple.wakup.sdk.communications;

import android.content.Context;
import android.location.Location;

import com.yellowpineapple.wakup.sdk.Wakup;
import com.yellowpineapple.wakup.sdk.communications.requests.BaseRequest;
import com.yellowpineapple.wakup.sdk.communications.requests.OfferListRequestListener;
import com.yellowpineapple.wakup.sdk.communications.requests.OfferRequestListener;
import com.yellowpineapple.wakup.sdk.communications.requests.offers.CompanyOffersRequest;
import com.yellowpineapple.wakup.sdk.communications.requests.offers.FeaturedOffersRequest;
import com.yellowpineapple.wakup.sdk.communications.requests.offers.FindOffersRequest;
import com.yellowpineapple.wakup.sdk.communications.requests.offers.FindRelatedOffersRequest;
import com.yellowpineapple.wakup.sdk.communications.requests.offers.GetCategoriesRequest;
import com.yellowpineapple.wakup.sdk.communications.requests.offers.GetCouponImageRequest;
import com.yellowpineapple.wakup.sdk.communications.requests.offers.GetOffersByIdRequest;
import com.yellowpineapple.wakup.sdk.communications.requests.offers.GetRedemptionCodeRequest;
import com.yellowpineapple.wakup.sdk.communications.requests.offers.RelatedOffersRequest;
import com.yellowpineapple.wakup.sdk.communications.requests.register.RegisterRequest;
import com.yellowpineapple.wakup.sdk.communications.requests.search.SearchRequest;
import com.yellowpineapple.wakup.sdk.models.Category;
import com.yellowpineapple.wakup.sdk.models.Company;
import com.yellowpineapple.wakup.sdk.models.CompanyDetail;
import com.yellowpineapple.wakup.sdk.models.Offer;
import com.yellowpineapple.wakup.sdk.models.RegistrationInfo;
import com.yellowpineapple.wakup.sdk.models.RemoteImage;
import com.yellowpineapple.wakup.sdk.models.Store;
import com.yellowpineapple.wakup.sdk.utils.PersistenceHandler;
import com.yellowpineapple.wakup.sdk.utils.Strings;

import java.util.List;

public class RequestClient {

	private static RequestClient sharedInstance = null;

    private final static String API_TOKEN_HEADER = "API-Token";
    private final static String USER_TOKEN_HEADER = "User-Token";

    public static final Environment ENVIRONMENT = Environment.PRODUCTION;

    public enum Environment {
        PRODUCTION("https://app.wakup.net/", false),
        PRE("http://pre.wakup.net/", false);

        String url;
        boolean dummy;

        Environment(String url, boolean dummy) {
            this.url = url;
            this.dummy = dummy;
        }

        public String getUrl() {
            return url;
        }

        public boolean isDummy() {
            return dummy;
        }
    }
	
	/* Properties */
	private RequestLauncher requestLauncher;
    private PersistenceHandler persistence;
    private Environment environment;
    private String apiKey = null;
	
	public static RequestClient getSharedInstance(Context context) {
		if (sharedInstance == null) {
			sharedInstance = new RequestClient(context, ENVIRONMENT);
		}
		return sharedInstance;
	}
	
	private RequestClient(Context context, Environment environment) {
		requestLauncher = new DefaultRequestLauncher(context);
        persistence = PersistenceHandler.getSharedInstance(context);
        this.environment = environment;
        apiKey = Wakup.instance(context).getOptions().getApiKey();
	}

    /* Public methods */

    // Registration
    public Request register(RegistrationInfo info, RegisterRequest.Listener listener) {
        return launch(new RegisterRequest(info, listener));
    }

    // Offers

    public Request findOffers(Location location, int page, OfferListRequestListener listener) {
        return launch(new FindOffersRequest(location, null, null, page, listener));
    }

    public Request findOffers(Location location, List<String> tags, int page, OfferListRequestListener listener) {
        return launch(new FindOffersRequest(location, null, tags, page, listener));
    }

    public Request findOffers(Location location, Company company, List<String> tags, int page, OfferListRequestListener listener) {
        return launch(new FindOffersRequest(location, company, tags, page, listener));
    }

    public Request findLocatedOffers(Location location, Double radius, OfferListRequestListener listener) {
        return launch(FindOffersRequest.findLocatedOffers(location, radius, listener));
    }

    public Request findCategoryOffers(Location location, Category category, Company company, int page, OfferListRequestListener listener) {
        return launch(FindOffersRequest.findCategoryOffers(location, category, company, page, listener));
    }

    public Request findNearestOffer(Location location, final OfferRequestListener listener) {
        return launch(FindOffersRequest.findNearestOffer(location, new OfferListRequestListener() {
            @Override
            public void onSuccess(List<Offer> offers) {
                listener.onSuccess(offers.size() > 0 ? offers.get(0) : null);
            }

            @Override
            public void onError(Exception exception) {
                listener.onError(exception);
            }
        }));
    }

    public Request relatedOffers(Offer offer, int page, int perPage, OfferListRequestListener listener) {
        return launch(new RelatedOffersRequest(offer, page, perPage, listener));
    }

    public Request findCategoryRelatedOffers(Location location, Category category, Company company, int page, OfferListRequestListener listener) {
        return launch(new FindRelatedOffersRequest(location, category, company, page, BaseRequest.RESULTS_PER_PAGE, listener));
    }

    public Request findOffersById(List<String> offerIds, Location location, int page, OfferListRequestListener listener) {
        return launch(new GetOffersByIdRequest(offerIds, location, page, listener));
    }

    public Request getCompanyOffers(CompanyDetail company, Store store, int page, OfferListRequestListener listener) {
        return launch(new CompanyOffersRequest(company, store, page, BaseRequest.RESULTS_PER_PAGE, listener));
    }

    public Request getFeaturedOffers(Location location, int page, int perPage, OfferListRequestListener listener) {
        return launch(new FeaturedOffersRequest(location, page, perPage, listener));
    }

    public Request getRedemptionCode(Offer offer, GetRedemptionCodeRequest.Listener listener) {
        return launch(new GetRedemptionCodeRequest(offer, listener));
    }

    public RemoteImage getCouponImage(Offer offer, String format) {
        GetCouponImageRequest request = new GetCouponImageRequest(offer, format, environment, persistence.getDeviceToken());
        return request.getRemoteImage();
    }


    // Search

    public Request search(String query, SearchRequest.Listener listener) {
        return launch(new SearchRequest(query, listener));
    }

    // Categories

    public Request getCategories(GetCategoriesRequest.Listener listener) {
        return launch(new GetCategoriesRequest(listener));
    }

	/* Private methods */
	private Request launch(BaseRequest request) {
        request.addHeader(API_TOKEN_HEADER, apiKey);
        if (Strings.notEmpty(persistence.getDeviceToken())) {
            request.addHeader(USER_TOKEN_HEADER, persistence.getDeviceToken());
        }
        request.setRequestLauncher(requestLauncher);
        request.setEnvironment(environment);
        request.launch();
        return request;
	}
}
