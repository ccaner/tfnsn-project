package com.tfnsnproject.util;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppConfiguration {

    private static AppConfiguration configuration=new AppConfiguration();

    private Properties props=new Properties();

    private Logger logger = Logger.getLogger(AppConfiguration.class.getName());

    private AppConfiguration() {
        try {
            props.load(this.getClass().getResourceAsStream("/app.properties"));
        } catch (Exception e) {
            logger.log(Level.SEVERE,"Unable to load configuration: "+e.getMessage(),e);
        }
    }

    public static final AppConfiguration getInstance () {
        return configuration;
    }

    public String getProperty (String propertyName) {
        return props.getProperty(propertyName);
    }

}
