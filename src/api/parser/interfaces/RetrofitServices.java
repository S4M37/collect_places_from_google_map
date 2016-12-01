package api.parser.interfaces;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitServices {
	public static final String baseUrlGmaps = "https://maps.googleapis.com/maps/api/place/nearbysearch/";
	public static final String baseUrlHere = "https://places.cit.api.here.com/places/v1/discover/";

	// Gmaps
	@GET("json")
	Call<ResponseBody> getPlacesGMaps(@Query("location") String location,
			@Query("key") String key, @Query("radius") int radius);

	@GET("json")
	Call<ResponseBody> getNextPlacesGMaps(@Query("location") String location,
			@Query("key") String key, @Query("radius") int radius,
			@Query("pagetoken") String token);

	// Here Maps
	@GET("search")
	Call<ResponseBody> getPlacesHere(@Query("app_id") String appId,
			@Query("app_code") String appCode, @Query("in") String in,
			@Query("q") String q);

	@GET("search;context=Zmxvdy1pZD0xNWJlYTVkOC1lNDIzLTU0OGUtYmQ3Ny0yY2ZiNjYwOTBjYjhfMTQ2OTYxNDk4NjcxN183NTU4XzkxODcmb2Zmc2V0PTIwJnNpemU9MjA")
	Call<ResponseBody> getNextPlacesHere(@Query("app_id") String appId,
			@Query("app_code") String appCode, @Query("in") String in,
			@Query("q") String q);

}
