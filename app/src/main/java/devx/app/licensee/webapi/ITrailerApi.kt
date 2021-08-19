package devx.app.licensee.webapi

import com.google.gson.JsonObject
import devx.app.licensee.common.ChangeRentalStatusRequest
import devx.app.licensee.webapi.response.MyProfile.profile.MyProfileResponse
import devx.app.licensee.webapi.response.MyProfile.trailerFinancial.TrailerFinancialResponse
import devx.app.licensee.webapi.response.accessControlList.AccessControlListResponse
import devx.app.licensee.webapi.response.addUpSell.AddUpSellItemResponse
import devx.app.licensee.webapi.response.home.HomeResponse
import devx.app.licensee.webapi.response.login.LoginResponse
import devx.app.licensee.webapi.response.trailerslist.TrailerAddedByAdmin
import devx.app.seller.webapi.response.CommonResponse
import devx.app.seller.webapi.response.notification.NotifListResponse
import devx.app.seller.webapi.response.trailerslist.TrailerListResp
import devx.app.seller.webapi.response.upSellItem.UpSellItemByAdminResponse
import devx.app.seller.webapi.response.upSellItem.UpSellItemResponse
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.*
import java.util.*

interface ITrailerApi {

    @GET("/licensee/employee/acl")
    fun getAccessControlList(): Observable<AccessControlListResponse>

    @Headers("Content-Type: application/json")
    @POST("/employee/signin")
    fun makeUserLogin(
            @Body jsonObject: JsonObject
    ): Observable<LoginResponse>

    @Multipart
    @POST("/licensee")
    fun signUp(
            @Part("reqBody") signupRequest: RequestBody,
            @Part employeePhoto: MultipartBody.Part,
            @Part employeeAdditionalDocumentScan: MultipartBody.Part,
            @Part employeeDriverLicenseScan: MultipartBody.Part,
            @Part licenseeProofOfIncorporation: MultipartBody.Part,
            @Part licenseeLogo: MultipartBody.Part
    ): Observable<CommonResponse>

    @Multipart
    @POST("/rental/location/track")
    fun stopDeliveryTracking(
            @Part("reqBody") requestBody: RequestBody,
            @Part driverLicenseScan: MultipartBody.Part
    ): Observable<CommonResponse>

    @Multipart
    @POST("/rental/location/track")
    fun startDeliveryTracking(
            @Part("reqBody") requestBody: RequestBody
    ): Observable<CommonResponse>

    @Multipart
    @PUT("/licensee")
    fun updateLicenseeProfile(
            @Part("reqBody") updateRequest: RequestBody,
            @Part licenseeLogo: MultipartBody.Part,
            @Part licenseeProofOfIncorporation: MultipartBody.Part
    ): Observable<CommonResponse>


    @Multipart
    @PUT("/employee/profile")
    fun updateEmployeeProfile(
            @Part("reqBody") updateRequest: RequestBody,
            @Part employeePhoto: MultipartBody.Part,
            @Part employeeAdditionalDocumentScan: MultipartBody.Part,
            @Part employeeDriverLicenseScan: MultipartBody.Part
    ): Observable<CommonResponse>

    @Multipart
    @POST("/employee/invite/accept")
    fun employeeInviteAccept(
            @Part("reqBody") updateRequest: RequestBody,
            @Part employeePhoto: MultipartBody.Part,
            @Part employeeAdditionalDocumentScan: MultipartBody.Part,
            @Part employeeDriverLicenseScan: MultipartBody.Part
    ): Observable<CommonResponse>

    @Multipart
    @PUT("/employee/profile")
    fun updateEmployeeProfile(
            @Part("reqBody") updateRequest: RequestBody
    ): Observable<CommonResponse>

    @Headers("Content-Type: application/json")
    @HTTP(method = "DELETE", path = "/employee", hasBody = true)
    fun deleteEmployeeProfile(
            @Body jsonObject: JsonObject
    ): Observable<CommonResponse>


    @Headers("Content-Type: application/json")
    @HTTP(method = "DELETE", path = "/trailer", hasBody = true)
    fun deleteTrailer(
            @Body jsonObject: JsonObject
    ): Observable<CommonResponse>

    @Headers("Content-Type: application/json")
    @HTTP(method = "DELETE", path = "/upsellitem", hasBody = true)
    fun deleteUpsell(
            @Body jsonObject: JsonObject
    ): Observable<CommonResponse>

    @Headers("Content-Type: application/json")
    @POST("/licensee/otp/resend")
    fun sendOTPForLogin(
            @Body jsonObject: JsonObject
    ): Observable<CommonResponse>

    @Headers("Content-Type: application/json")
    @POST("/licensee/otp/verify")
    fun verifyOTP(
            @Body jsonObject: JsonObject
    ): Observable<CommonResponse>


    @FormUrlEncoded
    @POST("/signup")
    fun createUserAccount(
            @Field("email") emailId: String,
            @Field("password") password: String,
            @Field("name") name: String,
            @Field("mobile") mobile: String,
            @Field("dob") dob: String,
            @Field("address") address: String,
            @Field("driverLicense") driverLicence: String,
            @Field("creditCard") creditCard: String
    ): Observable<LoginResponse>

    @Headers("Content-Type: application/json")
    @POST("/licensee")
    fun createUserAccount1(
            @Body jsonObject: JsonObject
    ): Observable<CommonResponse>

    @POST("/rental/location")
    fun updateTrailerLocation(
            @Body jsonObject: JsonObject
    ): Observable<CommonResponse>

    @PUT("/user")
    fun updateUserDetails(
            @Body jsonObject: JsonObject
    ): Observable<CommonResponse>

