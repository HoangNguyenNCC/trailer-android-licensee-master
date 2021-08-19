package devx.app.seller.webapi.response.trailerslist

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Trailers(
    val __v: Int,
    val _id: String,
    val age: Int,
    val availability: Boolean,
    val capacity: String,
    val createdAt: String,
    val description: String,
    val dlrCharges: Int,
    val features: List<String>,
    val isFeatured: Boolean,
    val licenseeId: String,
    val name: String,
    val photos: List<Photos>?,
    val rating: Float,
    val rentalCharges: RentalCharges,
    val size: String,
    val tare: String,
    val type: String,
    val updatedAt: String,
    val vin: String,
    //added following acco to /trailers api resp
    val licensee: String,
    val price: String,
    val photo:String,
    val isSelected: Boolean=false,
    val adminRentalItemId:String,
    val trailerModel:String,
    val insured:Boolean=false,
    val serviced:Boolean=false,
    val insurance :Insurance,
    val servicing:Servicing,
    var flag:Boolean=false,
    val trailerAvailability:TrailerAvailability
) : Parcelable

@Parcelize
data class TrailerAvailability (
    val availability:Boolean,
    val ongoing:Ongoing,
    val upcoming:Ongoing
):Parcelable

@Parcelize
data class Ongoing (
    val rentalItemId:String,
    val invoiceId:String,
    val startDate:String,
    val endDate:String
):Parcelable

@Parcelize
data class Photos (
    var data:String="",
    var _id:String=""
):Parcelable

@Parcelize
data class Servicing (
    val serviceDate:String,
    val nextDueDate:String,
    val name:String,
    val document:String
):Parcelable

@Parcelize
data class Insurance (
    val issueDate:String,
    val expiryDate:String,
    val document:String
):Parcelable
