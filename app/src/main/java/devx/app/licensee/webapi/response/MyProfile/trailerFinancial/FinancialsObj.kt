package devx.app.licensee.webapi.response.MyProfile.trailerFinancial

import devx.app.licensee.webapi.response.requelist.RentelItems
import devx.app.seller.webapi.response.trailerslist.Photos

data class FinancialsObj(
    val total: String,
    val rentalId:String,
    //val totalByTypeList: List<TotalByType>,
    //val invoicesList: List<Invoices>,
    val trailerDetails:RentedItems,
    val isLicenseePaid:Boolean,
    val adminPayment:AdminPayment,
    val incoming:List<Incoming>,
    val outgoing:List<Incoming>
)

data class Incoming (

    val amount:Double
)

data class AdminPayment (
val transfer:Transfer
)

data class Transfer (
    val amount:Double
)

data class RentalObject (
    val rentedItems:List<RentedItems>
)

data class RentedItems (
    val name: String,
    val photos: List<Photos>
    /*val rentedItemId: String,
    val total: String,
    val itemType:String,
    val totalCharges:TotalCharges*/
)
