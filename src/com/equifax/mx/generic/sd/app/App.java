package com.equifax.mx.generic.sd.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

import org.apache.log4j.Logger;


public final class App {
    
    private final static Logger LOG = Logger.getLogger(App.class.getName());
    
    private static HashMap<String, LinkedList<String>> dp = new HashMap<String, LinkedList<String>>();
    private static HashMap<String, LinkedList<String>> dm = new HashMap<String, LinkedList<String>>();
    private static HashSet<String> allDep = new HashSet<String>();
    private static LinkedList<String> commands = new LinkedList<String>();
    private static HashSet<String> intalled = new HashSet<String>();
    
    private static void executingCommands() throws Exception {
        // TODO The thing
        LOG.debug("Hello SD");

     for (String string : commands) {
          String com[] =  string.split("\\s+"); 
         if(com[0].equalsIgnoreCase("INSTALL")) {
             LOG.info(com[0]+" "+com[1]);
             if(intalled.contains(com[1])) {
                 LOG.info("   "+com[1]+" is already installed");
             }else {
                 HashSet<String> lt = checkPmDep(dp, com[1]);
                 if(lt.size()!=0) {
                     for (String object : lt) {
                         if(!intalled.contains(object)) {
                             LOG.info("   Intalling "+object);
                             intalled.add(object);
                         }
                     }
                 }else {
                     LOG.info("   Intalling "+com[1]);
                     intalled.add(com[1]);
                 }
             }
             
         } else if(com[0].equalsIgnoreCase("REMOVE")) {
             LOG.info(com[0]+" "+com[1]);
             if(intalled.contains(com[1])) {
                 HashSet<String> lt = checkDmDep(dm, com[1]);
                 if(lt.size()==0) {
                     if(!allDep.contains(com[1])) {
                         intalled.remove(com[1]);
                         LOG.info("   Removing "+com[1]);
                     }else {
                         LOG.info("   "+com[1]+ " is still needed");
                     }
                 }else {
                     for (String object : lt) {
                         if(intalled.contains(object)) {
                             intalled.remove(object);
                             LOG.info("   Removing "+object);
                         }
                     }
                 }
             } else {
                 LOG.info("   "+com[1]+" is not installed");
             }  
         } else if (com[0].equalsIgnoreCase("LIST")) {
             LOG.info("LIST");
             for (String lst : intalled) {
                     LOG.info("   "+lst);
             }
         } else if (com[0].equalsIgnoreCase("END")) {
             LOG.info("END");
             System.exit(0);
         } else {
             LOG.info(com[0]+ "Unknow command");
         }
         
     }       
        
        
    }
    
    //Check Promotion Dependence
    public static HashSet<String> checkPmDep(HashMap<String, LinkedList<String>> dp, String rm) {
        Iterator<Entry<String, LinkedList<String>>> it = dp.entrySet().iterator();
        HashSet<String> add = new HashSet<String>();
        while (it.hasNext()) {
            Entry<String, LinkedList<String>> pair = it.next();            
            LinkedList<String> tmp = (LinkedList<String>) pair.getValue();
             if(tmp.contains(rm)) {
                 for (int i = tmp.indexOf(rm); i < tmp.size(); i++) {
                     add.add(tmp.get(i));
                 }
                 for (int i = tmp.indexOf(rm); i < tmp.size(); i++) {
                     tmp.remove(i);
                 }   
            };
        }
        return add;
    }
    
    //Check Demotion Dependence
    public static HashSet<String> checkDmDep(HashMap<String, LinkedList<String>> dp, String rm) {
        Iterator<Entry<String, LinkedList<String>>> it = dp.entrySet().iterator();
        HashSet<String> add = new HashSet<String>();
        LinkedList<String> tormv = new LinkedList<String>();
        while (it.hasNext()) {
            Entry<String, LinkedList<String>> pair = it.next();            
            LinkedList<String> tmp = (LinkedList<String>) pair.getValue();
            if(tmp.contains(rm)) {
                if(tmp.getFirst().equals(rm)) {
                    tormv = tmp;
                    break;
                }
            }
        }
        
        LinkedList<String> elm = new LinkedList<String>();
        for (String string : tormv) {
            elm.add(string);
        }
        
        boolean isRemv = true;
        for (String string : elm) {
            Iterator<Entry<String, LinkedList<String>>> itt = dp.entrySet().iterator();
            while (itt.hasNext()) {
                Entry<String, LinkedList<String>> pair = itt.next();            
                LinkedList<String> tmp = (LinkedList<String>) pair.getValue();
                if(tmp.contains(string)) {
                    if(tmp.getFirst().equals(string)) {
                        if(isRemovabel(dp, string)) {
                            add.add(string);
                        } else {
                            isRemv = false;
                            break;
                        }                   
                    }
                }
            }
            if(!isRemv) {
                break;
            }else {
                Iterator<Entry<String, LinkedList<String>>> itt2 = dp.entrySet().iterator();
                while (itt2.hasNext()) {
                    Entry<String, LinkedList<String>> pair = itt2.next();            
                    LinkedList<String> tmp = (LinkedList<String>) pair.getValue();
                    for (String st : add) {
                        tmp.remove(st);
                    }
                }
            }
        }
            
        return add;
    }
    
    public static boolean isRemovabel(HashMap<String, LinkedList<String>> dp, String ermv) {
        boolean isRemovable = true;
        Iterator<Entry<String, LinkedList<String>>> it = dp.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, LinkedList<String>> pair = it.next();            
            LinkedList<String> tmp = (LinkedList<String>) pair.getValue();
            if(tmp.contains(ermv)) {
                if(!tmp.getFirst().equals(ermv)) {
                    return false;
                }
            }
        }
        return isRemovable;
    }
    
    
    
    public static void readInput() throws IOException {
        File inFile = new File(Settings.INPUT_FILE);
        FileInputStream fr = null;
        if (inFile.exists()) {
            fr = new FileInputStream(inFile);
            BufferedReader r = new BufferedReader (new InputStreamReader(fr, Charset.forName("UTF-8"))); 
            String s; 
            while((s = r.readLine()) != null) {
                String com[] =  s.split("\\s+");
                if(com[0].equals("DEPEND")) {
                    LinkedList<String> p = new LinkedList<String>();
                    p.addAll(Arrays.asList(com));
                    p.remove(0);
                    dp.put("dep"+dp.size(),p);
                    
                    LinkedList<String> m = new LinkedList<String>();
                    m.addAll(Arrays.asList(com));
                    m.remove(0);
                    dm.put("dep"+dm.size(),m);
                    
                    allDep.addAll(Arrays.asList(com));
                    allDep.remove(com[0]);
                } else {
                    commands.add(s.trim());
                }
                
            }
            if(fr != null) {
                fr.close();
            }
        }
    }
    
    public static void main(String[] args) throws Exception {
        Settings.parseArgs(args);       
        try {
            readInput();
            executingCommands();
        } catch (Exception e) {
            LOG.error("Unexpected error", e);
        }
    }
    
}
