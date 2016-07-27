import gmap.BusinessGMaps;
import here.BusinessHere;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;

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

	// Gmaps params
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

	// Here params
	private static final String appId = "RYlAuCm2kgTSUNqa7DvC";
	private static final String appCode = "zYUsXpTPy6xBNn43KHWV4Q";

	// La marsa
	// private static final String in =
	// "36.9008985,10.2892277,36.8704203,10.3097723,36.8801153,10.3256583,36.8992013,10.3261823,36.9008985,10.2892277";

	// Grand Tunis
	private static final String in = "37.129560,10.162504,37.022643,9.877539,36.927832,9.929044,36.756383,9.926298,36.648482,9.918058,36.622034,10.310819,36.795983,10.472867,37.129560,10.162504";

	private static final String q = "eat and drink";

	public static void main(String[] args) {

		HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
		interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
		OkHttpClient client = new OkHttpClient.Builder().addInterceptor(
				interceptor).build();
		Retrofit retrofit = new retrofit2.Retrofit.Builder()
				.baseUrl(RetrofitServices.baseUrlHere).client(client).build();

		service = retrofit.create(RetrofitServices.class);
		filename = "D:/NewExcelFile.xls";
		workbook = new HSSFWorkbook();
		sheet = workbook.createSheet(regions[r]);

		// google maps
		// getPlacesFromGMaps();

		// HERE maps
		getPlacesFromHere();

	}

	// Gmail methods
	public static void getPlacesFromGMaps() {
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
								writeLineGmaps(business);
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

	public static void getNextPlacesGmaps(String token) {
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
								writeLineGmaps(business);
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
								getPlacesFromGMaps();
							}
						}
					}
				});
			}
		}, 3000);

	}

	public static void writeLineGmaps(BusinessGMaps business) {

		try {
			HSSFRow row = sheet.createRow((short) count);
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

	// Here methods
	public static void getPlacesFromHere() {
		final Call<ResponseBody> call = service.getPlacesHere(appId, appCode,
				in, q);
		call.enqueue(new Callback<ResponseBody>() {

			@Override
			public void onFailure(Call<ResponseBody> arg0, Throwable arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onResponse(Call<ResponseBody> arg0,
					Response<ResponseBody> arg1) {
				try {
					JSONObject jsonObject = new JSONObject(arg1.body().string());
					jsonObject = jsonObject.getJSONObject("results");
					JSONArray jsonArray = jsonObject.getJSONArray("items");
					Gson gson = new Gson();
					String nextUrl = jsonObject.getString("next");
					for (int i = 0; i < jsonArray.length(); i++) {
						BusinessHere businessHere = gson.fromJson(jsonArray
								.get(i).toString(), BusinessHere.class);
						writeLineHere(businessHere);
						count++;
					}
					if (nextUrl != null) {
						getNextPlacesHere(nextUrl);
					}
				} catch (IOException | NullPointerException | JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static void getNextPlacesHere(final String next) {
		// final Call<ResponseBody> call = service.getNextPlacesHere(appId,
		// appCode, in, q);

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
					System.err.println(output);

					JSONObject jsonObject = new JSONObject(output);
					// jsonObject.getJSONObject("results");
					System.err.println(jsonObject.toString());
					JSONArray jsonArray = jsonObject.getJSONArray("items");
					Gson gson = new Gson();
					try {
						String nextUrl = jsonObject.getString("next");
						for (int i = 0; i < jsonArray.length(); i++) {
							BusinessHere businessHere = gson.fromJson(jsonArray
									.get(i).toString(), BusinessHere.class);
							writeLineHere(businessHere);
							count++;
						}
						if (nextUrl != null) {
							getNextPlacesHere(nextUrl);
						}
					} catch (JSONException e) {
						getPlacesFromHere();
					}

				} catch (IOException | NullPointerException | JSONException e) {
					e.printStackTrace();
				}

				/*
				 * call.enqueue(new Callback<ResponseBody>() {
				 * 
				 * @Override public void onFailure(Call<ResponseBody> arg0,
				 * Throwable arg1) { arg1.printStackTrace(); }
				 * 
				 * @Override public void onResponse(Call<ResponseBody> arg0,
				 * Response<ResponseBody> arg1) { try { JSONObject jsonObject =
				 * new JSONObject(arg1.body() .string()); //jsonObject =
				 * jsonObject.getJSONObject("results"); JSONArray jsonArray =
				 * jsonObject .getJSONArray("items"); Gson gson = new Gson();
				 * String nextUrl = jsonObject.getString("next"); for (int i =
				 * 0; i < jsonArray.length(); i++) { BusinessHere businessHere =
				 * gson.fromJson( jsonArray.get(i).toString(),
				 * BusinessHere.class); writeLineHere(businessHere); count++; }
				 * if (nextUrl != null) { getNextPlacesHere(nextUrl); } } catch
				 * (IOException | NullPointerException | JSONException e) {
				 * e.printStackTrace(); } } });
				 */
			}
		}, 3000);

	}

	public static void writeLineHere(BusinessHere businessHere) {
		try {
			HSSFRow row = sheet.createRow((short) count);
			if (businessHere.category != null)
				row.createCell(0).setCellValue(businessHere.category.title);
			row.createCell(1).setCellValue(businessHere.title);
			String tags = "";
			if (businessHere.tags != null)
				for (int j = 0; j < businessHere.tags.length; j++) {
					tags += businessHere.tags[j].title + ", ";
				}
			if (businessHere.vicinity != null)
				row.createCell(2).setCellValue(tags);
			row.createCell(3).setCellValue(
					businessHere.vicinity.replaceAll("<br/>", " "));
			row.createCell(4).setCellValue(
					businessHere.position[0] + "," + businessHere.position[1]);
			FileOutputStream fileOut = new FileOutputStream(filename);
			workbook.write(fileOut);
			fileOut.close();
			System.out.println("Your excel file has been modified!");

		} catch (Exception ex) {
			System.out.println(ex);
		}

	}
}
