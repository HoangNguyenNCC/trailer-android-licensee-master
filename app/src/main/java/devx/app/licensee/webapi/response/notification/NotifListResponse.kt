package devx.app.seller.webapi.response.notification

import devx.app.seller.webapi.response.trailerslist.Photos

data class NotifListResponse(
    val message: String,
    val remindersList: List<Notifications>,
    val success: Boolean
)

data class Notifications(
    val invoiceId :String,
    val itemId: String,
    val expiryDate: String,
    val itemPhoto: Photos,
    val itemName: String,
    val statusText: String,
    val expiringItemId: String,
    val reminderColor: String,
    val isPickUp:Boolean,
    val reminderType:String,
    val customerName:String,
    val customerPhoto:String,
    val rentedItems: List<RentalItem>
)

data class RentalItem(
    val itemType:String,
    val itemId:String,
    val itemName:String,
    val itemPhoto:Photos
)

/*
{
"success": 1,:
"message": "Successfully fetched Reminders data",
"remindersList": [
{
  "itemId": "5e58914c9d61b40017de39f8",
  "itemName": "TuffMate2000",
  "itemPhoto": "http://trailer2you.herokuapp.com/file/trailer/5e58914c9d61b40017de39f8/1",
  "reminderType": "Insurance Deadline",
  "expiringItemId": "5e784f236b5f2d0017bc7795",
  "expiryDate": "2020-05-01T00:00:00.000Z",
  "statusText": "36 Days",
  "reminderColor": "#00BE75"
},
{
  "itemId": "5e58914c9d61b40017de39f8",
  "itemName": "TuffMate2000",
  "itemPhoto": "http://trailer2you.herokuapp.com/file/trailer/5e58914c9d61b40017de39f8/1",
  "reminderType": "Service Deadline",
  "expiringItemId": "5e784f8e6b5f2d0017bc7796",
  "expiryDate": "2020-06-05T00:00:00.000Z",
  "statusText": "71 Days",
  "reminderColor": "#00BE75"
}
]
}


* */