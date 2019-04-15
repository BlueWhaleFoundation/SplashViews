package foundation.bluewhale.splashviews.network;

import foundation.bluewhale.splashviews.R;

public class BaseNetErrorHandler {
    public static int makeErrorMessage(boolean isProductionVersion, String message) {
        switch (message) {
            case "VERSION.DEPRECATED_VERSION":
                return R.string.ERROR_VERSION_DEPRECATED_VERSION;

            case "USER.NO_USER":
                return R.string.ERROR_USER_NO_USER;
            case "AUTH.INVALID_PASSWORD":
                return 0/*R.string.ERROR_INVALID_PASSWORD*/;
            case "NATIONAL_VALUE.EXIST_TEL":
                return R.string.ERROR_USER_EXIST_TEL;

            case "PAYMENT.NOT_ENOUGH_MONEY":
                return R.string.ERROR_PAYMENT_NOT_ENOUGH_MONEY;

            case "BUSINESS.NO_BUSINESS":
                return R.string.ERROR_BUSINESS_NO_BUSINESS;

            case "TRANSFER.NO_PAYMET_AMOUNT":
                return R.string.ERROR_TRANSFER_NO_PAYMET_AMOUNT;
            case "RATING.UNEXPECTED_RATING_VALUE":
                return R.string.ERROR_RATING_UNEXPECTED_RATING_VALUE;
            case "RATING.ALREADY_RATED":
                return R.string.ERROR_ERROR_RATING_ALREADY_RATED;

            case "REGISTER.EXIST_TEL":
                return R.string.ERROR_REGISTER_EXIST_TEL;
            case "REGISTER.EXIST_EMAIL":
                return R.string.ERROR_REGISTER_EXIST_EMAIL;

            case "QRCODE.PRICE_IS_REQUIRED":
                return R.string.ERROR_QRCODE_PRICE_IS_REQUIRED;

            case "PAYMENT.NO_MATCHING_BUSINESS":
                return R.string.ERROR_PAYMENT_NO_MATCHING_BUSINESS;
            case "PAYMENT.NO_BUYER":
                return R.string.ERROR_PAYMENT_NO_BUYER;
            case "PAYMENT.INVALID_BUYER":
                return R.string.ERROR_PAYMENT_NO_BUYER;
            case "PAYMENT.NO_PAYMENT":
                return R.string.ERROR_PAYMENT_NO_PAYMENT;
            case "PAYMENT.NO_BUSINESS":
                return R.string.ERROR_PAYMENT_NO_BUSINESS;
            case "PAYMENT.NO_SELLER":
                return R.string.ERROR_PAYMENT_NO_SELLER;

            case "TRANSFER.NO_SENDER":
                return R.string.ERROR_TRANSFER_NO_SENDER;
            case "TRANSFER.NO_TO_USER":
                return R.string.ERROR_TRANSFER_NO_TO_USER;
            case "TRANSFER.NO_PAYMENT_AMOUNT":
                return R.string.ERROR_TRANSFER_NO_PAYMENT_AMOUNT;
            case "TRANSFER.NO_CURRENCY":
                return R.string.ERROR_TRANSFER_NO_CURRENCY;
            case "TRANSFER.NO_FROM_USER":
                return R.string.ERROR_TRANSFER_NO_FROM_USER;
            case "TRANSFER.NOT_REGISTERED_SENDER":
                return R.string.ERROR_TRANSFER_NOT_REGISTERED_SENDER;
            case "TRANSFER.NOT_REGISTERED_RECEIVER":
                return R.string.ERROR_TRANSFER_NOT_REGISTERED_RECEIVER;
            case "TRANSFER.FAIL":
                return R.string.ERROR_TRANSFER_FAIL;
            case "TRANSFER.ALREADY_REGISTERED":
                return R.string.ERROR_TRANSFER_ALREADY_REGISTERED;
            case "TRANSFER.TRANSACTION_MAX_FAIL":
                return R.string.ERROR_TRANSFER_TRANSACTION_MAX_FAIL;
            case "TRANSFER.TRANSFER_TO_SELF":
                return R.string.ERROR_TRANSFER_TRANSFER_TO_SELF;

            case "USER.NOT_ENOUGH_MONEY":
                return R.string.ERROR_USER_NOT_ENOUGH_MONEY;
            case "USER.UPDATE_USER_FAIL":
                return R.string.ERROR_USER_UPDATE_USER_FAIL;

            case "TICKET.NOT_ENOUGH_TICKETS":
                return R.string.ERROR_TICKET_NOT_ENOUGH_TICKETS;
            case "TICKET.ALREADY_BOUGHT":
                return R.string.ERROR_TICKET_ALREADY_BOUGHT;
            case "TICKET.ALREADY_REFUNDED_OR_UESD":
                return R.string.ERROR_TICKET_ALREADY_REFUNDED_OR_UESD;

            case "TRANSACTION.COMMIT_RETRY_MAXED":
                return R.string.ERROR_TRANSACTION_COMMIT_RETRY_MAXED;
            case "TRANSACTION.COMMIT_FAILED":
                return R.string.ERROR_TRANSACTION_COMMIT_FAILED;
            case "TRANSACTION.INVALID_INPUT":
                return R.string.ERROR_TRANSACTION_INVALID_INPUT;
            case "TRANSACTION.SAME_FROM_TO":
                return R.string.ERROR_TRANSACTION_SAME_FROM_TO;
            case "TRANSACTION.ALREADY_REFUNDED":
                return R.string.ERROR_TRANSACTION_ALREADY_REFUNDED;
            case "TRANSACTION.ZERO_AMOUNT":
                return R.string.ERROR_TRANSACTION_ZERO_AMOUNT;
            case "ORDER.ALREADY_BOUGHT_OR_REFUNDED":
            case "ORDER.EXPIRED_ORDER":
                return R.string.ERROR_ORDER_EXPIRED_ORDER;
            case "QRCODE.EXPIRED":
                return R.string.ERROR_QRCODE_EXPIRED;
            case "ORDER.WRONG_DISCOUNT_RATIO":
                return R.string.ERROR_ORDER_WRONG_DISCOUNT_RATIO;

            case "USER.SAME_PASSWORD":
                return R.string.ERROR_USER_SAME_PASSWORD;

            case "TOKEN.NOT_ENOUGH_TOKEN":
                return R.string.ERROR_TOKEN_NOT_ENOUGH_TOKEN;

            case "EXCHANGE.NOT_SUPPORTED":
                return R.string.ERROR_EXCHANGE_NOT_SUPPORTED;

            case "BEP.NO_WALLET":
                return R.string.ERROR_BEP_NO_WALLET;
            case "JWT.EXPIRED_TOKEN":
                return R.string.ERROR_JWT_EXPIRED_TOKEN;
            case "TRANSFER.NOT_REGISTERED_WALLET":
                return R.string.ERROR_TRANSFER_NOT_REGISTERED_WALLET;
            case "EXCHANGE.LIMIT_EXCEED":
                return R.string.ERROR_EXCHANGE_LIMIT_EXCEED;

            case "EXCHANGE.BWX_NOT_MULTIPLE_OF_1000":
                return R.string.ERROR_EXCHANGE_BWX_NOT_MULTIPLE_OF_1000;

            case "QRCODE.NO_QRCODE":
                return R.string.ERROR_NO_QR_CODE;

            case "PRODUCT.NO_PRODUCT":
                return R.string.ERROR_NO_PRODUCT;

            case "NICE.AUTH_FAILED":
                return R.string.ERROR_NICE_AUTH_FAILED;
            case "NICE.USED_TOKEN":
                return R.string.ERROR_NICE_USED_TOKEN;
            case "NICE.INVALID_TOKEN":
                return R.string.ERROR_NICE_INVALID_TOKEN;

            case "PRODUCT.REGISTERED_BARCODE":
                return R.string.ERROR_PRODUCT_REGISTERED_BARCODE;

            case "CPDAX.INVALID_REQUEST":
                return R.string.ERROR_CPDAX_INVALID_REQUEST;

            case "FEATURE_ON_PROGRESS":
                return R.string.ERROR_FEATURE_ON_PROGRESS;

            case "INTERNAL_SERVER_ERROR":
                return R.string.error_unhandled_message;

            case "EXTERNAL_SERVER_ERROR":
                return R.string.EXTERNAL_SERVER_ERROR;

            //Ignore "UNAUTHORIZED_RESOURCE_ACCESS" so that the universial error message will show up

            default: {
                if (!com.google.common.base.Strings.isNullOrEmpty(message) && message.contains("NICE.")) {
                    return getNiceErrorMessage(message);
                } else {
                    if (isProductionVersion)
                        return R.string.error_unhandled_message;
                    else
                        return 0;
                }

            }
        }
    }


