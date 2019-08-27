/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;


public class StarterApplication extends Application {

  public static final String CHANNEL_1_ID = "channel1";

  @Override
  public void onCreate() {
    super.onCreate();

    // Enable Local Datastore.
    Parse.enableLocalDatastore(this);

    // Add your initialization code here
    Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
            .applicationId("af3aa7c463d2bd5b13890f1fb8d1bf2227bd92e4")
            .clientKey("34360942b7c806e24656b6fcd30cb2588f5e9619")
            .server("http://3.15.160.189/parse/")
            .build()

            //vfxcB6ymhqDN
    );


    ParseUser.enableAutomaticUser();
    ParseACL defaultACL = new ParseACL();
    defaultACL.setPublicReadAccess(true);
    defaultACL.setPublicWriteAccess(true);
    ParseACL.setDefaultACL(defaultACL, true);

    createNotificationChannels();

  }


  private void createNotificationChannels(){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel channel1 = new NotificationChannel(
              CHANNEL_1_ID,
              "channel 1",
              NotificationManager.IMPORTANCE_HIGH
      );

      channel1.setDescription("This is Channel1");

      NotificationManager manager = getSystemService(NotificationManager.class);
      manager.createNotificationChannel(channel1);

    }
  }



}