    @POST("/rental/approval")
    fun onTrailerOptionSelected(
            @Body jsonObject: JsonObject
    ): Observable<CommonResponse>

    @Headers("Content-Type: application/json")
    @GET
    fun getFeaturedTrailers(
            @Url url: String,
            @QueryMap queryMap: HashMap<String, String>
    ): Observable<TrailerListResp>


    @Headers("Content-Type: application/json")
    @GET
    fun getTrailerByAdmin(
            @Url url: String
    ): Observable<TrailerAddedByAdmin>

    @Headers("Content-Type: application/json")
    @GET
    fun getUpsellByAdmin(
            @Url url: String
    ): Observable<UpSellItemByAdminResponse>

    @Headers("Content-Type: application/json")
    @GET
    fun getAllTrailers(
            @Url url: String,
            @QueryMap queryMap: HashMap<String, String>
    ): Observable<HomeResponse>

    /*@Headers("Content-Type: application/json")
    @GET
    fun getAllTrailersRequests(
        @Url url: String,
        @QueryMap queryMap: HashMap<String, String>
    ): Observable<TrailerRequestListResponse>
*/
    @GET
    fun fetchMyProfileDetails(
            @Url url: String,
            @QueryMap queryMap: HashMap<String, String>
    ): Observable<MyProfileResponse>

    @GET
    fun getUpSellOrTrailerItems(
            @Url url: String,
            @QueryMap queryMap: HashMap<String, String>
    ): Observable<AddUpSellItemResponse>

    @GET
    fun loadTrailerDetails(
            @Url url: String,
            @Query("id") id: String
    ): Observable<JSONObject>

    @GET
    fun loadUpsellItems(
            @Url url: String,
            @Query("trailerId") trailerId: String
    ): Observable<UpSellItemResponse>

    @GET
    fun getMyNotifications(@Url url: String, @Query("id") id: String): Observable<NotifListResponse>

    @FormUrlEncoded
    @POST("/rental/cancel")
    fun cancelTrailer(
            @Field("rentalId") rentalId: String,
            @Field("cartId") cartId: String,
            @Field("canceledByType") canceledByType: String,
            @Field("canceledBy") canceledBy: String,
            @Field("canceledOn") canceledOn: String
    ): Observable<CommonResponse>

   // @Headers("Content-Type: application/json")
    @Multipart
    @POST("/upsellitem")
    fun addUpSellItem(
           @Part("reqBody") upsellDetails: RequestBody,
           @Part photos: ArrayList<MultipartBody.Part>
   ): Observable<CommonResponse>

    @POST("employee/invite")
    fun sendEmployeeInvitation(

            @Body jsonObject: JsonObject
    ): Observable<CommonResponse>

    //@Headers("Content-Type: application/json")
    @Multipart
    @POST("/trailer")
    fun addTrailer(
            @Part("reqBody") trailerDetails: RequestBody,
            @Part photos: List<MultipartBody.Part>,
            @Part insuranceDocument: MultipartBody.Part,
            @Part servicingDocument: MultipartBody.Part
    ): Observable<CommonResponse>

    @FormUrlEncoded
    @POST("/rental/cancel")
    fun rateTrailer(
            @Field("itemType") rentalId: String,
            @Field("itemId") cartId: String,
            @Field("ratedByUserId") canceledByType: String,
            @Field("ratingValue") canceledBy: String
    ): Observable<CommonResponse>

    @GET("/licensee/financial")
    fun getTrailerFinancialData(
            @Query("startDate") startDate: String,
            @Query("endDate") endDate: String
    ): Observable<TrailerFinancialResponse>

    @GET("/licensee/financial")
    fun getTrailerFinancialData(
            @Query("userType") userType: String): Observable<TrailerFinancialResponse>

    //@Headers("Content-Type: application/json")
    @Multipart
    @POST("/insurance")
    fun itemInsurance(
            @Part("reqBody") trailerDetails: RequestBody,
            @Part insuranceDocument: MultipartBody.Part
    ): Observable<CommonResponse>

    //@FormUrlEncoded
    @Multipart
    @POST("/servicing")
    fun itemServicing(
            @Part("reqBody") trailerDetails: RequestBody,
            @Part servicingDocument: MultipartBody.Part
    ): Observable<CommonResponse>

    @FormUrlEncoded
    @POST("/licensee/generateotp")
    fun sendForgotPasswordOTP(
            @Field("email") emailId: String,
            @Field("mobile") mobileNumber: String
    ): Observable<CommonResponse>

    @FormUrlEncoded
    @POST("/licensee/generateotp")
    fun verifyOTP(
            @Field("email") emailId: String,
            @Field("otp") otp: String,
            @Field("mobile") mobileNumber: String
    ): Observable<CommonResponse>

    @FormUrlEncoded
    @PUT("/employee/resetpassword")
    fun resetPassword(
            @Field("token") authToken: String,
            @Field("password") password: String
    ): Observable<CommonResponse>

    //Multipart
    @POST("user/updateprofile")
    fun updateProfile(@Part("user_id") id: RequestBody,
                      @Part image: MultipartBody.Part,
                      @Part("other") other: RequestBody) : Observable<ResponseBody>

    @Headers("Content-Type: application/json")
    @PUT("/employee/password/change")
    fun changePwd(
            @Body jsonObject: JsonObject
    ): Observable<CommonResponse>

    @Multipart
    @POST("/rental/status")
    fun startReturn(
            @Part("reqBody") rentalStatus: RequestBody,
            @Part driverLicenseScan: MultipartBody.Part
    ): Observable<ResponseBody>

    @POST("/rental/status")
    fun setStatus(
            @Body rentalStatusRequest: ChangeRentalStatusRequest
    ): Observable<ResponseBody>

}
