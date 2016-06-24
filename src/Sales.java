import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

public class Sales {

	public static int count = 0;
	static HSSFWorkbook workbook;
	static int r = 0;
	static HSSFSheet sheet;
	static String filename;
	static RetrofitServices service;

	// google place api web service KEY for server
	public static String key = "YOUR_API_KEY";
	// radius with meters
	public static final int radius = 4000;
	// list of Lat-Lon "lat,long" Ex: "36.923623, 10.277558"
	static String[] latlng = { "36.8892467,10.1655893", "36.808533,10.135356",
			"36.772090,10.234230", "36.7712706,10.2774014",
			"36.830981,10.180098", "36.923623, 10.277558",
			"36.871426,10.342927", "36.871426,10.342927",
			"36.952291, 10.198928", "36.8490815,10.1675927",
			"36.8385529,10.1481056" };
	// ListOfRegion
	static String[] regions = { "riadh landalos", "Bardo", "megrin", "rades",
			"sidi bousaid", "carthage", "gammarth", "menzah", "cité olympique",
			"raoued", "manar 2" };

	public static void main(String[] args) {

		HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
		interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
		OkHttpClient client = new OkHttpClient.Builder().addInterceptor(
				interceptor).build();
		Retrofit retrofit = new retrofit2.Retrofit.Builder()
				.baseUrl(RetrofitServices.baseUrl).client(client).build();

		service = retrofit.create(RetrofitServices.class);
		filename = "D:/NewExcelFile.xls";
		workbook = new HSSFWorkbook();
		sheet = workbook.createSheet(regions[r]);
		getPlaces();

	}

	public static void getPlaces() {
		final Call<ResponseBody> call = service.getPlaces(latlng[r], key,
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
								Business business = gson.fromJson(jsonArray
										.get(i).toString(), Business.class);
								writeLine(business, count);
								count++;
							}
							String nextPageToken = jsonObject
									.getString("next_page_token");
							if (nextPageToken != null) {
								getNextPlaces(nextPageToken);
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

	public static void getNextPlaces(String token) {
		final Call<ResponseBody> call = service.getNextPlaces(latlng[r], key,
				radius, token);

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
								Business business = gson.fromJson(jsonArray
										.get(i).toString(), Business.class);
								writeLine(business, count);
								count++;
							}
							String nextPageToken = jsonObject
									.getString("next_page_token");
							System.err.println("token : " + nextPageToken);
							if (nextPageToken != null) {
								getNextPlaces(nextPageToken);
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

	public static void writeLine(Business business, int i) {

		try {
			HSSFRow row = sheet.createRow((short) i);
			row.createCell(0).setCellValue(regions[r]);
			row.createCell(1).setCellValue(business.name);
			String subCategories = "";
			for (int j = 0; j < business.types.length; j++) {
				subCategories += business.types[j] + ", ";
			}
			row.createCell(2).setCellValue(subCategories);
			row.createCell(3).setCellValue(business.vicinity);
			row.createCell(4).setCellValue(
					business.geometry.location.lat + ","
							+ business.geometry.location.lng);
			FileOutputStream fileOut = new FileOutputStream(filename);
			workbook.write(fileOut);
			fileOut.close();
			System.out.println("Your excel file has been modified!");

		} catch (Exception ex) {
			System.out.println(ex);
		}
	}
}
