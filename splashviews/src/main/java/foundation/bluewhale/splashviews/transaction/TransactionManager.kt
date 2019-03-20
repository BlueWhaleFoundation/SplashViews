package foundation.bluewhale.splashviews.transaction

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

        fun getItem(userId: String, adapterItem: DTransaction): Item {
            try {
                val isReceiver = userId == adapterItem.to!!
                val amount = (if (isReceiver) "+" else "-") + NumberTool.convert(adapterItem.amount)
                val remain =
                    NumberTool.convert(if (isReceiver) adapterItem.toInfo?.leftBp else adapterItem.fromInfo?.leftBp)
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
                        if (isReceiver) R.string.history_transfer_to else R.string.history_transfer_from,
                        if (isReceiver) adapterItem.fromInfo!!.name else adapterItem.toInfo!!.name
                    )
                //비회원 송금
                else if (FUSER_TRANSFER == adapterItem.type)
                    return Item(
                        R.string.history_fuser_transfer,
                        amount,
                        remain,
                        if (isReceiver) R.string.history_transfer_to else R.string.history_transfer_from,
                        if (isReceiver) adapterItem.fromInfo!!.name else adapterItem.toInfo!!.name
                    )
                //비회원 송금 취소
                else if (FUSER_BP_ROLLBACK == adapterItem.type)
                    return Item(
                        R.string.history_fuser_transfer_rollback,
                        amount,
                        remain,
                        if (isReceiver) R.string.history_transfer_to else R.string.history_transfer_from,
                        if (isReceiver) adapterItem.fromInfo!!.name else adapterItem.toInfo!!.name
                    )
                //결제
                else if (PAYMENT == adapterItem.type)
                    return Item(
                        R.string.history_payment,
                        amount,
                        remain,
                        if (isReceiver) R.string.history_pay_refunded else R.string.history_pay_complete,
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
                        if (isReceiver) R.string.history_payroll_to else R.string.history_payroll_from,
                        if (isReceiver) adapterItem.fromInfo!!.name else adapterItem.toInfo!!.name
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
                        adapterItem.toInfo!!.name!!
                    )

                //DEFAULT
            } catch (e: Exception) {
            }

            return Item(R.string.history_error_msg, "", "", R.string.history_server_msg, "")
        }

        fun getItem(userId: String, adapterItem: DBEPTransaction): Item {
            try {
                val isReceiver = userId == adapterItem.to
                val amount = (if (isReceiver) "+" else "-") + NumberTool.convert(adapterItem.tokenAmount)
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
                        if (isReceiver) R.string.history_transfer_to else R.string.history_transfer_from,
                        if (isReceiver) adapterItem.fromInfo.address else adapterItem.toInfo.address
                    )
                //유저송금
                else if (USER_TRANSFER == adapterItem.type)
                    return Item(
                        R.string.history_transfer,
                        amount,
                        remain,
                        if (isReceiver) R.string.history_transfer_to else R.string.history_transfer_from,
                        if (isReceiver) adapterItem.fromInfo.name else adapterItem.toInfo.name
                    )
                //결제
                else if (PAYMENT == adapterItem.type)
                    return Item(
                        R.string.history_payment,
                        amount,
                        remain,
                        if (isReceiver) R.string.history_pay_refunded else R.string.history_pay_complete,
                        adapterItem.storeName
                    )
            } catch (e: Exception) {
            }

            return Item(R.string.history_error_msg, "", "", R.string.history_server_msg, "")
        }

    }

    data class Item(var category: Int, var used: String, var remain: String, var title: Int, var message: String?) {
        val category_background_id: Int = R.drawable.background_category_blue

        init {
            when {
                message.isNullOrEmpty() -> this.message = ""
                message?.length!! > 20 -> this.message = message!!.substring(0, 20)
            }
        }
    }
}