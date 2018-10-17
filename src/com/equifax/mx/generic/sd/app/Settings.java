package com.equifax.mx.generic.sd.app;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.PropertyConfigurator;

import com.avon.mx.digital.common.settings.Property;
import com.avon.mx.digital.common.settings.PropertyFileReflector;

public final class Settings {
    
    @Property(key="env.name")
    public static String ENV_NAME;
    
    @Property(key="input.file")
    public static String INPUT_FILE;
    
    @SuppressWarnings("static-access")
    public static synchronized void parseArgs(String args[]) throws Exception {
        // Parse the options.
        
        Options options = new Options();
        
        String propertiesKey = "properties";
        options.addOption(OptionBuilder.hasArg(true).isRequired().create(propertiesKey));
        
        CommandLine commandLine = null;
        
        try {
            CommandLineParser parser = new GnuParser();
            commandLine = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println("Could not parse the options.");
            throw e;
        }
        
        // Load the properties.
        
        Properties properties;
        
        try {
            String propertiesFilename = commandLine.getOptionValue(propertiesKey);
            properties = PropertyFileReflector.load(propertiesFilename, "UTF-8", Settings.class);
        } catch (IOException e) {
            System.err.println("Could not load the properties file.");
            throw e;
        }
        
        PropertyConfigurator.configure(properties);
    }
    
} // class
