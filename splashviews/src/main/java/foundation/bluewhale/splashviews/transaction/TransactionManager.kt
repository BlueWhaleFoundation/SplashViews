package foundation.bluewhale.splashviews.transaction

import com.google.common.base.Strings
import foundation.bluewhale.splash.network.result.receiveData.DBEPTransaction
import foundation.bluewhale.splash.network.result.receiveData.DTransaction
import foundation.bluewhale.splashviews.R
import foundation.bluewhale.splashviews.util.NumberTool

class TransactionManager {
    companion object {
        val TRANSFER = "TRANSFER"
        val FUSER_TRANSFER = "FUSER_TRANSFER"
        val FUSER_BP_ROLLBACK = "FUSER_BP_ROLLBACK"
        val BONUS = "BONUS"
        val PAYMENT = "PAYMENT"
        val CHARGE = "CHARGE"
        val PAYROLL = "PAYROLL"
        val REWARD = "REWARD"
        val WITHDRAW = "WITHDRAW"
        val EXCHANGE = "EXCHANGE"
        val EXCHANGE_AND_PAY = "EXCHANGE_AND_PAY"

        val WALLET_TRANSFER = "WALLET_TRANSFER"
        val USER_TRANSFER = "USER_TRANSFER"

        val DEFAULT = "DEFAULT"

        const val txNone = 0
        const val txMinus = 1
        const val txPlus = 2

        fun getTxType(userId: String, adapterItem: DTransaction): Int {
            return if (userId == adapterItem.from)
                txMinus
            else if (userId == adapterItem.to)
                txPlus
            else
                txNone
        }

        fun getTxType(userId: String, adapterItem: DBEPTransaction): Int {
            return if (userId == adapterItem.from)
                txMinus
            else if (userId == adapterItem.to)
                txPlus
            else
                txNone
        }

        fun getItem(userId: String, adapterItem: DTransaction): Item {
            try {
                val txType = getTxType(userId, adapterItem)
                val amount = when (txType) {
                    txPlus -> "+" + NumberTool.convert(adapterItem.amount)
                    txMinus -> "-" + NumberTool.convert(adapterItem.amount)
                    else -> ""
                }
                val remain =
                //NumberTool.convert(if (isReceiver) adapterItem.toInfo?.leftBp else adapterItem.fromInfo?.leftBp)
                    when (txType) {
                        txPlus -> NumberTool.convert(adapterItem.toInfo?.leftBp)
                        txMinus -> NumberTool.convert(adapterItem.fromInfo?.leftBp)
                        else -> null
                    }
                //보너스
                if (BONUS == adapterItem.type)
                    return Item(
                        R.string.history_bonus,
                        amount,
                        remain,
                        R.string.history_bonus,
                        adapterItem.bonusInfo!!.storeName!!
                    )
                //송금
                else if (TRANSFER == adapterItem.type)
                    return Item(
                        R.string.history_transfer,
                        amount,
                        remain,
                        when (txType) {
                            txPlus -> R.string.history_transfer_to
                            txMinus -> R.string.history_transfer_from
                            else -> 0
                        },
                        when (txType) {
                            txPlus -> adapterItem.fromInfo!!.name
                            txMinus -> adapterItem.toInfo!!.name
                            else -> null
                        }
                    )
                //비회원 송금
                else if (FUSER_TRANSFER == adapterItem.type)
                    return Item(
                        R.string.history_fuser_transfer,
                        amount,
                        remain,
                        when (txType) {
                            txPlus -> R.string.history_transfer_to
                            txMinus -> R.string.history_transfer_from
                            else -> 0
                        },
                        when (txType) {
                            txPlus -> adapterItem.fromInfo!!.name
                            txMinus -> adapterItem.toInfo!!.name
                            else -> null
                        }
                    )
                //비회원 송금 취소
                else if (FUSER_BP_ROLLBACK == adapterItem.type)
                    return Item(
                        R.string.history_fuser_transfer_rollback,
                        amount,
                        remain,
                        when (txType) {
                            txPlus -> R.string.history_transfer_to
                            txMinus -> R.string.history_transfer_from
                            else -> 0
                        },
                        when (txType) {
                            txPlus -> adapterItem.fromInfo!!.name
                            txMinus -> adapterItem.toInfo!!.name
                            else -> null
                        }
                    )
                //결제
                else if (PAYMENT == adapterItem.type)
                    return Item(
                        R.string.history_payment,
                        amount,
                        remain,
                        when (txType) {
                            txPlus -> R.string.history_pay_refunded
                            txMinus -> R.string.history_pay_complete
                            else -> 0
                        },
                        adapterItem.paymentInfo!!.storeName!!
                    )
                //충전
                else if (CHARGE == adapterItem.type)
                //return new Item(string.history_charge, amount, remain, string.history_charge_complete, adapterItem.getMethod());
                    return Item(
                        R.string.history_charge,
                        amount,
                        remain,
                        R.string.history_charge_complete,
                        adapterItem.fromInfo!!.name!!
                    )
                //급여
                else if (PAYROLL == adapterItem.type)
                    return Item(
                        R.string.history_payroll,
                        amount,
                        remain,
                        when (txType) {
                            txPlus -> R.string.history_payroll_to
                            txMinus -> R.string.history_payroll_from
                            else -> 0
                        },
                        when (txType) {
                            txPlus -> adapterItem.fromInfo!!.name
                            txMinus -> adapterItem.toInfo!!.name
                            else -> null
                        }
                    )
                else if (REWARD == adapterItem.type)
                //리워드
                    return Item(
                        R.string.history_bonus,
                        amount,
                        remain,
                        R.string.history_bonus,
                        adapterItem.bonusInfo!!.storeName!!
                    )
                //출금
                else if (WITHDRAW == adapterItem.type) {
                    val message: String?
                    if (adapterItem.withdrawInfo != null)
                        message =
                            String.format(
                                "%s %s",
                                adapterItem.withdrawInfo.bankName,
                                adapterItem.withdrawInfo.bankAccount
                            )
                    else
                        message = adapterItem.fromInfo!!.name
                    return Item(R.string.history_withdraw, amount, remain, R.string.history_withdraw, message!!)
                }
                //환전
                else if (EXCHANGE == adapterItem.type)
                    return Item(
                        R.string.history_exchange,
                        amount,
                        remain,
                        R.string.history_exchange_complete,
                        adapterItem.toInfo!!.name!!
                    )
                else if (EXCHANGE_AND_PAY == adapterItem.type)
                    return Item(
                        R.string.history_pay_from_bep_tobp,
                        amount,
                        remain,
                        R.string.history_pay_complete,
                        adapterItem.fromInfo!!.name!!
                    )

                //DEFAULT
            } catch (e: Exception) {
            }

            return Item(R.string.history_error_msg, "", "", R.string.history_server_msg, "")
        }

        fun getItem(userId: String, adapterItem: DBEPTransaction): Item {
            try {
                val txType = getTxType(userId, adapterItem)
                val amount = when (txType) {
                    txPlus -> "+" + NumberTool.convert(adapterItem.tokenAmount)
                    txMinus -> "-" + NumberTool.convert(adapterItem.tokenAmount)
                    else -> NumberTool.convert(adapterItem.tokenAmount)
                }

                val remain = NumberTool.convert(adapterItem.leftToken)

                //환전
                if (EXCHANGE == adapterItem.type)
                    return Item(
                        R.string.history_exchange,
                        amount,
                        remain,
                        R.string.history_exchange_complete,
                        adapterItem.toInfo.name
                    )
                //지갑송금
                else if (WALLET_TRANSFER == adapterItem.type)
                    return Item(
                        R.string.history_transfer,
                        amount,
                        remain,
                        when (txType) {
                            txPlus -> R.string.history_transfer_to
                            txMinus -> R.string.history_transfer_from
                            else -> 0
                        },
                        when (txType) {
                            txPlus -> {
                                if (!Strings.isNullOrEmpty(adapterItem.fromInfo.name))
                                    adapterItem.fromInfo.name
                                else
                                    adapterItem.fromInfo.address
                            }
                            txMinus -> {
                                if (!Strings.isNullOrEmpty(adapterItem.fromInfo.name))
                                    adapterItem.toInfo.name
                                else
                                    adapterItem.toInfo.address
                            }
                            else -> ""
                        }

                    )
                //유저송금
                else if (USER_TRANSFER == adapterItem.type)
                    return Item(
                        R.string.history_transfer,
                        amount,
                        remain,
                        when (txType) {
                            txPlus -> R.string.history_transfer_to
                            txMinus -> R.string.history_transfer_from
                            else -> 0
                        },
                        when (txType) {
                            txPlus -> adapterItem.fromInfo.name
                            txMinus -> adapterItem.toInfo.name
                            else -> ""
                        }
                    )
                //결제
                else if (PAYMENT == adapterItem.type)
                    return Item(
                        R.string.history_payment,
                        amount,
                        remain,
                        when (txType) {
                            txPlus -> R.string.history_pay_refunded
                            txMinus -> R.string.history_pay_complete
                            else -> 0
                        },
                        adapterItem.storeName
                    )
            } catch (e: Exception) {
            }

            return Item(R.string.history_error_msg, "", "", R.string.history_server_msg, "")
        }

    }

    data class Item(var category: Int, var used: String, var remain: String?, var title: Int, var message: String?) {
        val category_background_id: Int = R.drawable.background_category_blue

        init {
            when {
                message.isNullOrEmpty() -> this.message = ""
                message?.length!! > 20 -> this.message = message!!.substring(0, 20)
            }
        }
    }
}