package foundation.bluewhale.splashviews.transaction

import com.google.common.base.Strings
import foundation.bluewhale.splash.network.result.receiveData.DBEPTransaction
import foundation.bluewhale.splash.network.result.receiveData.DTransaction
import foundation.bluewhale.splashviews.R
import foundation.bluewhale.splashviews.util.NumberTool
import foundation.bluewhale.splashviews.util.TextCoverTool

class TransactionManager {
    companion object {
        private const val TRANSFER = "TRANSFER"
        private const val FUSER_TRANSFER = "FUSER_TRANSFER"
        private const val FUSER_BP_ROLLBACK = "FUSER_BP_ROLLBACK"
        private const val BONUS = "BONUS"
        private const val PAYMENT = "PAYMENT"
        private const val CHARGE = "CHARGE"
        private const val PAYROLL = "PAYROLL"
        private const val REWARD = "REWARD"
        private const val WITHDRAW = "WITHDRAW"
        private const val EXCHANGE = "EXCHANGE"
        private const val EXCHANGE_AND_PAY = "EXCHANGE_AND_PAY"
        private const val EXCHANGE_FOR_PAY = "EXCHANGE_FOR_PAY"

        private const val WALLET_TRANSFER = "WALLET_TRANSFER"
        private const val USER_TRANSFER = "USER_TRANSFER"

        val DEFAULT = "DEFAULT"

        private const val txNone = 0
        private const val txMinus = 1
        private const val txPlus = 2

        fun getTxType(userId: String, adapterItem: DTransaction): Int {
            return when (userId) {
                adapterItem.from -> txMinus
                adapterItem.to -> txPlus
                else -> txNone
            }
        }

        fun getTxType(userId: String, adapterItem: DBEPTransaction): Int {
            return when (userId) {
                adapterItem.from -> txMinus
                adapterItem.to -> txPlus
                else -> txNone
            }
        }

        fun getItem(userId: String, adapterItem: DTransaction, isBiz: Boolean): Item {
            try {
                val txType = getTxType(userId, adapterItem)
                val amount = when (txType) {
                    txPlus -> "+" + NumberTool.convert(adapterItem.amount)
                    txMinus -> "-" + NumberTool.convert(adapterItem.amount)
                    else -> "" + NumberTool.convert(adapterItem.amount)
                }
                val remain =
                //NumberTool.convert(if (isReceiver) adapterItem.toInfo?.leftBp else adapterItem.fromInfo?.leftBp)
                    when (txType) {
                        txPlus -> NumberTool.convert(adapterItem.toInfo?.leftBp)
                        txMinus -> NumberTool.convert(adapterItem.fromInfo?.leftBp)
                        else -> null
                    }
                when {
                    //보너스
                    BONUS == adapterItem.type -> return Item(
                        R.string.history_bonus,
                        amount,
                        remain,
                        R.string.history_bonus,
                        adapterItem.bonusInfo!!.storeName!!
                    )
                    //송금
                    TRANSFER == adapterItem.type -> return Item(
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
                    FUSER_TRANSFER == adapterItem.type -> return Item(
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
                    FUSER_BP_ROLLBACK == adapterItem.type -> return Item(
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
                    PAYMENT == adapterItem.type -> return Item(
                        R.string.history_payment,
                        amount,
                        remain,
                        when (txType) {
                            txPlus -> if (isBiz) R.string.history_pay_complete else R.string.history_pay_refunded
                            txMinus -> if (isBiz) R.string.history_pay_refunded else R.string.history_pay_complete
                            else -> 0
                        },
                        adapterItem.paymentInfo!!.itemName
                    )
                    //충전
                    CHARGE == adapterItem.type -> return Item(
                        R.string.history_charge,
                        amount,
                        remain,
                        R.string.history_charge_complete,
                        adapterItem.fromInfo!!.name!!
                    )
                    //급여
                    PAYROLL == adapterItem.type -> return Item(
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
                    //리워드
                    REWARD == adapterItem.type -> return Item(
                        R.string.history_bonus,
                        amount,
                        remain,
                        R.string.history_bonus,
                        adapterItem.bonusInfo!!.storeName!!
                    )
                    //출금
                    WITHDRAW == adapterItem.type -> {
                        val message: String? = if (adapterItem.withdrawInfo != null)
                            String.format(
                                "%s %s",
                                adapterItem.withdrawInfo.bankName,
                                adapterItem.withdrawInfo.bankAccount
                            )
                        else
                            adapterItem.fromInfo!!.name
                        return Item(R.string.history_withdraw, amount, remain, R.string.history_withdraw, message!!)
                    }
                    //환전
                    EXCHANGE == adapterItem.type -> return Item(
                        R.string.history_exchange,
                        amount,
                        remain,
                        R.string.history_exchange_complete,
                        adapterItem.toInfo!!.name!!
                    )
                    EXCHANGE_AND_PAY == adapterItem.type -> return Item(
                        R.string.history_pay_from_bep_tobp,
                        amount,
                        remain,
                        when (txType) {
                            txPlus -> if (isBiz) R.string.history_pay_complete else R.string.history_pay_refunded
                            txMinus -> if (isBiz) R.string.history_pay_refunded else R.string.history_pay_complete
                            else -> 0
                        },
                        if (isBiz) adapterItem.fromInfo!!.name!! else adapterItem.toInfo!!.name!!
                    )
                    EXCHANGE_FOR_PAY == adapterItem.type -> return Item(
                        R.string.history_pay_from_bep_tobp,
                        amount,
                        remain,
                        when (txType) {
                            txPlus -> if (isBiz) R.string.history_pay_complete else R.string.history_pay_refunded
                            txMinus -> if (isBiz) R.string.history_pay_refunded else R.string.history_pay_complete
                            else -> 0
                        },
                        if (isBiz) adapterItem.fromInfo!!.name!! else adapterItem.toInfo!!.name!!
                    )

                    //DEFAULT
                }

                //DEFAULT
            } catch (e: Exception) {
            }

            return Item(R.string.history_error_msg, "", "", R.string.history_server_msg, "")
        }

        fun getItem(userId: String, adapterItem: DBEPTransaction, isBiz: Boolean): Item {
            try {
                val txType = getTxType(userId, adapterItem)
                val amount = when (txType) {
                    txPlus -> "+" + NumberTool.convert(adapterItem.tokenAmount)
                    txMinus -> "-" + NumberTool.convert(adapterItem.tokenAmount)
                    else -> NumberTool.convert(adapterItem.tokenAmount)
                }

                val remain = NumberTool.convert(adapterItem.leftToken)

                when {
                    //환전
                    EXCHANGE == adapterItem.type -> return Item(
                        R.string.history_exchange,
                        amount,
                        remain,
                        R.string.history_exchange_complete,
                        adapterItem.toInfo.name
                    )
                    //지갑송금
                    WALLET_TRANSFER == adapterItem.type -> return Item(
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
                                    TextCoverTool.getCoveredText(adapterItem.fromInfo.address)
                            }
                            txMinus -> {
                                if (!Strings.isNullOrEmpty(adapterItem.fromInfo.name))
                                    adapterItem.toInfo.name
                                else
                                    TextCoverTool.getCoveredText(adapterItem.fromInfo.address)
                            }
                            else -> ""
                        }

                    )
                    //유저송금
                    USER_TRANSFER == adapterItem.type -> return Item(
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
                    PAYMENT == adapterItem.type -> return Item(
                        R.string.history_payment,
                        amount,
                        remain,
                        when (txType) {
                            txPlus -> if (isBiz) R.string.history_pay_complete else R.string.history_pay_refunded
                            txMinus -> if (isBiz) R.string.history_pay_refunded else R.string.history_pay_complete
                            else -> 0
                        },
                        adapterItem.storeName
                    )
                }
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