package foundation.bluewhale.splashviews.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import android.util.Log

class SmsReciever : BroadcastReceiver() {
    private val SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED"

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.e("BwSmsReciever", "intent: " + intent)
        if (intent?.getAction().equals(SMS_RECEIVED)) {
            val bundle = intent?.getExtras()
            if (bundle != null) {
                // get sms objects
                val pdus = bundle.get("pdus") as Array<Any>
                if (pdus.size == 0) {
                    return
                }
                // large message might be broken into many
                val messages = arrayOfNulls<SmsMessage>(pdus.size)
                val sb = StringBuilder()
                for (i in pdus.indices) {
                    messages[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                    sb.append(messages[i]?.getMessageBody())
                }
                val sender = messages[0]?.getOriginatingAddress()
                val message = sb.toString()

                Log.e("BwSmsReciever", "sender: " + sender)
                Log.e("BwSmsReciever", "message: " + message)
                if ("16001522".equals(sender)) {
                    var head = "인증번호 ["
                    val startIdx = message.indexOf(head)
                    val endIdx = message.indexOf("]를")
                    if (startIdx > -1
                            && startIdx + head.length < endIdx) {
                        var num = message.substring(startIdx + head.length, endIdx)
                        Log.e("BwSmsReciever", "num: " + num)

                        if (SMSMessenger.getRxBusSingleton().hasObservers()) {
                            SMSMessenger.getRxBusSingleton().send(num)
                        }
                    }
                }

            }
        }
    }
}