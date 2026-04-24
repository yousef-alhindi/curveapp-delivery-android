package com.curve.delivery.util
import android.media.tv.CommandResponse
import com.curve.delivery.model.AddBankRequest
import com.curve.delivery.model.AddVehicleRequest
import com.curve.delivery.model.LoginRequest
import com.curve.delivery.model.NewPasswordRequest
import com.curve.delivery.model.OTPVerifyRequest
import com.curve.delivery.model.RegisterRequest
import com.curve.delivery.model.ResetPassRequest
import com.curve.delivery.response.AcceptOrdersResponse
import com.curve.delivery.response.AcceptRequest
import com.curve.delivery.response.AddBankResp
import com.curve.delivery.response.AddVehicleResp
import com.curve.delivery.response.AutocompleteResponse
import com.curve.delivery.response.CMSResponse
import com.curve.delivery.response.DeleteAccountResponse
import com.curve.delivery.response.DirectionsResponse
import com.curve.delivery.response.EarningRequest
import com.curve.delivery.response.FaqListResponse
import com.curve.delivery.response.GetDashboardDataResponse
import com.curve.delivery.response.GetDutyStatusResponse
import com.curve.delivery.response.GetOrderDetailsResponse
import com.curve.delivery.response.GetOrdersDetailsResponse
import com.curve.delivery.response.GetPendingOrdersResponse
import com.curve.delivery.response.GetProfileDetailsResponse
import com.curve.delivery.response.LoginResp
import com.curve.delivery.response.MyEarningResponse
import com.curve.delivery.response.NewPasswordResp
import com.curve.delivery.response.OTPVerifyResp
import com.curve.delivery.response.OrderPickupResponse
import com.curve.delivery.response.PlaceDetailsResponse
import com.curve.delivery.response.RateAndReviewResponse
import com.curve.delivery.response.RegisterResp
import com.curve.delivery.response.RejectOrdersResponse
import com.curve.delivery.response.RejectRequest
import com.curve.delivery.response.ResetPassResp
import com.curve.delivery.response.UpdateDeliveryAddressRequest
import com.curve.delivery.response.UpdateDeliveryAddressResponse
import com.curve.delivery.response.UpdateDutyStatusResponse
import com.curve.delivery.response.UpdatePassword
import com.curve.delivery.response.UpdatePasswordResponse
import com.curve.delivery.response.UpdateProfileRequest
import com.curve.delivery.response.UploadImageResp
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {
    @POST("delivery/auth/login")
    suspend fun hitLogin(@Body request: LoginRequest): LoginResp

    @POST("delivery/auth/login")
    suspend fun hitLogin1(@Body request: LoginRequest): Response<LoginResp>

    @POST("delivery/auth/register")
    suspend fun hitRegister1(@Body request: RegisterRequest): RegisterResp

    @POST("delivery/auth/register")
    suspend fun hitRegister(@Body request: RegisterRequest): Response<RegisterResp>

    @POST("delivery/auth/verifyOtp")
    suspend fun hitVerifyOtp1(@Header("accessToken") accessToken: String, @Body request: OTPVerifyRequest): OTPVerifyResp

    @POST("delivery/auth/verifyOtp")
    suspend fun hitVerifyOtp(@Header("accessToken") accessToken: String, @Body request: OTPVerifyRequest): Response<OTPVerifyResp>

    @POST("delivery/auth/addBank")
    suspend fun hitAddBank(@Header("accessToken") accessToken: String, @Body request: AddBankRequest): Response<AddBankResp>

    @GET("maps/api/directions/json")
    suspend fun getDirections(@Query("origin") origin: String,
                              @Query("destination") destination: String,
                              @Query("key") apiKey: String): Response<DirectionsResponse>

    @POST("delivery/auth/addVechile")
    suspend fun hitAddVehicle(
        @Header("accessToken") accessToken: String,
        @Body request: AddVehicleRequest): Response<AddVehicleResp>


    @POST("delivery/auth/resetPassword")
    suspend fun hitResetPassword(
        @Header("accessToken") accessToken: String,
        @Body request: ResetPassRequest): Response<ResetPassResp>


    @POST("delivery/auth/newPassword")
    suspend fun hitNewPassword(
        @Header("accessToken") accessToken: String,
        @Body request: NewPasswordRequest): Response<NewPasswordResp>

    @Multipart
    @POST("delivery/auth/uploadFile")
    suspend fun uploadFile(@Part() upload_delivery_file: MultipartBody.Part? = null): Response<UploadImageResp>

    @GET("/maps/api/place/autocomplete/json")
    suspend fun autocompletePlaces(@Query("input") input: String,
                                   @Query("key") apiKey: String,
                                   @Query("types") types: String = "establishment"): AutocompleteResponse

    @GET("/maps/api/place/details/json")
    suspend fun getPlaceDetails(@Query("place_id") placeId: String, @Query("key") apiKey: String): PlaceDetailsResponse

    @GET("delivery/order/getPendingOrders/{deliverBoyId}")
    suspend fun getPendingRequest(@Path("deliverBoyId") deliverBoyId:String,@Header("accessToken")accessToken:String):Response<GetPendingOrdersResponse>


    @POST("delivery/order/acceptOrderDelivery/{deliveryId}")
    suspend fun acceptOrders(@Path("deliveryId") deliveryId:String,@Header("accessToken") accessToken:String,@Body model:AcceptRequest):Response<AcceptOrdersResponse>

    @POST("delivery/order/rejectOrderDelivery/{deliveryId}")
    suspend fun rejectOrders(@Path("deliveryId") deliveryId:String,@Header("accessToken") accessToken: String,@Body model:RejectRequest):Response<RejectOrdersResponse>

    @GET("delivery/order/getOrderDetails/{deliveryId}")
    suspend fun getOrders(@Path("deliveryId") deliveryId:String,@Header("accessToken") accessToken:String):Response<GetOrdersDetailsResponse>

    @POST("delivery/order/orderPickup/{deliveryId}/{deliveryBoyId}")
    suspend fun ordersPickup(@Path("deliveryId") deliveryId:String,@Path("deliveryBoyId")deliveryBoyId:String,@Header("accessToken")accessToken:String):Response<OrderPickupResponse>

    @POST("delivery/order/orderDelivered/{deliveryId}/{deliveryBoyId}")
    suspend fun ordersDelivered(@Path("deliveryId") deliveryId:String,@Path("deliveryBoyId")deliveryBoyId:String,@Header("accessToken")accessToken:String):Response<OrderPickupResponse>

    @PATCH("delivery/auth/updateDeliveryStatus")
    suspend fun updateDutyStatus(@Query("deliveryBoyId")deliveryBoyId:String,@Query("status") status:Boolean,@Header("accessToken")accessToken:String):Response<UpdateDutyStatusResponse>

    @GET("delivery/auth/deliveryBoyDetails")
    suspend fun getDutyStatus(@Header("accessToken")accessToken:String):Response<GetDutyStatusResponse>

    @GET("delivery/order/getdashboardData")
    suspend fun getDashboardData(@Header("accessToken") accessToken: String,@Query("deliveryBoyId") deliveryBoyId:String):Response<GetDashboardDataResponse>

    //till not used
    @PATCH("delivery/auth/updateDeliveryBoyDetails")
    suspend fun updateDeliveryAddress(@Header("accessToken") accessToken: String,@Body model:UpdateDeliveryAddressRequest):Response<UpdateDeliveryAddressResponse>

    //new response m6
    @GET("delivery/auth/deliveryBoyDetails")
    suspend fun getProfileDeliveryDetails(@Header("accessToken")accessToken:String):Response<GetProfileDetailsResponse>

    @PUT("delivery/auth/updateProfile")
    suspend fun updateProfile(@Header("accessToken")accessToken:String,@Body model:UpdateProfileRequest):Response<GetProfileDetailsResponse>

    @POST("delivery/order/myEarnings")
    suspend fun myEarning(@Header("accessToken")accessToken:String,@Body model:EarningRequest):Response<MyEarningResponse>

    @GET("delivery/order/getOrdersList")
    suspend fun getOrderHistory(@Header("accessToken")accessToken:String):Response<GetOrderDetailsResponse>

    @GET("delivery/auth/deleteDeliveryBoy")
    suspend fun getDeletedAccount(@Header("accessToken")accessToken:String):Response<DeleteAccountResponse>

    @GET("delivery/order/getRatingsAndReviewList")
    suspend fun getRateAndReview(@Header("accessToken")accessToken:String):Response<RateAndReviewResponse>

    @GET("delivery/cms")
    suspend fun cmsManagement(@Header("accessToken")accessToken:String,@Query("type")type:String,@Query("service")service:String):Response<CMSResponse>

    @GET("delivery/cms/faqList")
    suspend fun faqList(@Header("accessToken")accessToken:String,@Query("service")service:String):Response<FaqListResponse>

    @POST("delivery/auth/updatePassword")
    suspend fun updatePassword(@Header("accessToken")accessToken:String,@Body model:NewPasswordRequest):Response<UpdatePasswordResponse>

    @POST("delivery/auth/logout")
    suspend fun logout(@Header("accessToken")accessToken:String):Response<UpdatePasswordResponse>
}