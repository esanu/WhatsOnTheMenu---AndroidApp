package project4task2;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bson.Document;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;


@WebServlet(name = "GetRecipe", urlPatterns = {"/getrecipe", "/dashboard"})
public class GetRecipe extends HttpServlet {
    Database db = null;
    Model model = null;

    @Override
    public void init(){
        db = new Database();
        model = new Model();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { 
        String jspView = "testview.jsp"; //variable to hold servlet view
        String urlPath = request.getServletPath();  //gets the servlet path to determine which view to show user
        System.out.println("URL: " + urlPath);
//        jspView = "testview.jsp";  //this was used for testing 
        String recipeQuery = request.getParameter("queryString");   //get the string user enters to search for recipe
        String excludeList = request.getParameter("exclude");   //get the string of excluded ingredients
        String dietPreference = request.getParameter("diet");   //get the diet preference
        String device = request.getParameter("device");     //get the device used
        String beginTime = request.getParameter("start");   //get the start timestamp
        
        if(recipeQuery != null){    //if a search parameter was entered by the user
            String apiUrl = model.createURLForAPICall(recipeQuery, dietPreference, excludeList);   //then create the URL to call the API - helper method used
            String apiResponse = model.makeAPIRequest(apiUrl);    //after creating the URL, call the API - helper method used
            String clientResponse = model.parseJsonData(apiResponse);     //parse the json data received and prepare response for android client
            if (apiResponse != null) {  // If things went well with the call to API
                response.setStatus(200);    //set the response status to 200
                jspView = "response.jsp";   //set the view to response.jsp
                request.setAttribute("response", clientResponse);   //pass the client repsonse to the view
                long endRequest = new Date().getTime(); //get the timestamp when this was completed
                String endTimestamp = String.valueOf(new Timestamp(endRequest));    //convert long timestamp to string
                String componentString = model.createLogComponents(clientResponse);     
                String [] components = componentString.split(";");  //get the components of the response to log in database
                System.out.println("Server response");
//                pass parametes to log in mongodb
                db.logData(recipeQuery, dietPreference, excludeList, components[0], components[1], components[2], components[3], beginTime, endTimestamp, device);
            } else {
                response.setStatus(401);    //if things didn't go well, then set the response status to 401
            }                 
        }
        
        //if the user wants to see the dashboard
        if(urlPath.equalsIgnoreCase("/dashboard")){
            jspView = "dashboard.jsp";  //show the dashboard view
            List<Document> dbOutput = db.readDocsInDB();    //get the items in the dashboard
            request.setAttribute("dashboard", dbOutput);  //display the items on the dashboard 
            
            //objects for the app analytics
            List<Document> topNSearch = db.getTopSearchTerms(3);       
            double avgLatency = db.getAverageLatency();
            List<Document> topNModel = db.getTopPhoneModels(3);
            
            //pass the analytics data to the dashboard.jsp view
            request.setAttribute("topSearchTerms", topNSearch);
            request.setAttribute("topDeviceModels", topNModel);
            request.setAttribute("avgLatency", avgLatency);
        }
        
        RequestDispatcher view = request.getRequestDispatcher(jspView);     //dispatcher to create a view
        view.forward(request, response);
    }   
}
