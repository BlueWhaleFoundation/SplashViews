package foundation.bluewhale.splash.network.result.receiveData

import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal

@Parcelize
data class DBEPTransaction constructor(
        val _id: String
        , val txId: String
        , val storeName: String?
        , val type: String
        , val from: String
        , val to: String
        , val bpForOneBWX: BigDecimal
        , val tokenAmount: BigDecimal
        , val leftToken: BigDecimal
        , val currency: String
        , val fromInfo: BEPInfo
        , val toInfo: BEPInfo
        , val createdAt: Long
        , val updatedAt: Long
) : Parcelable {
    @Parcelize
    data class BEPInfo constructor(
            val address: String
            ,val name: String?
    ) : Parcelable

    @IgnoredOnParcel
    var headerId: Long = 0
}


//{
//    "_id": "5c6dfe0294490ef2b6ccf5bc",
//    "txId": "5c6dfe0294490ef2b6ccf5bb",
//    "type": "EXCHANGE",
//    "from": "5c6dfe0294490ef2b6ccf5bb",
//    "to": "5c6dfe0294490ef2b6ccf5bb",
//    "bpForOneBWX": 0.095,
//    "tokenAmount": 500,
//    "leftToken": 150,
//    "currency": "BWX",
//    "fromInfo": {
//    "address": "string"
//},
//    "toInfo": {
//    "address": "string"
//},
//    "createdAt": 0,
//    "updatedAt": 0
//}
