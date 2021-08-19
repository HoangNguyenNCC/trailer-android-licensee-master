package devx.app.licensee.webapi.response.geocoding;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public final class Response {
    Result[] results;
    String status;
    String errorMessage;


    protected Response(JSONObject json) throws JSONException {
        if (json.has("results")) {
            JSONArray jsonResults = json.getJSONArray("results");
            results = new Result[jsonResults.length()];
            for (int i = 0; i < jsonResults.length(); i++)
                results[i] = new Result(jsonResults.getJSONObject(i));
        }

        status = json.getString("status");

        if (json.has("error_message"))
            errorMessage = json.getString("error_message");
    }

    public Result[] getResults() {
        return results;
    }

    public String getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Result[] getResults(String addressType) {
        List<Result> filter = new ArrayList<>();

        for (Result result : getResults())
            for (int i = 0; i < result.getAddressTypes().length; i++) {
                String type = result.getAddressTypes()[i];
                if (type.equals(addressType)) {
                    filter.add(result);
                    break;
                }
            }

        return filter.toArray(new Result[filter.size()]);
    }
}

/*



{
		"results" : [
		{
		"address_components" : [
		{
		"long_name" : "Prism Tower",
		"short_name" : "Prism Tower",
		"types" : [ "premise" ]
		},
		{
		"long_name" : "Sahakar Marg",
		"short_name" : "Sahakar Marg",
		"types" : [ "route" ]
		},
		{
		"long_name" : "Lalkothi",
		"short_name" : "Lalkothi",
		"types" : [ "political", "sublocality", "sublocality_level_1" ]
		},
		{
		"long_name" : "Jaipur",
		"short_name" : "Jaipur",
		"types" : [ "locality", "political" ]
		},
		{
		"long_name" : "Jaipur",
		"short_name" : "Jaipur",
		"types" : [ "administrative_area_level_2", "political" ]
		},
		{
		"long_name" : "Rajasthan",
		"short_name" : "RJ",
		"types" : [ "administrative_area_level_1", "political" ]
		},
		{
		"long_name" : "India",
		"short_name" : "IN",
		"types" : [ "country", "political" ]
		},
		{
		"long_name" : "302015",
		"short_name" : "302015",
		"types" : [ "postal_code" ]
		}
		],
		"formatted_address" : "Prism Tower, Sahakar Marg, Lalkothi, Jaipur, Rajasthan 302015, India",
		"geometry" : {
		"bounds" : {
		"northeast" : {
		"lat" : 26.886559,
		"lng" : 75.79853349999999
		},
		"southwest" : {
		"lat" : 26.8863023,
		"lng" : 75.7983909
		}
		},
		"location" : {
		"lat" : 26.8864529,
		"lng" : 75.79848679999999
		},
		"location_type" : "ROOFTOP",
		"viewport" : {
		"northeast" : {
		"lat" : 26.8877796302915,
		"lng" : 75.7998111802915
		},
		"southwest" : {
		"lat" : 26.8850816697085,
		"lng" : 75.79711321970849
		}
		}
		},
		"place_id" : "ChIJ8ZmPpiW0bTkRbk6X6N67EoY",
		"types" : [ "premise" ]
		}
		],
		"status" : "OK"
		}


*/

