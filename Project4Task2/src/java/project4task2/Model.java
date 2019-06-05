/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project4task2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author ebuns
 */
public class Model {
    //helper method to call the API and return the API response (string in JSON format)
    protected String makeAPIRequest(String apiUrl){   
        int httpResponseCode = 0;
        String apiResponse = "";
        
        try {
            URL url = new URL(apiUrl);     //Create a url object
            HttpURLConnection conn = (HttpURLConnection) url.openConnection(); //create HTTP connection to the API
            conn.setRequestMethod("GET");       //set the http connection to get
            conn.setRequestProperty("Accept", "application/json");  // tell the server what format we want back
            httpResponseCode = conn.getResponseCode();    // wait for response
            
            if (httpResponseCode == 200) {  // If things went well
                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));     //create a bufferedreader object to read from the inputstream
                String str;
                while ((str = br.readLine()) != null) {
                    apiResponse += str;     //read the response into a string
                }   
            }
            conn.disconnect();  //afterwards, disconnect
        } catch(MalformedURLException e){
            System.out.println(e.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(GetRecipe.class.getName()).log(Level.SEVERE, null, ex);
        }
        return apiResponse;     //return the response
    }
    
    protected String createLogComponents(String response){   //helper method to log the components of the json response to the database
        String components = "";
        try {
       
            JSONParser parse =  new JSONParser();    //create parser object
            Object parseData = parse.parse(response);  //parse response string
            JSONObject json = (JSONObject) parseData;   //cast to JSONObject
            
            //get the appropiate components
            String meal = (String) json.get("meal");   
            String url = (String) json.get("url");
            double calories = (double) json.get("calories");
            String time = (String) json.get("time");
            //create a string made up of the components
            components = meal + ";" + url + ";" + time + ";" + calories;
        } catch (org.json.simple.parser.ParseException ex) {
            Logger.getLogger(GetRecipe.class.getName()).log(Level.SEVERE, null, ex);
        }
        return components;
    }
    
    //helper method to pick a random JSON object from recipe API to show the user at Android client
    protected String createResponseForApp(JSONArray response){     
        int random = new Random().nextInt(response.size());//get a random recipe
        JSONObject jsonObject = (JSONObject) response.get(random);
        
        String recipeDetails = jsonObject.toString();
        return recipeDetails;
    }
    
    protected String parseJsonData(String jsonData){
        String response = "";
        try {
            //get the hits and then get the recipe details
            JSONParser parse =  new JSONParser();    //create parser object
            Object parseData = parse.parse(jsonData);  //parse response string
            JSONObject json = (JSONObject) parseData;   //cast to JSONObject
            JSONArray jsonArray = (JSONArray) json.get("hits");     //creates a jsonarray of hits (the response from recipe api)
            JSONArray jsonArrayResponse = new JSONArray();

            if(jsonArray.size() > 0){   //if there are any hits, get the components of each hit that I need...
                for(int i = 0; i < jsonArray.size(); i++){
                    JSONObject jsonHit = (JSONObject) jsonArray.get(i);
                    JSONObject hit = (JSONObject) jsonHit.get("recipe");
                    String meal = (String) hit.get("label");
                    String url = (String) hit.get("url");
                    double calories = (double) hit.get("calories");
                    double time = (double) hit.get("totalTime");
                    String printTime = "unknown";
                    if(time != 0.0){
                        printTime = String.valueOf(time);
                    }
                 //...and parse each hit into the json object I want
                    JSONObject jsonResponse = new JSONObject();
                    jsonResponse.put("meal", meal);
                    jsonResponse.put("url", url);
                    jsonResponse.put("time", printTime);
                    jsonResponse.put("calories", calories);
                    jsonArrayResponse.add(jsonResponse);        //add to a jsonarry. a random one of these will be picked to show user
                }
            }
            
            response = createResponseForApp(jsonArrayResponse);     //calls the helper method that picks a random jsonobject and returns a string
        } catch (org.json.simple.parser.ParseException ex) {
            Logger.getLogger(GetRecipe.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }
    
    //helper method to create the link to call the API and return url
    protected String createURLForAPICall(String query, String dietPreference, String excludeList){
        String diet = "", excluded = "";
        
        if(dietPreference.equalsIgnoreCase("none")){       
            diet = "";                    
        } else {
            String temp = dietPreference.replaceAll(" ", "-");      //formats the dietprefence according to api requirements 
            diet = "&diet=" +temp.toLowerCase();    
        }
        
        if(excludeList != null){
            if(excludeList.contains(",")){  //if more than one ingredient is excluded, then split and format according to api requirements
                String [] temp2 = excludeList.split(",");
                for(String each: temp2){
                    excluded += "&excluded="+each.toLowerCase();        //otherwise, format according to api requirements 
                }
            } else {
                excluded = "&excluded="+excludeList.toLowerCase();  
            } 
        }
        
        String url = "https://api.edamam.com/search?q="+ query  //cocantenate the query string to the url
                + "&app_id=53456ef4&app_key=4e3adb2dba3d40fc2f2317c8e5f13e40"   //along with the api keys
                + "&from=0&to=5"
                + diet      //and diet preference
                + excluded;     //and excluded diet list
        return url;
    }
}
