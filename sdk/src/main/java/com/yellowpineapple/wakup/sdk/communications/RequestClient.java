package com.yellowpineapple.wakup.sdk.communications;

import android.content.Context;
import android.location.Location;

import com.yellowpineapple.wakup.sdk.Wakup;
import com.yellowpineapple.wakup.sdk.communications.requests.BaseRequest;
import com.yellowpineapple.wakup.sdk.communications.requests.OfferListRequestListener;
import com.yellowpineapple.wakup.sdk.communications.requests.offers.CompanyOffersRequest;
import com.yellowpineapple.wakup.sdk.communications.requests.offers.FeaturedOffersRequest;
import com.yellowpineapple.wakup.sdk.communications.requests.offers.FindOffersRequest;
import com.yellowpineapple.wakup.sdk.communications.requests.offers.GetCouponImageRequest;
import com.yellowpineapple.wakup.sdk.communications.requests.offers.GetOffersByIdRequest;
import com.yellowpineapple.wakup.sdk.communications.requests.offers.GetRedemptionCodeRequest;
import com.yellowpineapple.wakup.sdk.communications.requests.offers.RelatedOffersRequest;
import com.yellowpineapple.wakup.sdk.communications.requests.register.RegisterRequest;
import com.yellowpineapple.wakup.sdk.communications.requests.search.SearchRequest;
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

    public enum Environment {
        PRODUCTION("https://app.wakup.net/", false);

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
	RequestLauncher requestLauncher;
    PersistenceHandler persistence;
    Environment environment;
    Context context;
	String apiKey = null;
	
	public static RequestClient getSharedInstance(Context context) {
		if (sharedInstance == null) {
			sharedInstance = new RequestClient(context, Environment.PRODUCTION);
		}
		return sharedInstance;
	}
	
	private RequestClient(Context context, Environment environment) {
		requestLauncher = new DefaultRequestLauncher(context);
        persistence = PersistenceHandler.getSharedInstance(context);
        this.context = context;
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
        return launch(new FindOffersRequest(location, radius, listener));
    }

    public Request relatedOffers(Offer offer, int page, int perPage, OfferListRequestListener listener) {
        return launch(new RelatedOffersRequest(offer, page, perPage, listener));
    }

    public Request findOffersById(List<String> offerIds, Location location, int page, OfferListRequestListener listener) {
        return launch(new GetOffersByIdRequest(offerIds, location, page, listener));
    }

    public Request getCompanyOffers(CompanyDetail company, Store store, int page, OfferListRequestListener listener) {
        return launch(new CompanyOffersRequest(company, store, page, BaseRequest.RESULTS_PER_PAGE, listener));
    }

    public Request getFeaturedOffers(Location location, OfferListRequestListener listener) {
        return launch(new FeaturedOffersRequest(location, listener));
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
