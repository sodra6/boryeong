package com.mindone.Boryeongapi.utils;
 
 public class CamelUtil
 {
   public static String convert2CamelCase(String underScore)
   {
     if ((underScore.indexOf(95) < 0) && (Character.isLowerCase(underScore.charAt(0))))
     {
       return underScore;
     }
     
     StringBuilder result = new StringBuilder();
     boolean nextUpper = false;
     
     int len = underScore.length();
 
     for (int i = 0; i < len; ++i) {
      char currentChar = underScore.charAt(i);
       if (currentChar == '_') {
         nextUpper = true;
       }
       else if (nextUpper) {
         result.append(Character.toUpperCase(currentChar));
         nextUpper = false;
       } else {
         result.append(Character.toLowerCase(currentChar));
       }
     }
 
     return result.toString();
   }
   
   public static String restoreCamelCase(String upperScore)
   {
     if ((Character.isUpperCase(upperScore.charAt(0))))
     {
    	 
         StringBuilder result = new StringBuilder("_");
         
         int len = upperScore.length();
     
         for (int i = 0; i < len; ++i) {
          char currentChar = upperScore.charAt(i);
           //if (currentChar == '_') {
           if ( Character.isUpperCase(currentChar)) {
             result.append(Character.toLowerCase(currentChar));
           }
        
         }
     
         return result.toString();
     }
     
     return upperScore;
    
   }
 }
