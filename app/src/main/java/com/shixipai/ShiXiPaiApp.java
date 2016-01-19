package com.shixipai;

import android.app.Application;
import android.content.Context;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;

/**
 * Created by xiepeng on 16/1/13.
 */
public class ShiXiPaiApp extends Application {
    private static Context sContext;

    private ObjectGraph objectGraph;

    @Override
    public void onCreate() {
        super.onCreate();

        objectGraph = ObjectGraph.create(getModules().toArray());
        objectGraph.inject(this);

        sContext = getApplicationContext();
    }

    public static Context getContext() {
        return sContext;
    }

    private List<Object> getModules() {
        return Arrays.<Object>asList(new AppModule(this));
    }

    public ObjectGraph createScopedGraph(Object... modules) {
        return objectGraph.plus(modules);
    }
}
