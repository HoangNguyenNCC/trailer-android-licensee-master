package devx.app.licensee.webapi;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import devx.app.licensee.common.ChangeRentalStatusRequest;
import devx.app.licensee.common.utils.ConstantApi;
import devx.app.licensee.webapi.response.MyProfile.profile.MyProfileResponse;
import devx.app.licensee.webapi.response.MyProfile.trailerFinancial.TrailerFinancialResponse;
import devx.app.licensee.webapi.response.accessControlList.AccessControlListResponse;
import devx.app.licensee.webapi.response.addUpSell.AddUpSellItemResponse;
import devx.app.licensee.webapi.response.geocoding.Response;
import devx.app.licensee.webapi.response.home.HomeResponse;
import devx.app.licensee.webapi.response.login.LoginResponse;
import devx.app.licensee.webapi.response.trailerslist.TrailerAddedByAdmin;
import devx.app.seller.webapi.response.CommonResponse;
import devx.app.seller.webapi.response.notification.NotifListResponse;
import devx.app.seller.webapi.response.trailerslist.TrailerListResp;
import devx.app.seller.webapi.response.upSellItem.UpSellItemByAdminResponse;
import devx.app.seller.webapi.response.upSellItem.UpSellItemResponse;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class CallApi {

    public CallApi() {
    }

    public Observable<LoginResponse> makeLoginUser(JsonObject loginJsonObject) {
        ITrailerApi iTrailerApi = ServiceGenerator.getLiveEndNode();
        return iTrailerApi.makeUserLogin(loginJsonObject);
    }

    public Observable<CommonResponse> signUp(RequestBody signupRequest,
                                           MultipartBody.Part employeePhoto,
                                           MultipartBody.Part employeeAdditionalDocumentScan,
                                           MultipartBody.Part employeeDriverLicenseScan,
                                           MultipartBody.Part licenseeProofOfIncorporation,
                                           MultipartBody.Part licenseeLogo) {
        ITrailerApi iTrailerApi = ServiceGenerator.getLiveEndNode();
        return iTrailerApi.signUp(signupRequest,
                employeePhoto,
                employeeAdditionalDocumentScan,
                employeeDriverLicenseScan,
                licenseeProofOfIncorporation,
                licenseeLogo);
    }

    public Observable<CommonResponse> stopDeliveryTracking(RequestBody requestBody,
                                             MultipartBody.Part employeePhoto) {
        ITrailerApi iTrailerApi = ServiceGenerator.getLiveEndNode();
        return iTrailerApi.stopDeliveryTracking(requestBody,
                employeePhoto);
    }

    public Observable<ResponseBody> startReturnProcess(RequestBody requestBody,
                                                       MultipartBody.Part employeePhoto) {
        ITrailerApi iTrailerApi = ServiceGenerator.getLiveEndNode();
        return iTrailerApi.startReturn(requestBody,
                employeePhoto);
    }

    public Observable<ResponseBody> setStatus(ChangeRentalStatusRequest body){
        ITrailerApi iTrailerApi = ServiceGenerator.getLiveEndNode();
        return iTrailerApi.setStatus(body);
    }

    public Observable<CommonResponse> startDeliveryTracking(RequestBody requestBody) {
        ITrailerApi iTrailerApi = ServiceGenerator.getLiveEndNode();
        return iTrailerApi.startDeliveryTracking(requestBody);
    }

    public Observable<CommonResponse> updateLicenseeProfile(RequestBody signupRequest,
                                             MultipartBody.Part licenseeLogo,
                                                            MultipartBody.Part incorporation) {
        ITrailerApi iTrailerApi = ServiceGenerator.getLiveEndNode();
        return iTrailerApi.updateLicenseeProfile(signupRequest,
                licenseeLogo,incorporation);

    }

    public Observable<CommonResponse> updateEmployeeProfile(RequestBody signupRequest,
                                                            MultipartBody.Part licensePic,
                                                            MultipartBody.Part additionalDoc,
                                                            MultipartBody.Part photo) {
        ITrailerApi iTrailerApi = ServiceGenerator.getLiveEndNode();
        return iTrailerApi.updateEmployeeProfile(signupRequest,licensePic,additionalDoc,
                photo);
    }

    public Observable<CommonResponse> employeeInviteAccept(RequestBody signupRequest,
                                                            MultipartBody.Part licensePic,
                                                            MultipartBody.Part additionalDoc,
                                                            MultipartBody.Part photo) {
        ITrailerApi iTrailerApi = ServiceGenerator.getLiveEndNode();
        return iTrailerApi.employeeInviteAccept(signupRequest,licensePic,additionalDoc,
                photo);
    }

    public Observable<CommonResponse> updateEmployeeProfile(RequestBody signupRequest) {
        ITrailerApi iTrailerApi = ServiceGenerator.getLiveEndNode();
        return iTrailerApi.updateEmployeeProfile(signupRequest);
    }

    public Observable<CommonResponse> deleteEmployeeProfile(JsonObject signupRequest) {
        ITrailerApi iTrailerApi = ServiceGenerator.getLiveEndNode();
        return iTrailerApi.deleteEmployeeProfile(signupRequest);
    }
    public Observable<CommonResponse> deleteTrailer(JsonObject signupRequest) {
        ITrailerApi iTrailerApi = ServiceGenerator.getLiveEndNode();
        return iTrailerApi.deleteTrailer(signupRequest);
    }
    public Observable<CommonResponse> deleteUpsell(JsonObject signupRequest) {
        ITrailerApi iTrailerApi = ServiceGenerator.getLiveEndNode();
        return iTrailerApi.deleteUpsell(signupRequest);
    }
    public Observable<CommonResponse> sendOTPForLogin(JsonObject jsonObject) {
        ITrailerApi iTrailerApi = ServiceGenerator.getLiveEndNode();
        return iTrailerApi.sendOTPForLogin(jsonObject);
    }

    public Observable<CommonResponse> verifyOTP(JsonObject jsonObject) {
        ITrailerApi iTrailerApi = ServiceGenerator.getLiveEndNode();
        return iTrailerApi.verifyOTP(jsonObject);
    }

    public Observable<CommonResponse> createUserAccount(JsonObject jsonObject) {
        ITrailerApi iTrailerApi = ServiceGenerator.getLiveEndNode();
        return iTrailerApi.createUserAccount1(jsonObject);
    }

    public Observable<CommonResponse> updateTrailerLocation(JsonObject jsonObject) {
        ITrailerApi iTrailerApi = ServiceGenerator.getLiveEndNode();
        return iTrailerApi.updateTrailerLocation(jsonObject);
    }

    public Observable<CommonResponse> sendEmployeeInvitation(JsonObject jsonObject) {
        ITrailerApi iTrailerApi = ServiceGenerator.getLiveEndNode();
        return iTrailerApi.sendEmployeeInvitation(jsonObject);
    }

    public Observable<CommonResponse> addUserAddress(JsonObject jsonObject) {
        ITrailerApi iTrailerApi = ServiceGenerator.getLiveEndNode();
        return iTrailerApi.updateUserDetails(jsonObject);
    }

    public Observable<CommonResponse> updateUserDetails(JsonObject jsonObject) {
        ITrailerApi iTrailerApi = ServiceGenerator.getLiveEndNode();
        return iTrailerApi.updateUserDetails(jsonObject);
    }

    public Observable<TrailerListResp> getFeaturedTrailers(HashMap<String, String> requestMap) {
        ITrailerApi iTrailerApi = ServiceGenerator.getLiveEndNode();
        return iTrailerApi.getFeaturedTrailers(ConstantApi.LIVE_BASE_URL + "trailers?", requestMap);
    }

    public Observable<TrailerAddedByAdmin> getTrailersByAdmin(String url) {
        ITrailerApi iTrailerApi = ServiceGenerator.getLiveEndNode();
        return iTrailerApi.getTrailerByAdmin(url);
    }
    public Observable<UpSellItemByAdminResponse> getUpsellByAdmin(String url) {
        ITrailerApi iTrailerApi = ServiceGenerator.getLiveEndNode();
        return iTrailerApi.getUpsellByAdmin(url);
    }
    public Observable<HomeResponse> getAllTrailer(HashMap<String, String> requestMap) {
        ITrailerApi iTrailerApi = ServiceGenerator.getLiveEndNode();
        return iTrailerApi.getAllTrailers(ConstantApi.LIVE_BASE_URL + "licensee/trailers?", requestMap);
    }

    /*public Observable<TrailerRequestListResponse> getRequestedTrailers(HashMap<String, String> requestMap) {
        ITrailerApi iTrailerApi = ServiceGenerator.getLiveEndNode();
        return iTrailerApi.getAllTrailersRequests(ConstantApi.LIVE_BASE_URL + "rental/requests?", requestMap);
    }*/

    public Observable<MyProfileResponse> fetchMyProfileDetails(HashMap<String, String> requestMap) {
        ITrailerApi iTrailerApi = ServiceGenerator.getLiveEndNode();
        return iTrailerApi.fetchMyProfileDetails(ConstantApi.LIVE_BASE_URL + "employee/profile?", requestMap);
    }

    public Observable<AddUpSellItemResponse> getUpSellOrTrailerItems(String url, HashMap<String, String> requestMap) {
        ITrailerApi iTrailerApi = ServiceGenerator.getLiveEndNode();
        return iTrailerApi.getUpSellOrTrailerItems(url, requestMap);
    }

    public Observable<JSONObject> loadTrailerDetails(String id) {
        ITrailerApi iTrailerApi = ServiceGenerator.getLiveEndNode();
        return iTrailerApi.loadTrailerDetails(ConstantApi.LIVE_BASE_URL + "licensee/trailer", id);
    }

    public Observable<UpSellItemResponse> loadUpsellItems(String id) {
        ITrailerApi iTrailerApi = ServiceGenerator.getLiveEndNode();
        return iTrailerApi.loadUpsellItems(ConstantApi.LIVE_BASE_URL + "upsellitems", id);
    }

    public Observable<NotifListResponse> getMyNotifications(String userId) {
        ITrailerApi iTrailerApi = ServiceGenerator.getLiveEndNode();
        return iTrailerApi.getMyNotifications(ConstantApi.LIVE_BASE_URL + "reminders", userId);
    }

    public Observable<CommonResponse> cancelTrailer(String rentalId, String cartId, String canceledByType, String canceledBy, String canceledOn) {
        ITrailerApi iTrailerApi = ServiceGenerator.getLiveEndNode();
        return iTrailerApi.cancelTrailer(rentalId, cartId, canceledByType, canceledBy, canceledOn);
    }

    public Observable<TrailerFinancialResponse> getTrailerFinancialData(String startDate, String endDate) {
        ITrailerApi iTrailerApi = ServiceGenerator.getLiveEndNode();
        return iTrailerApi.getTrailerFinancialData(startDate, endDate);
    }
    public Observable<TrailerFinancialResponse> getTrailerFinancialData(String userType) {
        ITrailerApi iTrailerApi = ServiceGenerator.getLiveEndNode();
        return iTrailerApi.getTrailerFinancialData(userType);
    }
    public Observable<AccessControlListResponse> getAccessControlList() {
        ITrailerApi iTrailerApi = ServiceGenerator.getLiveEndNode();
        return iTrailerApi.getAccessControlList();
    }

    public Observable<CommonResponse> addUpSellItem(RequestBody upsellDetails,
                                                    ArrayList<MultipartBody.Part> photos) {
        ITrailerApi iTrailerApi = ServiceGenerator.getLiveEndNode();
        return iTrailerApi.addUpSellItem(upsellDetails,photos);
    }

  /* AddTrailrRequst: { name: "2020 C&B DMP610-6TA 26"", vin: "123456789" type: "dump", description: "2 5/16" ADJUSTABLE COUPLER, 8000# JACK, LOOSE RAMPS, SCISSOR HOIST",
            size: "Length: 10' Width: 6'", capacity: "6980", tare: "2920", age: 3,
            features: ["2 5/16\" ADJUSTABLE COUPLER", "8000# JACK", "LOOSE RAMPS", "SCISSOR HOIST", "26\" SIDES", "STAKE POCKETS", "LED LIGHTS", "HD FENDERS"],
        photos: [ "imggg"],      availability: true,     rentalCharges: 120,     dlrCharges: 400 }*/


    public Observable<CommonResponse> addTrailer(RequestBody trailerDetails,
                                                 ArrayList<MultipartBody.Part> photos,
                                                 MultipartBody.Part insuranceDoc,
                                                 MultipartBody.Part servicingDoc) {
        ITrailerApi iTrailerApi = ServiceGenerator.getLiveEndNode();
        return iTrailerApi.addTrailer(trailerDetails,photos,insuranceDoc,servicingDoc);
    }

    public Observable<CommonResponse> rateTrailer(String itemType, String trailerId, String userId, String ratingValue) {
        ITrailerApi iTrailerApi = ServiceGenerator.getLiveEndNode();
        return iTrailerApi.rateTrailer(itemType, trailerId, userId, ratingValue);
    }

    public Observable<CommonResponse> itemInsurance(RequestBody trailerDetails,
                                                    MultipartBody.Part insuranceDoc) {
        ITrailerApi iTrailerApi = ServiceGenerator.getLiveEndNode();
        return iTrailerApi.itemInsurance(trailerDetails,insuranceDoc);
    }

    public Observable<CommonResponse> itemServicing(RequestBody trailerDetails,
                                                    MultipartBody.Part serviceDoc) {
        ITrailerApi iTrailerApi = ServiceGenerator.getLiveEndNode();
        return iTrailerApi.itemServicing(trailerDetails, serviceDoc);
    }

    public Observable<CommonResponse> onTrailerOptionSelected(JsonObject jsonObject) {
        ITrailerApi iTrailerApi = ServiceGenerator.getLiveEndNode();
        return iTrailerApi.onTrailerOptionSelected(jsonObject);
    }

    public Observable<CommonResponse> sendForgotPasswordOTP(String emailId, String mobileNumber) {
        ITrailerApi iTrailerApi = ServiceGenerator.getLiveEndNode();
        return iTrailerApi.sendForgotPasswordOTP(emailId, mobileNumber);
    }

    public Observable<CommonResponse> verifyOTP(String emailId, String otp, String mobileNumber) {
        ITrailerApi iTrailerApi = ServiceGenerator.getLiveEndNode();
        return iTrailerApi.verifyOTP(emailId, otp, mobileNumber);
    }

    public Observable<CommonResponse> resetPassword(String authToken, String password) {
        ITrailerApi iTrailerApi = ServiceGenerator.getLiveEndNode();
        return iTrailerApi.resetPassword(authToken, password);
    }


    public Observable<CommonResponse> changePwd( JsonObject jsonobject ) {
        ITrailerApi iTrailerApi = ServiceGenerator.getLiveEndNode();
        return iTrailerApi.changePwd(jsonobject);
    }
}
