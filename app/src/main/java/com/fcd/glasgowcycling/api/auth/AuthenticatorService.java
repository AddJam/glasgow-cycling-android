package com.fcd.glasgowcycling.api.auth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AuthenticatorService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        CyclingAuthenticator authenticator = new CyclingAuthenticator(this);
        return authenticator.getIBinder();
    }
}