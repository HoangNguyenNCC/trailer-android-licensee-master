package devx.app.licensee.webapi.response.MyProfile.trailerFinancial

data class TotalCharges(
    val discount: String,
    val dlrCharges: String,
    val lateFees: String,
    val rentalCharges: String,
    val t2yCommission: String,
    var total: String,
    val payable:String,
    val charges:Charges
)

data class Charges (
    val discount: String,
    val dlrCharges: String,
    val lateFees: String,
    val rentalCharges: String,
    val t2yCommission: String,
    var total: String
)
