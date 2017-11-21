package io.github.carlorodriguez.alarmon;

import android.content.Context;
import android.content.SharedPreferences;

import com.parse.Parse;

import static io.github.carlorodriguez.alarmon.Constants.FIRST_RUN;
import static io.github.carlorodriguez.alarmon.Constants.NORMAL_RUN;
import static io.github.carlorodriguez.alarmon.Constants.UPGRADED_RUN;

/**
 * Created by Bradley on 06/07/2017.
 */

public class Application extends android.app.Application {

    private Context context = this;

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(getApplicationContext());
        Parse.initialize(new Parse.Configuration.Builder(context)
                .applicationId("81MA2q0yTryO7B3681naByyLPUaNFUrl6gEaB6TV")
                .clientKey("Hlr8sBXFiISc12l6LQsAR6gpUfOBNlT9VEP6ge6r")
                .server("https://pg-app-8x4vo4ggncazg2gdalcd7cgje0ylsa.scalabl.cloud/1/").build());

    }
}
