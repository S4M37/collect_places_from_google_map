package api.parser.gmaps;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import api.parser.gmaps.models.BusinessGMaps;
import api.parser.interfaces.PublishDataInterface;
import api.parser.interfaces.RetrofitServices;

import com.google.gson.Gson;

public class GmapsParser {

	// Gmaps params
	int r = 0;
	HSSFSheet sheet;
	String filename;
	RetrofitServices service;

	int count = 0;
	// google place api web service KEY for server
	String key = "AIzaSyCHJWZFP6Xx_MrwOeJlWXXMJ98JGzxxhIM";
	// radius with meters
	final int radius = 4000;
	// list of Lat-Lon "lat,long" Ex: "36.923623, 10.277558"
	String[] latlng = { "34.529361,10.496020" };
	// ListOfRegion
	String[] regions = { "Mahares" };

	PublishDataInterface publishData;

	public String getKey() {
		return key;
	}

	public GmapsParser setKey(String key) {
		this.key = key;
		return this;
	}

	public String[] getLatlng() {
		return latlng;
	}

	public GmapsParser setLatlng(String[] latlng) {
		this.latlng = latlng;
		return this;
	}

	public PublishDataInterface getPublishData() {
		return publishData;
	}

	public GmapsParser setPublishData(PublishDataInterface publishData) {
		this.publishData = publishData;
		return this;
	}

	public int getRadius() {
		return radius;
	}

	public RetrofitServices getService() {
		return service;
	}

	public GmapsParser setService(RetrofitServices service) {
		this.service = service;
		return this;
	}

	// Gmail methods
	public void getPlaces() {
		final Call<ResponseBody> call = service.getPlacesGMaps(latlng[r], key,
				radius);
		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				call.enqueue(new Callback<ResponseBody>() {

					@Override
					public void onFailure(Call<ResponseBody> arg0,
							Throwable arg1) {
						arg1.printStackTrace();
					}

					@Override
					public void onResponse(Call<ResponseBody> arg0,
							Response<ResponseBody> arg1) {
						JSONObject jsonObject = null;
						try {
							jsonObject = new JSONObject(arg1.body().string());
							JSONArray jsonArray = jsonObject
									.getJSONArray("results");
							Gson gson = new Gson();
							for (int i = 0; i < jsonArray.length(); i++) {
								BusinessGMaps business = gson.fromJson(
										jsonArray.get(i).toString(),
										BusinessGMaps.class);
								publishData.publishFromGmaps(business,
										regions[r], count);
								count++;
							}
							String nextPageToken = jsonObject
									.getString("next_page_token");
							if (nextPageToken != null) {
								getNextPlacesGmaps(nextPageToken);
							}
						} catch (JSONException | IOException
								| NullPointerException e) {
							e.printStackTrace();
							r++;
						}
					}
				});
			}
		}, 3000);// between requests need some time to not be blocked and denied
					// from google
	}

	public void getNextPlacesGmaps(String token) {
		final Call<ResponseBody> call = service.getNextPlacesGMaps(latlng[r],
				key, radius, token);

		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				call.enqueue(new Callback<ResponseBody>() {

					@Override
					public void onFailure(Call<ResponseBody> arg0,
							Throwable arg1) {
						arg1.printStackTrace();
					}

					@Override
					public void onResponse(Call<ResponseBody> arg0,
							Response<ResponseBody> arg1) {
						JSONObject jsonObject = null;
						try {
							jsonObject = new JSONObject(arg1.body().string());
							JSONArray jsonArray = jsonObject
									.getJSONArray("results");
							Gson gson = new Gson();
							for (int i = 0; i < jsonArray.length(); i++) {
								BusinessGMaps business = gson.fromJson(
										jsonArray.get(i).toString(),
										BusinessGMaps.class);
								publishData.publishFromGmaps(business,
										regions[r], count);
								count++;
							}
							String nextPageToken = jsonObject
									.getString("next_page_token");
							System.err.println("token : " + nextPageToken);
							if (nextPageToken != null) {
								getNextPlacesGmaps(nextPageToken);
							}
						} catch (JSONException | IOException
								| NullPointerException e) {
							e.printStackTrace();
							r++;
							if (r < regions.length) {
								// sheet = workbook.createSheet(regions[r]);
								getPlaces();
							}
						}
					}
				});
			}
		}, 3000);

	}

}
