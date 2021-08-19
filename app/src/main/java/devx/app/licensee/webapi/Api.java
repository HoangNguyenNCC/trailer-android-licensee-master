package devx.app.licensee.webapi;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import devx.app.licensee.common.ChangeRentalStatusRequest;
import devx.app.licensee.webapi.response.addUpSell.AddUpSellItemResponse;
import devx.app.licensee.webapi.response.home.HomeResponse;
import devx.app.licensee.webapi.response.login.LoginResponse;
import devx.app.licensee.webapi.response.requelist.RentalRequest;
import devx.app.licensee.webapi.response.requelist.TrailerRequestListReponse;
import devx.app.seller.webapi.response.BookingChargesResponse;
import devx.app.seller.webapi.response.CommonResponse;
import devx.app.seller.webapi.response.notification.NotifListResponse;
import devx.app.seller.webapi.response.upSellItem.UpSellItemResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * Created by Aarna Systems Pvt.Ltd. on 17/5/16.
 * Developer : Sumit Untwal
 */
public interface Api {

    @POST("/employee/signin")
    Call<LoginResponse> singinApi(
            @Body JsonObject jsonObject
    );

    @Headers("Content-Type: application/json")
    @POST("/licensee/email/verify/resend")
    Call<CommonResponse> sendEmailVerifyLink(
            @Body JsonObject jsonObject
    );

    @Multipart
    @POST("/licensee")
    Call<ResponseBody> signUp(@Part("reqBody") RequestBody signupRequest,
                                @Part MultipartBody.Part employeePhoto,
                                @Part MultipartBody.Part employeeAdditionalDocumentScan,
                                @Part MultipartBody.Part employeeDriverLicenseScan,
                                @Part MultipartBody.Part licenseeProofOfIncorporation,
                                @Part MultipartBody.Part licenseeLogo);

    @Headers("Content-Type: application/json")
    @GET
    Call<HomeResponse> getAllTrailers(
            @Url String url,
            @QueryMap HashMap<String, String> queryMap
    );

    @Headers("Content-Type: application/json")
    @GET
    Call<UpSellItemResponse> getAllTrailersUpsellsForBooking(
            @Url String url,
            @QueryMap HashMap<String, String> queryMap
    );

    @Headers("Content-Type: application/json")
    @GET
    Call<TrailerRequestListReponse> getAllRequest(
            @Url String url,
            @QueryMap HashMap<String, String> queryMap
    );
    @GET
    Call<AddUpSellItemResponse> getUpSellOrTrailerItems(
            @Url String url,
            @QueryMap HashMap<String, String> queryMap
    );
    @GET
    Call<NotifListResponse> getMyNotifications(
            @Url String url,
            @Query("id") String id);

    @Multipart
    @PUT("/employee/profile")
    Call<CommonResponse> updateEmployeeProfile(
            @Part("reqBody") RequestBody updateRequest,
            @Part MultipartBody.Part employeePhoto,
            @Part MultipartBody.Part employeeAdditionalDocumentScan,
            @Part MultipartBody.Part employeeDriverLicenseScan
    );
    @Multipart
    @PUT("/employee/profile")
    Call<CommonResponse> updateEmployeeProfileWithoutLicenseDoc(
            @Part("reqBody") RequestBody updateRequest,
            @Part MultipartBody.Part employeePhoto
    );

    @Multipart
    @PUT("/employee/profile")
    Call<CommonResponse> updateEmployeeProfile(
            @Part("reqBody") RequestBody updateRequest,
            @Part MultipartBody.Part employeePhoto,
            @Part MultipartBody.Part employeeAdditionalDocumentScan
    );


    @Multipart
    @PUT("/employee/profile")
    Call<CommonResponse> updateEmployeeWithoutDocument(
            @Part("reqBody") RequestBody updateRequest,
            @Part MultipartBody.Part employeePhoto,
            @Part MultipartBody.Part employeeDriverLicenseScan
    );
    @Multipart
    @PUT("/employee/profile")
    Call<CommonResponse> updateEmployeeWithoutLicense(
            @Part("reqBody") RequestBody updateRequest,
            @Part MultipartBody.Part employeePhoto,
            @Part MultipartBody.Part employeeAdditionalDocumentScan
    );
    @Multipart
    @PUT("/licensee")
    Call<CommonResponse> updateLicenseeProfile(
            @Part("reqBody") RequestBody updateRequest,
            @Part MultipartBody.Part licenseeLogo,
            @Part  MultipartBody.Part licenseeProofOfIncorporation
    );
    @Multipart
    @PUT("/licensee")
    Call<CommonResponse> updateLicenseeProfile(
            @Part("reqBody") RequestBody updateRequest,
            @Part MultipartBody.Part licenseeLogo
    );

    @Multipart
    @POST("/insurance")
    Call<CommonResponse> itemInsurance(
            @Part("reqBody")RequestBody trailerDetails,
            @Part MultipartBody.Part insuranceDocument
    );
    @Multipart
    @POST("/insurance")
    Call<CommonResponse> itemInsurance(
            @Part("reqBody")RequestBody trailerDetails
    );
    @Multipart
    @POST("/servicing")
    Call<CommonResponse> itemServicing(
            @Part("reqBody") RequestBody trailerDetails,
            @Part MultipartBody.Part servicingDocument
    );
    @Multipart
    @POST("/servicing")
    Call<CommonResponse> itemServicing(
            @Part("reqBody") RequestBody trailerDetails
    );
    @Multipart
    @POST("/trailer")
    Call<CommonResponse> addTrailer(
            @Part("reqBody") RequestBody trailerDetails,
            @Part List<MultipartBody.Part> photos,
            @Part MultipartBody.Part insuranceDocument,
            @Part MultipartBody.Part servicingDocument
    );
    @Multipart
    @POST("/trailer")
    Call<CommonResponse> editTrailer(
            @Part("reqBody") RequestBody trailerDetails,
            @Part List<MultipartBody.Part> photos
    );
    @POST("/employee/invite")
    Call<CommonResponse> sendEmployeeInvitation(
            @Body JsonObject jsonObject
    );

    @POST("/trailer/block")
    Call<CommonResponse> addScheduleCallAPI(
            @Body JsonObject jsonObject
    );

    @POST("/booking/charges")
    Call<BookingChargesResponse> getBookingCharges(
            @Body JsonObject jsonObject
    );

    @Multipart
    @POST("/rental/status")
    Call<ResponseBody> startReturn(
            @Part("reqBody") RequestBody rentalStatus,
            @Part MultipartBody.Part driverLicenseScan
    );

    @POST("/rental/status")
    Call<ResponseBody> setStatus(
            @Body ChangeRentalStatusRequest rentalStatusRequest
    );
}
