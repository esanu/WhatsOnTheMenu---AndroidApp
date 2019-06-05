/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project4task2;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.BsonField;
import com.mongodb.client.model.Sorts;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;

/**
 *
 * @author ebuns
 */
public class Database {
    //creates database and collection    
    MongoClientURI uri = new MongoClientURI(
        "mongodb://esanu:password1234@cluster0-shard-00-00-kwvvx.mongodb.net:27017,cluster0-shard-00-01-kwvvx.mongodb.net:27017,cluster0-shard-00-02-kwvvx.mongodb.net:27017/test?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin&retryWrites=true");

    MongoClient mongoClient = new MongoClient(uri);
    MongoDatabase database = mongoClient.getDatabase("test");
    MongoCollection<Document> collection = database.getCollection("mycollection");
    
        
    protected void logData(String queryString, String diet, String excluded, String recipe, String recipeURL, String time, String calories, String begin, String end, String device){
        //suppresses db log
        //Source: Piazza post
        final LogManager lm = LogManager.getLogManager();
            for( final Enumeration<String> i = lm.getLoggerNames(); i.hasMoreElements(); ) {
                lm.getLogger( i.nextElement()).setLevel( Level.OFF );
            }        
//        String beginString = begin.replaceAll("+", " ");
        long latency = 0;
        try {           
            //converts string to timestamp to get difference between begin and end (latency)
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
            Date parseBegin = dateFormat.parse(begin);
            Date parseEnd = dateFormat.parse(end);
            Timestamp beginTmestamp = new java.sql.Timestamp(parseBegin.getTime());
            Timestamp endTimestamp = new java.sql.Timestamp(parseEnd.getTime());
            long beginLong = beginTmestamp.getTime();
            long endLong = endTimestamp.getTime();
            latency = endLong - beginLong;
        }catch (ParseException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
          
        //creates a document for the mongodb and appends all the data I want to log.
        Document doc = new Document("recipe_search", queryString)
                .append("exclude_ingredients",excluded)
                .append("diet", diet)
                .append("recipe", recipe)
                .append("recipe_link", recipeURL)
                .append("beginRequest", begin)
                .append("endRequest", end)
                .append("requestTime", latency)
                .append("device", device)
                .append("log_time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date().getTime()));
        //inserts the document into the database
        collection.insertOne(doc);   
    }
    
    protected List<Document> readDocsInDB(){
        MongoCursor<Document> cursor = collection.find().iterator();    //creates an iterator to loop through the database to read documents
        List<Document> records = new ArrayList<>();
        try {
            while (cursor.hasNext()) {    
                records.add(cursor.next());     //adds each document to a list of documents and returns the list
            } 
        } finally {
            cursor.close();
        } 
        return records;    
    }
    
    //source: https://www.youtube.com/watch?v=yJrjs5GK3sM&t=222s
    protected List<Document> getTopSearchTerms(int n){
        //uses the aggregate iterable class to aggregate the data in db to get the top "n" search terms
        AggregateIterable<Document> countSearch = collection.aggregate(Arrays.asList(
                Aggregates.group("$recipe_search", Accumulators.sum("count", 1)), Aggregates.sort(Sorts.descending("count")), Aggregates.limit(n)));       
        List<Document> topNSearch = new ArrayList<>();
        for(Document document: countSearch){    //loops through the iterable class and adds the results to a list.
            topNSearch.add(document);
        }
        return topNSearch;
    }
    
    protected List<Document> getTopPhoneModels(int n){
        AggregateIterable<Document> countSearch = 
                collection.aggregate(Arrays.asList(Aggregates.group("$device", Accumulators.sum("count", 1)), 
                        Aggregates.sort(Sorts.descending("count")), Aggregates.limit(n)));       
        List<Document> topNDevice = new ArrayList<>();
        for(Document document: countSearch){
            topNDevice.add(document);
        }
        return topNDevice;
    }
    
    //  Source: https://stackoverflow.com/questions/40307659/get-average-from-mongo-collection-using-aggrerate
    protected double getAverageLatency(){
        AggregateIterable<Document> aggregate = collection.aggregate(Arrays.asList(Aggregates.group("_id", new BsonField("avgLatency", new BsonDocument("$avg", new BsonString("$requestTime"))))));
        Document result = aggregate.first();
        return result.getDouble("avgLatency");
    }
}
