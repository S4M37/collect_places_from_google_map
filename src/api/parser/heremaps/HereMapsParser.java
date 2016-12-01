package api.parser.heremaps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.ResponseBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import api.parser.heremaps.models.BusinessHere;
import api.parser.interfaces.PublishDataInterface;
import api.parser.interfaces.RetrofitServices;

import com.google.gson.Gson;

public class HereMapsParser {

	private String appId;
	private String appCode;

	private RetrofitServices service;
	private String borders;
	private String[] categories;
	private int catCounter = 0;
	private int count = 0;
	private String region;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public RetrofitServices getService() {
		return service;
	}

	public void setService(RetrofitServices service) {
		this.service = service;
	}

	public String[] getCategories() {
		return categories;
	}

	public void setCategories(String[] categories) {
		this.categories = categories;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public PublishDataInterface getPublishData() {
		return publishData;
	}

	public void setPublishData(PublishDataInterface publishData) {
		this.publishData = publishData;
	}

	public String getBorders() {
		return borders;
	}

	public void setBorders(String borders) {
		this.borders = borders;
	}

	PublishDataInterface publishData;

	// Here methods
	public void getPlaces() {
		final Call<ResponseBody> call = service.getPlacesHere(appId, appCode,
				borders, categories[catCounter]);
		call.enqueue(new Callback<ResponseBody>() {

			private int count;

			@Override
			public void onFailure(Call<ResponseBody> arg0, Throwable arg1) {
			}

			@Override
			public void onResponse(Call<ResponseBody> arg0,
					Response<ResponseBody> arg1) {
				try {
					JSONObject jsonObject = new JSONObject(arg1.body().string());
					jsonObject = jsonObject.getJSONObject("results");
					JSONArray jsonArray = jsonObject.getJSONArray("items");
					Gson gson = new Gson();
					for (int i = 0; i < jsonArray.length(); i++) {
						BusinessHere businessHere = gson.fromJson(jsonArray
								.get(i).toString(), BusinessHere.class);
						publishData
								.publishFromHere(businessHere, region, count);
						count++;
					}
					String nextUrl = jsonObject.getString("next");
					if (nextUrl != null) {
						getNextPlaces(nextUrl);
					}
				} catch (IOException | NullPointerException | JSONException e) {
					e.printStackTrace();
					catCounter++;
					if (catCounter < categories.length) {
						getPlaces();
					}
				}
			}
		});
	}

	public void getNextPlaces(final String next) {
		final Call<ResponseBody> call = service.getNextPlacesHere(appId,
				appCode, borders, categories[catCounter]);

		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {

				URL url;
				try {
					System.err.println(next);

					url = new URL(next);
					HttpsURLConnection con = (HttpsURLConnection) url
							.openConnection();
					con.setRequestMethod("GET");
					con.setRequestProperty("Accept",
							"application/json; charset=utf-8");
					con.setRequestProperty("Content-Type",
							"application/json; application/x-www-form-urlencoded;charset=utf-8");

					BufferedReader br = new BufferedReader(
							new InputStreamReader((con.getInputStream())));
					String output = "", tmp;
					while ((tmp = br.readLine()) != null) {
						output += tmp + "\n";
					}
					JSONObject jsonObject = new JSONObject(output);
					System.err.println(jsonObject.toString());
					JSONArray jsonArray = jsonObject.getJSONArray("items");
					Gson gson = new Gson();
					for (int i = 0; i < jsonArray.length(); i++) {
						BusinessHere businessHere = gson.fromJson(jsonArray
								.get(i).toString(), BusinessHere.class);
						publishData
								.publishFromHere(businessHere, region, count);
						count++;
					}
					String nextUrl = jsonObject.getString("next");
					if (nextUrl != null) {
						getNextPlaces(nextUrl);
					}

				} catch (IOException | NullPointerException | JSONException e) {
					e.printStackTrace();
					catCounter++;
					if (catCounter < categories.length) {
						getPlaces();
					}
				}

				call.enqueue(new Callback<ResponseBody>() {

					@Override
					public void onFailure(Call<ResponseBody> arg0,
							Throwable arg1) {
						arg1.printStackTrace();
					}

					@Override
					public void onResponse(Call<ResponseBody> arg0,
							Response<ResponseBody> arg1) {
						try {
							JSONObject jsonObject = new JSONObject(arg1.body()
									.string()); // jsonObject =
							jsonObject.getJSONObject("results");
							JSONArray jsonArray = jsonObject
									.getJSONArray("items");
							Gson gson = new Gson();
							String nextUrl = jsonObject.getString("next");
							for (int i = 0; i < jsonArray.length(); i++) {
								BusinessHere businessHere = gson.fromJson(
										jsonArray.get(i).toString(),
										BusinessHere.class);
								publishData.publishFromHere(businessHere,
										region, count);
								count++;
							}
							if (nextUrl != null) {
								getNextPlaces(nextUrl);
							}
						} catch (IOException | NullPointerException
								| JSONException e) {
							e.printStackTrace();
						}
					}
				});

			}
		}, 3000);

	}

}
