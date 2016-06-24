import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitServices {
	public static final String baseUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/";

	@GET("json")
	Call<ResponseBody> getPlaces(@Query("location") String location,
			@Query("key") String key, @Query("radius") int radius);

	@GET("json")
	Call<ResponseBody> getNextPlaces(@Query("location") String location,
			@Query("key") String key, @Query("radius") int radius,
			@Query("pagetoken") String token);

}
