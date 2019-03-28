package foundation.bluewhale.splashviews.network;

public class ServerErrorState {
    boolean isProductionVersion;
    String errorMessage = null;
    int stringRes = 0;
    public ServerErrorState(){}

    public ServerErrorState(int stringRes) {
        this.stringRes = stringRes;
        errorMessage = "NO_INTERNET";
    }

    public ServerErrorState(boolean isProductionVersion, String errorMessage) {
        this.errorMessage = errorMessage;
        stringRes = BaseNetErrorHandler.makeErrorMessage(isProductionVersion, errorMessage);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getStringRes() {
        return stringRes;
    }

    public boolean isObsoleteVersion(){
        return "VERSION.DEPRECATED_VERSION".equals(errorMessage);
    }

    //Depreacated
    /*public boolean isNotAuthorizedUser(){
        return "AUTH.NOT_AUTHORIZED".equals(errorMessage)
                || "AUTH.VALIDATION_OUTDATED".equals(errorMessage)
                || "USER.INVALID_USER".equals(errorMessage);
    }*/
    public boolean isNotAuthorizedUser(){
        return "AUTHENTICATION_FAILED".equals(errorMessage);
    }

    public boolean hasNoRole(){
        return "UNAUTHORIZED_ROUTE".equals(errorMessage);
    }


    public boolean isInvalidPassword(){
        return "AUTH.INVALID_PASSWORD".equals(errorMessage);
    }
    public boolean hasInsufficientMoney(){
        return "USER.NOT_ENOUGH_MONEY".equals(errorMessage);
    }

    public boolean isExceededMaxPwdFail(){
        return "AUTH.EXCEED_MAX_PWD_FAIL".equals(errorMessage);
    }

    public boolean isNoUser(){
        return "USER.NO_USER".equals(errorMessage);
    }

    public boolean cannotAccessToBusiness(){
        return "UNAUTHORIZED_JWT_ROLE".equals(errorMessage);
    }


    public boolean cannotPurchase(){
        return "TICKET.NOT_ENOUGH_TICKETS".equals(errorMessage)
                || "TICKET.ALREADY_BOUGHT".equals(errorMessage)
                || "TICKET.ALREADY_REFUNDED_OR_UESD".equals(errorMessage)
                || "ORDER.EXPIRED_ORDER".equals(errorMessage);
    }

    public boolean notHaveQRCode(){
        return "QRCODE.NO_QRCODE".equals(errorMessage);
    }
}
