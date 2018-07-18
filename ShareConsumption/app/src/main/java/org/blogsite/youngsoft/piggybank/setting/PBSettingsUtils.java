package org.blogsite.youngsoft.piggybank.setting;

/**
 * Created by klee on 2018-02-28.
 */

public class PBSettingsUtils {
    private static PBSettingsUtils instance = null;

    private PBSettings settings = null;

    public static PBSettingsUtils getInstance(){
        if(instance==null){
            instance = new PBSettingsUtils();
        }
        return instance;
    }

    public static PBSettingsUtils getInstance(PBSettings settings){
        if(instance==null){
            instance = new PBSettingsUtils();
        }
        instance.setSettings(settings);
        return instance;
    }

    public boolean isDebug(){
        boolean debug = false;
        if(settings!=null){
            debug = settings.isDebug();
        }
        return debug;
    }

    public PBSettings getSettings() {
        return settings;
    }

    public void setSettings(PBSettings settings) {
        this.settings = settings;
    }
}
