package com.linkdump.tchur.ld.utils;

public class StringParser {

    public String rootString;


    private static final String OG_REGEX = "og:image|og:title|og:description|og:type|og:url|og:video";


    public StringParser(String rootString){
        this.rootString = rootString;
    }


    public static StringParser build(String rootString){
       return new StringParser(rootString);
    }




}
