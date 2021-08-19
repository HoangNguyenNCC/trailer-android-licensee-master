package devx.app.seller.webapi.response

data class BookingChargesResponse(
    val totalPayableAmount : String,
    val message: String,
    val trailerCharges:TrailerCharges
)

data class TrailerCharges (
    val dlrCharges:String,
    val taxes :String,
    val rentalCharges :String,
    val lateFees :String

)
