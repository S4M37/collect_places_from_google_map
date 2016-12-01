package api.app;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import api.parser.gmaps.GmapsParser;
import api.parser.heremaps.HereMapsParser;
import api.services.PublishDataInExcel;
import api.services.RetrofitUtils;

public class App {

	public static int count = 0;
	static HSSFWorkbook workbook;

	public static String GMAPS_KEY = "AIzaSyCHJWZFP6Xx_MrwOeJlWXXMJ98JGzxxhIM";
	static String[] latlng = { "34.529361,10.496020" };
	static String[] regions = { "Mahares" };

	// Here params
	private static final String appId = "RYlAuCm2kgTSUNqa7DvC";
	private static final String appCode = "zYUsXpTPy6xBNn43KHWV4Q";

	// La marsa
	// private static final String in =
	// "36.9008985,10.2892277,36.8704203,10.3097723,36.8801153,10.3256583,36.8992013,10.3261823,36.9008985,10.2892277";

	// Grand Tunis
	private static final String borders = "34.512035, 10.471129,34.527028, 10.525632,34.534664, 10.512070,34.545481, 10.499196,34.541946, 10.484175,34.515571, 10.468812,34.512035, 10.471129"
			.replaceAll(" ", "");
	private static final String[] categories = { "eat-drink", "going-out",
			"sights-museums", "transport", "accommodation", "shopping",
			"leisure-outdoor", "administrative-areas-buildings",
			"natural-geographical", "petrol-station", "atm-bank-exchange",
			"toilet-rest-area", "hospital-health-care-facility" };
	static int catCounter = 0;

	public static void main(String[] args) {

		workbook = new HSSFWorkbook();

		PublishDataInExcel publishDataGmaps = new PublishDataInExcel();
		publishDataGmaps.setFilename("D:/gmaps.xls").setWorkbook(workbook)
				.setSheet(workbook.createSheet("Gmaps Sheet"));

		// google maps

		GmapsParser gmapsParser = new GmapsParser();
		gmapsParser.setKey(GMAPS_KEY).setLatlng(latlng)
				.setPublishData(publishDataGmaps)
				.setService(RetrofitUtils.getRetrofitServicesGmapsInstance());

		// gmapsParser.getPlaces();

		// HERE maps
		PublishDataInExcel publishDataHere = new PublishDataInExcel();
		publishDataHere.setFilename("D:/here.xls").setWorkbook(workbook)
				.setSheet(workbook.createSheet("Here Sheet"));

		HereMapsParser hereMapsParser = new HereMapsParser();
		hereMapsParser.setAppCode(appCode);
		hereMapsParser.setAppId(appId);
		hereMapsParser.setPublishData(publishDataHere);
		hereMapsParser.setRegion("region");
		hereMapsParser.setCategories(categories);
		hereMapsParser.setBorders(borders);
		hereMapsParser.setService(RetrofitUtils
				.getRetrofitServiceHereMapsInstance());

		hereMapsParser.getPlaces();
	}
}
