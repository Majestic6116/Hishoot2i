package org.illegaller.ratabb.hishoot2i;

import com.crashlytics.android.Crashlytics;

import org.illegaller.ratabb.hishoot2i.di.Modules;
import org.illegaller.ratabb.hishoot2i.di.ir.UserDeviceScreenHeight;
import org.illegaller.ratabb.hishoot2i.di.ir.UserDeviceScreenWidth;
import org.illegaller.ratabb.hishoot2i.model.pref.IntPreference;
import org.illegaller.ratabb.hishoot2i.ui.activity.ErrorActivity;
import org.illegaller.ratabb.hishoot2i.ui.activity.MainActivity;
import org.illegaller.ratabb.hishoot2i.utils.UILHelper;
import org.illegaller.ratabb.hishoot2i.utils.Utils;

import android.app.Application;
import android.content.Context;

import javax.inject.Inject;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import dagger.ObjectGraph;
import io.fabric.sdk.android.Fabric;

public class HishootApplication extends Application {
    @Inject @UserDeviceScreenWidth IntPreference userScreenWidthPref;
    @Inject @UserDeviceScreenHeight IntPreference userScreenHeightPref;
    private ObjectGraph appGraph;

    public static HishootApplication get(Context context) {
        return (HishootApplication) context.getApplicationContext();
    }

    @Override public void onCreate() {
        super.onCreate();
        buildAndInjectAppGraph();
        Utils.enableStrictMode();
        //START CustomActivityOnCrash
        CustomActivityOnCrash.setLaunchErrorActivityWhenInBackground(true);
        CustomActivityOnCrash.setRestartActivityClass(MainActivity.class);
        CustomActivityOnCrash.setErrorActivityClass(ErrorActivity.class);
        CustomActivityOnCrash.setShowErrorDetails(true);
        CustomActivityOnCrash.install(this);
        //END CustomActivityOnCrash

        //START Fabric
        if (!BuildConfig.DEBUG) {
            final Fabric fabric = new Fabric.Builder(this)
                    .kits(new Crashlytics())
                    .debuggable(false)
                    .build();
            Fabric.with(fabric);
        } //END Fabric

        //ImageLoader
        UILHelper.init(this, userScreenWidthPref.get(), userScreenHeightPref.get());
    }

    private void buildAndInjectAppGraph() {
        appGraph = ObjectGraph.create(Modules.list(this));
        appGraph.inject(this);
    }

    public void inject(Object object) {
        appGraph.inject(object);
    }


}
