package api.parser.interfaces;

import api.parser.gmaps.models.BusinessGMaps;
import api.parser.heremaps.models.BusinessHere;

public interface PublishDataInterface {
	public void publishFromGmaps(BusinessGMaps business, String region,
			int lineNb);

	public void publishFromHere(BusinessHere businessHere, String region,
			int lineNb);

}
