package com.curve.delivery.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.curve.delivery.MyApplication
import com.curve.delivery.model.AddBankRequest
import com.curve.delivery.model.AddVehicleRequest
import com.curve.delivery.model.LoginRequest
import com.curve.delivery.model.NewPasswordRequest
import com.curve.delivery.model.OTPVerifyRequest
import com.curve.delivery.model.RegisterRequest
import com.curve.delivery.model.ResetPassRequest
import com.curve.delivery.new_architecture.helper.InternetConnection
import com.curve.delivery.response.AcceptOrdersResponse
import com.curve.delivery.response.AcceptRequest
import com.curve.delivery.response.AddBankResp
import com.curve.delivery.response.AddVehicleResp
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
import com.curve.delivery.response.RateAndReviewResponse
import com.curve.delivery.response.RegisterResp
import com.curve.delivery.response.RejectOrdersResponse
import com.curve.delivery.response.RejectRequest
import com.curve.delivery.response.ResetPassResp
import com.curve.delivery.response.UpdateDeliveryAddressRequest
import com.curve.delivery.response.UpdateDeliveryAddressResponse
import com.curve.delivery.response.UpdateDutyStatusResponse
import com.curve.delivery.response.UpdatePasswordResponse
import com.curve.delivery.response.UpdateProfileRequest
import com.curve.delivery.response.UploadImageResp
import com.curve.delivery.util.RetrofitClient
import com.curve.delivery.util.handleApiResponse
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.licotex.driver.adapter.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class M1ViewModel : ViewModel() {
    val mLoginResp = MutableLiveData<LoginResp>()
    val mSignupResp = MutableLiveData<RegisterResp>()
    val mOtpVerifyResp = MutableLiveData<OTPVerifyResp>()
    val mAddBankResp = MutableLiveData<AddBankResp>()
    val mAddVehicleResp = MutableLiveData<AddVehicleResp>()
    val mRestPasswordResp = MutableLiveData<ResetPassResp>()
    val mNewPasswordResp = MutableLiveData<NewPasswordResp>()
    val mUploadImageResp = MutableLiveData<UploadImageResp>()
    val getPendingOrdersResponse = MutableLiveData<GetPendingOrdersResponse>()
    val acceptOrdersResponse = MutableLiveData<AcceptOrdersResponse>()
    val rejectOrdersResponse = MutableLiveData<RejectOrdersResponse>()
    val getOrdersDetailsResponse = MutableLiveData<GetOrdersDetailsResponse>()
    val ordersPickupResponse = MutableLiveData<OrderPickupResponse>()
    val ordersDeliveredResponse = MutableLiveData<OrderPickupResponse>()
    val getDutyResponse = MutableLiveData<GetDutyStatusResponse>()
    val getDashboardDataResponse = MutableLiveData<GetDashboardDataResponse>()
    val updateDeliveryAddressResponse = MutableLiveData<UpdateDeliveryAddressResponse>()
    val getProfileDetailsResponse = MutableLiveData<GetProfileDetailsResponse?>()
    val updateProfileResponse = MutableLiveData<GetProfileDetailsResponse>()
    val getEarningResponse = MutableLiveData<MyEarningResponse>()
    val getOrderHistoryResponse = MutableLiveData<GetOrderDetailsResponse>()
    val deleteAccountResponse = MutableLiveData<DeleteAccountResponse>()
    val rateReviewResponse = MutableLiveData<RateAndReviewResponse>()
    val cmsManagementResponse = MutableLiveData<CMSResponse>()
    val faqListResponse = MutableLiveData<FaqListResponse>()
    val updatePassword = MutableLiveData<UpdatePasswordResponse>()
    val deleteResponse = MutableLiveData<UpdatePasswordResponse>()
    val updateDutyResponse = MutableLiveData<UpdateDutyStatusResponse>()
    val dataShow = MutableLiveData<GetOrdersDetailsResponse.Data>()
    var isNetworkAvailable = MutableLiveData<Boolean>(true)
    var mError = MutableLiveData<String>()
    var error = MutableLiveData<Throwable>()
    var mDeliveryId = mutableStateOf("")
    var logout = MutableLiveData<Boolean>()
    val directionMapApiResponse = MutableLiveData<DirectionsResponse>()

    val signupType = MutableLiveData<Int>()

    var mImageType = mutableStateOf(1)
    var mProfileImgUrl = mutableStateOf("")
    var mBasicFrontUrl = mutableStateOf("")
    var mPoliceBacjground = mutableStateOf("")
    var mBasicBackUrl = mutableStateOf("")
    var mVehicleUrl1 = mutableStateOf("")
    var mVehicleUrl2 = mutableStateOf("")
    var mVehicleUrl3 = mutableStateOf("")
    val messagesList = mutableStateListOf<Message>()
    var orderID = mutableStateOf("")
    var userID = mutableStateOf("")
    val isChecked = mutableStateOf(false)
    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    // Update the name value
    fun updateName(newName: String) {
        _name.value = newName
    }


    private val _currentComposable = MutableStateFlow(ComposableType.First)
    val currentComposable: StateFlow<ComposableType> = _currentComposable

    fun navigateTo(composableType: ComposableType) {
        _currentComposable.value = composableType
    }

    enum class ComposableType {
        First, Second, Third
    }

    fun checkInternetConnection(): Boolean {
        return if (InternetConnection.checkConnection(MyApplication.appContext)) {
            true
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                isNetworkAvailable.value = false
            }
            false
        }
    }


    fun updateSignupType(newType: Int) {
        signupType.value = newType
    }

    fun uploadFile(userUpload: MultipartBody.Part?) {
        viewModelScope.launch {
            handleApiResponse(
                call = { RetrofitClient.apiService.uploadFile(userUpload) },
                onSuccess = {
                    mUploadImageResp.value = it
                    progress.value = false
                },
                onError = {
                    mError.value = it
                    progress.value = false
                })
        }
    }

    /* fun uploadFile(userUpload:MultipartBody.Part?) {
         RetrofitClient.apiService.uploadFile(userUpload)
             .subscribeOn(Schedulers.io())
             .observeOn(AndroidSchedulers.mainThread())
             .doOnSubscribe {
                 progress.value = true
             }.doOnTerminate {
                 progress.value = false
             }
             .subscribe({
                 mUploadImageResp.value = it
             },
                 {
                     error.value = it
                 })
     }*/


    fun directionMapApi(origin: String, destination: String, key: String) {
        viewModelScope.launch {
            try {
                val directionResponse =
                    RetrofitClient.placesService.getDirections(origin, destination, key)
                directionMapApiResponse.postValue(directionResponse.body())
            } catch (e: Exception) {
                directionMapApiResponse.postValue(DirectionsResponse(null))
            }
        }
    }

    fun getPendingOrders(deliveryId: String, accessToken: String) {
        viewModelScope.launch {
            handleApiResponse(
                call = { RetrofitClient.apiService.getPendingRequest(deliveryId, accessToken) },
                onSuccess = {
                    getPendingOrdersResponse.value = it
                    progress.value = false
                },
                onError = {
                    mError.value = it
                    progress.value = false
                })
        }
    }

    fun acceptOrders(deliverParticlaurId: String, accessToken: String, model: AcceptRequest) {
        viewModelScope.launch {
            handleApiResponse(
                call = {
                    RetrofitClient.apiService.acceptOrders(
                        deliverParticlaurId,
                        accessToken,
                        model
                    )
                },
                onSuccess = {
                    acceptOrdersResponse.value = it
                    progress.value = false
                },
                onError = {
                    mError.value = it
                    progress.value = false
                })
        }
    }

    fun rejectOrders(deliverParticlaurId: String, accessToken: String, model: RejectRequest) {
        viewModelScope.launch {
            handleApiResponse(
                call = {
                    RetrofitClient.apiService.rejectOrders(
                        deliverParticlaurId,
                        accessToken,
                        model
                    )
                },
                onSuccess = {
                    rejectOrdersResponse.value = it
                    progress.value = false
                },
                onError = {
                    mError.value = it
                    progress.value = false
                })
        }
    }

    fun getOrdersDetails(deliverParticlaurId: String, accessToken: String) {
        viewModelScope.launch {
            handleApiResponse(
                call = { RetrofitClient.apiService.getOrders(deliverParticlaurId, accessToken) },
                onSuccess = {
                    getOrdersDetailsResponse.value = it
                    progress.value = false
                },
                onError = {
                    mError.value = it
                    progress.value = false
                })
        }
    }

    fun getOrdersPickup(deliveryId: String, deliverParticlaurId: String, accessToken: String) {
        viewModelScope.launch {
            handleApiResponse(
                call = {
                    RetrofitClient.apiService.ordersPickup(
                        deliveryId,
                        deliverParticlaurId,
                        accessToken
                    )
                },
                onSuccess = {
                    ordersPickupResponse.value = it
                    progress.value = false
                },
                onError = {
                    mError.value = it
                    progress.value = false
                })
        }
    }

    fun getOrdersDelivered(deliveryId: String, deliverParticlaurId: String, accessToken: String) {
        viewModelScope.launch {
            handleApiResponse(
                call = {
                    RetrofitClient.apiService.ordersDelivered(
                        deliveryId,
                        deliverParticlaurId,
                        accessToken
                    )
                },
                onSuccess = {
                    ordersDeliveredResponse.value = it
                    progress.value = false
                },
                onError = {
                    mError.value = it
                    progress.value = false
                })
        }
    }


    fun getDutyApi(accessToken: String) {
        viewModelScope.launch {
            handleApiResponse(
                call = { RetrofitClient.apiService.getDutyStatus(accessToken) },
                onSuccess = {
                    getDutyResponse.value = it
                    progress.value = false
                },
                onError = {
                    mError.value = it
                    progress.value = false
                })
        }
    }

    fun getDashboardData(accessToken: String, deliveryBoyId: String) {
        viewModelScope.launch {
            handleApiResponse(
                call = { RetrofitClient.apiService.getDashboardData(accessToken, deliveryBoyId) },
                onSuccess = {
                    getDashboardDataResponse.value = it
                    progress.value = false
                },
                onError = {
                    mError.value = it
                    progress.value = false
                })
        }
    }

    fun updateDeliveryAddressResponse(accessToken: String, model: UpdateDeliveryAddressRequest) {
        viewModelScope.launch {
            handleApiResponse(
                call = { RetrofitClient.apiService.updateDeliveryAddress(accessToken, model) },
                onSuccess = {
                    updateDeliveryAddressResponse.value = it
                    progress.value = false
                },
                onError = {
                    mError.value = it
                    progress.value = false
                })
        }
    }

    fun getProfileDetails(accessToken: String) {
        viewModelScope.launch {
            handleApiResponse(
                call = { RetrofitClient.apiService.getProfileDeliveryDetails(accessToken) },
                onSuccess = {
                    getProfileDetailsResponse.value = it
                    progress.value = false
                },
                onError = {
                    mError.value = it
                    progress.value = false
                })
        }
    }

    fun updateProfile(accessToken: String, model: UpdateProfileRequest) {
        viewModelScope.launch {
            handleApiResponse(
                call = { RetrofitClient.apiService.updateProfile(accessToken, model) },
                onSuccess = {
                    updateProfileResponse.value = it
                    progress.value = false
                },
                onError = {
                    mError.value = it
                    progress.value = false
                })
        }
    }

    fun getEarning(accessToken: String, model: EarningRequest) {
        viewModelScope.launch {
            handleApiResponse(
                call = { RetrofitClient.apiService.myEarning(accessToken, model) },
                onSuccess = {
                    getEarningResponse.value = it
                    progress.value = false
                },
                onError = {
                    mError.value = it
                    progress.value = false
                })
        }
    }

    fun getOrderHistory(accessToken: String) {
        viewModelScope.launch {
            handleApiResponse(
                call = { RetrofitClient.apiService.getOrderHistory(accessToken) },
                onSuccess = {
                    getOrderHistoryResponse.value = it
                    progress.value = false
                },
                onError = {
                    mError.value = it
                    progress.value = false
                })
        }
    }

    fun deleteAccountResponse(accessToken: String) {
        viewModelScope.launch {
            handleApiResponse(
                call = { RetrofitClient.apiService.getDeletedAccount(accessToken) },
                onSuccess = {
                    deleteAccountResponse.value = it
                    progress.value = false
                },
                onError = {
                    mError.value = it
                    progress.value = false
                })
        }
    }

    fun rateReviewResponse(accessToken: String) {
        viewModelScope.launch {
            handleApiResponse(
                call = { RetrofitClient.apiService.getRateAndReview(accessToken) },
                onSuccess = {
                    rateReviewResponse.value = it
                    progress.value = false
                },
                onError = {
                    mError.value = it
                    progress.value = false
                })
        }
    }

    fun cmsManagement(accessToken: String, type: String, service: String) {
        viewModelScope.launch {
            handleApiResponse(
                call = { RetrofitClient.apiService.cmsManagement(accessToken, type, service) },
                onSuccess = {
                    cmsManagementResponse.value = it
                    progress.value = false
                },
                onError = {
                    mError.value = it
                    progress.value = false
                })
        }
    }

    fun faqListResponse(accessToken: String, service: String) {
        viewModelScope.launch {
            handleApiResponse(
                call = { RetrofitClient.apiService.faqList(accessToken, service) },
                onSuccess = {
                    faqListResponse.value = it
                    progress.value = false
                },
                onError = {
                    mError.value = it
                    progress.value = false
                })
        }
    }

    fun updatePassword(accessToken: String, model: NewPasswordRequest) {
        viewModelScope.launch {
            handleApiResponse(
                call = { RetrofitClient.apiService.updatePassword(accessToken, model) },
                onSuccess = {
                    updatePassword.value = it
                    progress.value = false
                },
                onError = {
                    mError.value = it
                    progress.value = false
                })
        }
    }

    fun logout(accessToken: String) {
        viewModelScope.launch {
            handleApiResponse(
                call = { RetrofitClient.apiService.logout(accessToken) },
                onSuccess = {
                    deleteResponse.value = it
                    progress.value = false
                },
                onError = {
                    mError.value = it
                    progress.value = false
                })
        }
    }

    fun updateDutyApi(deliveryBoyId: String, status: Boolean, accessToken: String) {
        viewModelScope.launch {
            handleApiResponse(
                call = {
                    RetrofitClient.apiService.updateDutyStatus(
                        deliveryBoyId,
                        status,
                        accessToken
                    )
                },
                onSuccess = {
                    updateDutyResponse.value = it
                    progress.value = false
                },
                onError = {
                    mError.value = it
                    progress.value = false
                })
        }
    }

    fun hitLogin(userRequest: LoginRequest) {
        viewModelScope.launch {
            handleApiResponse(
                call = { RetrofitClient.apiService.hitLogin1(userRequest) },
                onSuccess = { mLoginResp.value = it },
                onError = { mError.value = it })
        }
    }


    var progress = MutableLiveData<Boolean>()


    fun hitRegister(userRequest: RegisterRequest) {
        viewModelScope.launch {
            handleApiResponse(
                call = { RetrofitClient.apiService.hitRegister(userRequest) },
                onSuccess = {
                    mSignupResp.value = it
                    progress.value = false
                },
                onError = {
                    mError.value = it
                    progress.value = false
                })
        }
    }


    fun hitVerifyOtp(token: String, userRequest: OTPVerifyRequest) {
        viewModelScope.launch {
            handleApiResponse(
                call = { RetrofitClient.apiService.hitVerifyOtp(token, userRequest) },
                onSuccess = { mOtpVerifyResp.value = it },
                onError = { mError.value = it })
        }
    }


    fun hitAddBank(token: String, userRequest: AddBankRequest) {
        viewModelScope.launch {
            handleApiResponse(
                call = { RetrofitClient.apiService.hitAddBank(token, userRequest) },
                onSuccess = { mAddBankResp.value = it },
                onError = { mError.value = it })
        }
    }


    fun hitAddVehicle(token: String, userRequest: AddVehicleRequest) {
        viewModelScope.launch {
            handleApiResponse(
                call = { RetrofitClient.apiService.hitAddVehicle(token, userRequest) },
                onSuccess = { mAddVehicleResp.value = it },
                onError = { mError.value = it })
        }
    }

    fun hitResetPassword(token: String, userRequest: ResetPassRequest) {

        viewModelScope.launch {
            handleApiResponse(
                call = { RetrofitClient.apiService.hitResetPassword(token, userRequest) },
                onSuccess = { mRestPasswordResp.value = it },
                onError = { mError.value = it })
        }
    }

    fun hitNewPassword(token: String, userRequest: NewPasswordRequest) {

        viewModelScope.launch {
            handleApiResponse(
                call = { RetrofitClient.apiService.hitNewPassword(token, userRequest) },
                onSuccess = { mNewPasswordResp.value = it },
                onError = { mError.value = it })
        }
    }

    fun chatFetch(dbReference: DatabaseReference) {
        dbReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Handle chat messages
                if (snapshot.child("chat").child("messages").exists()) {
                    messagesList.clear()
                    snapshot.child("chat").child("messages").children.forEach { messageSnapshot ->
                        val senderId =
                            messageSnapshot.child("senderId").getValue(String::class.java) ?: ""
                        val receiverId =
                            messageSnapshot.child("receiverId").getValue(String::class.java) ?: ""
                        val messageTypeValue =
                            messageSnapshot.child("messageType").getValue(Int::class.java) ?: 1
                        val message =
                            messageSnapshot.child("message").getValue(String::class.java) ?: ""
                        val timestamp =
                            messageSnapshot.child("timestamp").getValue(Long::class.java) ?: 0
                        var receiverImage =
                            messageSnapshot.child("receiverImage").getValue(String::class.java)
                        var senderImage =
                            messageSnapshot.child("senderImage").getValue(String::class.java)
                        val messageObject = Message(
                            senderId,
                            receiverId,
                            messageTypeValue,
                            message,
                            timestamp,
                            receiverImage,
                            senderImage
                        )
                        messagesList.add(messageObject)
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

    }

    fun moveToLogin(msg: String) {
        if (msg.contains("Session Expired") || msg.contains("Please provide access token")) {
            logout.value = true
        }
    }
}