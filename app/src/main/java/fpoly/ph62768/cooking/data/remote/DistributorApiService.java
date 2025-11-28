package fpoly.ph62768.cooking.data.remote;

import java.util.List;

import fpoly.ph62768.cooking.model.Distributor;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface DistributorApiService {

    @POST("distributors/add")
    Call<Distributor> createDistributor(@Body DistributorRequest request);

    @GET("distributors")
    Call<List<Distributor>> getDistributors();

    @GET("distributors/{id}")
    Call<Distributor> getDistributor(@Path("id") String id);

    @PUT("distributors/{id}")
    Call<Distributor> updateDistributor(
            @Path("id") String id,
            @Body DistributorRequest request
    );

    @DELETE("distributors/{id}")
    Call<Void> deleteDistributor(@Path("id") String id);
}

