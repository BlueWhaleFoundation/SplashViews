package foundation.bluewhale.splashviews.receiver;


import foundation.bluewhale.splashviews.rx.RxBus;

public class PermissionMessenger {
    private static RxBus _rxBus = null;

    public static RxBus getRxBusSingleton() {
        if (_rxBus == null) {
            _rxBus = new RxBus();
        }

        return _rxBus;
    }
}
