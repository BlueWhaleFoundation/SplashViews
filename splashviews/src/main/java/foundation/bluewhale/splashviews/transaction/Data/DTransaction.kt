package foundation.bluewhale.splash.network.result.receiveData

import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal

@Parcelize
data class DTransaction constructor(val _id: String?
                                    , val transactionId: String?
                                    , val status: String?
                                    , val amount: BigDecimal?
                                    , val channel: String?
                                    , val method: String?
                                    , val type: String?
                                    , val from: String?
                                    , val fromInfo: UserInfo?
                                    , val to: String?
                                    , val toInfo: UserInfo?
                                    , val currency: String?
                                    , val orderInfo: OrderInfo?
                                    , val paymentInfo: PaymentInfo?
                                    , val bonusInfo: BonusInfo?
                                    , val rewardInfo: RewardInfo?
                                    , val withdrawInfo: WithdrawInfo?
                                    , val createdAt: Long?
                                    , val updatedAt: Long?
                                    , val memo: String?
) : Parcelable {
    @Parcelize
    data class UserInfo constructor(val name: String?
                                    , val email: String?
                                    , val tel: String?
                                    , val leftBp: BigDecimal?
    ) : Parcelable

    @Parcelize
    data class PaymentInfo constructor(val thirdPartyId: String?
                                       , val business: String?
                                       , val storeName: String?
    ) : Parcelable

    @Parcelize
    data class BonusInfo constructor(val business: String?
                                     , val storeName: String?
    ) : Parcelable

    @Parcelize
    data class RewardInfo constructor(val rewardRatio: Double?
    ) : Parcelable

    @Parcelize
    data class WithdrawInfo constructor(val bankName: String?
                                        , val bankAccount: String?
    ) : Parcelable

    @Parcelize
    data class OrderInfo constructor(val businessId: String?
                                     , val storeName: String?
    ) : Parcelable


    @IgnoredOnParcel
    var headerId:Long = 0
}