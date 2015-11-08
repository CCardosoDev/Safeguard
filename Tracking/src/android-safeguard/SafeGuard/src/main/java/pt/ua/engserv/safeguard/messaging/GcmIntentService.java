package pt.ua.engserv.safeguard.messaging;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by luis on 10/21/13.
 */
public class GcmIntentService extends Service {
    public IBinder onBind(Intent intent) {
        return null;
    }
}