    public static int getNiceErrorMessage(String message) {
        final String prefix = "NICE.";
        switch (message) {
            case prefix + "0000": return R.string.ERROR_NICE_0000;
            case prefix + "0001": return R.string.ERROR_NICE_0001;
            case prefix + "0003": return R.string.ERROR_NICE_0003;
            case prefix + "0010": return R.string.ERROR_NICE_0010;
            case prefix + "0012": return R.string.ERROR_NICE_0012;
            case prefix + "0013": return R.string.ERROR_NICE_0013;
            case prefix + "0014": return R.string.ERROR_NICE_0014;
            case prefix + "0015": return R.string.ERROR_NICE_0015;
            case prefix + "0016": return R.string.ERROR_NICE_0016;
            case prefix + "0017": return R.string.ERROR_NICE_0017;
            case prefix + "0018": return R.string.ERROR_NICE_0018;
            case prefix + "0019": return R.string.ERROR_NICE_0019;
            case prefix + "0020": return R.string.ERROR_NICE_0020;
            case prefix + "0021": return R.string.ERROR_NICE_0021;
            case prefix + "0022": return R.string.ERROR_NICE_0022;
            case prefix + "0023": return R.string.ERROR_NICE_0023;
            case prefix + "0031": return R.string.ERROR_NICE_0031;
            case prefix + "0035": return R.string.ERROR_NICE_0035;
            case prefix + "0040": return R.string.ERROR_NICE_0040;
            case prefix + "0041": return R.string.ERROR_NICE_0041;
            case prefix + "0050": return R.string.ERROR_NICE_0050;
            case prefix + "0052": return R.string.ERROR_NICE_0052;
            case prefix + "0070": return R.string.ERROR_NICE_0070;
            case prefix + "0071": return R.string.ERROR_NICE_0071;
            case prefix + "0072": return R.string.ERROR_NICE_0072;
            case prefix + "0073": return R.string.ERROR_NICE_0073;
            case prefix + "0074": return R.string.ERROR_NICE_0074;
            case prefix + "0075": return R.string.ERROR_NICE_0075;
            case prefix + "0076": return R.string.ERROR_NICE_0076;
            case prefix + "0078": return R.string.ERROR_NICE_0078;
            case prefix + "0079": return R.string.ERROR_NICE_0079;
            case prefix + "9097": return R.string.ERROR_NICE_9097;
            default:              return R.string.ERROR_NICE_else;
        }
    }
}
