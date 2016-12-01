package api.services;

import api.parser.interfaces.RetrofitServices;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

public class RetrofitUtils {
	private static RetrofitServices retrofitServicesGMapas = null;

	public static RetrofitServices getRetrofitServicesGmapsInstance() {
		if (retrofitServicesGMapas == null) {
			HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
			interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
			OkHttpClient client = new OkHttpClient.Builder().addInterceptor(
					interceptor).build();
			Retrofit retrofit = new retrofit2.Retrofit.Builder()
					.baseUrl(RetrofitServices.baseUrlGmaps).client(client)
					.build();
			retrofitServicesGMapas = retrofit.create(RetrofitServices.class);
		}
		return retrofitServicesGMapas;
	}

	private static RetrofitServices retrofitServicesHereMaps = null;

	public static RetrofitServices getRetrofitServiceHereMapsInstance() {
		if (retrofitServicesHereMaps == null) {
			HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
			interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
			OkHttpClient client = new OkHttpClient.Builder().addInterceptor(
					interceptor).build();
			Retrofit retrofit = new retrofit2.Retrofit.Builder()
					.baseUrl(RetrofitServices.baseUrlHere).client(client)
					.build();
			retrofitServicesHereMaps = retrofit.create(RetrofitServices.class);
		}
		return retrofitServicesHereMaps;
	}
}
