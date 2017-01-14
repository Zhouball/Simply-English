package com.c0xif.simplyenglish;

import java.util.ArrayList;
import java.io.*;
import java.net.*;

public class ThesaurusAPI {

    final String intermediateKey = "4c87d5ff-0704-4fcc-9ffb-578ef8ac378c";
    final String intermediateURL = "http://www.dictionaryapi.com/api/v1/references/ithesaurus/xml/";
    final String collegiateKey = "b1ddcb1e-2725-46d4-9091-565d5ff9fc89";
    final String collegiateURL = "http://www.dictionaryapi.com/api/v1/references/thesaurus/xml/";

    String key;
    String url;

    public ThesaurusAPI(){
        key = intermediateKey;
        url = intermediateURL;
    }

    public ThesaurusAPI(boolean intermediate){
        if (intermediate) {
            key = intermediateKey;
            url = intermediateURL;
        }
        else{
            key = collegiateKey;
            url = collegiateURL;
        }
    }

    public ArrayList<String> fetchSynonyms(String word)throws Exception{
        return parseXML(getXML(word));
    }

    private String getXML(String word) throws Exception{
        StringBuilder result = new StringBuilder();
        String call = url + word.toLowerCase() + "?key=" + key;
        URL u = new URL(call);
        HttpURLConnection conn = (HttpURLConnection) u.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while((line = r.readLine()) != null){
            result.append(line);
        }
        r.close();
        return result.toString();
    }

    private ArrayList<String> parseXML(String xml){
        // No reason to involve complicated libraries. Little dirty, but far more efficient.
        String syn = xml.split("<syn>")[1];
        syn = syn.split("</syn>")[0];
        String[] synonyms = syn.split(" ");

        String rel = xml.split("<rel>")[1];
        rel = rel.split("</rel>")[0];
        String[] relateds = rel.split(" ");

        ArrayList<String> result = new ArrayList<String>();
        for(String s : synonyms){
            result.add(s.replaceAll("[^A-Za-z]+", ""));
        }
        for(String r : relateds){
            result.add(r.replaceAll("[^A-Za-z]+", ""));
        }

        return result;
    }

}
