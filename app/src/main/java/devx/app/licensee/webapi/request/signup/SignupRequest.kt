package devx.app.licensee.webapi.request.signup

public class SignupRequest(
    val employee: Employee,
    val licensee: Licensee
)

data class Employee(
    val country: String,
    val driverLicense: DriverLicense,
    val email: String,
    val mobile: String,
    val name: String,
    val password: String,
    val photo: String
)

data class Licensee(
    val accountNumber: String,
    val address: Address,
    val bsbNumber: String,
    val country: String,
    val email: String,
    val logo: String,
    val mobile: String,
    val name: String,
    val proofOfIncorporation: String,
    val workingDays: List<String>,
    val workingHours: String
)

data class DriverLicense(
    val card: String,
    val expiry: String,
    val scan: String,
    val state: String
)

data class Address(
    val coordinates: List<Double>,
    val pincode: String,
    val text: String
)


/*NEWWWWW
* 

{
	"employee": {
        "name": "Owner 1",
        "mobile": "9930603176",
        "country": "india",
        "email": "nehakadam@abc.com",
        "password": "aBcd@1234",
        "photo": "data:image/jpeg;base64,<base64-str>",
        "driverLicense": {
	        "card": "223782weyet",
	        "expiry": "06/23",
	        "state": "NSW",
	        "scan": " data:image/jpeg;base64,<base64-str>"
    	}
    },
    "licensee": {
        "name": "Licensee 1",
        "email": "nehakadam@abc.com",
        "mobile": "9930603176",
        "country": "india",
        "address": {
            "text": "NorthBridge, NSW, Australia",
            "pincode": "1560",
            "coordinates": [151.2172, -33.8132]
        },
		"workingDays": ["Monday","Tuesday","Wednesday"],
    	"workingHours": "0700-1900",
        "proofOfIncorporation": "data:image/png;base64,<base64-str>",        "logo": "data:image/jpeg;base64,<base64-str>",
"bsbNumber":"122393894",
"accountNumber":"392793729"
    }
}


*
* */
