<%-- 
    Document   : analytics
    Created on : Apr 6, 2019, 5:05:50 AM
    Author     : ebuns
--%>

<%@page import="java.util.List"%>
<%@page import="org.bson.Document"%>
<%@page import="java.lang.String"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Application Log Dashboard</title>
         <style>
            table, th, td {
              border: 1px solid black;
              border-collapse: collapse;
            }
        </style>   
    </head>
    <body> 
        <p>Top 3 Search Terms: 
        <p> Search Term | Count</p>
            <%List<Document> topSearch= (List<Document>)request.getAttribute("topSearchTerms");
            for(Document record: topSearch){%> 
            <p><%=record.get("_id") %>:        <%=record.get("count")%>                 
            </p>  
          <%}%>
        </p> 
        
        <p>Top 3 Device Models: 
            <p> Search Term | Count</p>
            <%List<Document> topModels= (List<Document>)request.getAttribute("topDeviceModels");
            for(Document record: topModels){%> 
                <p> <%=record.get("_id") %> :      
                    <%=record.get("count")%>
                </p>  
          <%}%> 
        </p>  
   
        <p>Average Latency: <%=request.getAttribute("avgLatency")%> </p>
            
        <table>
            <th>Search</th>
            <th>Excluded Ingredients</th>
            <th>Recipe</th>
            <th>Recipe Link</th>
            <th>Request_Begin</th>
            <th>Request_completed</th>
            <th>Time_to_complete</th>
            <th>Device</th>
            <th>Log Time</th>
            
           <%List<Document> records= (List<Document>) request.getAttribute("dashboard");
            for(Document record: records){%>
                <tr>
                    <td> <%=record.get("recipe_search") %> </td>
                    <td> <%=record.get("exclude_ingredients") %> </td>
                    <td> <%=record.get("recipe") %> </td>
                    <td> <%=record.get("recipe_link") %> </td>
                    <td> <%=record.get("beginRequest") %> </td>
                    <td> <%=record.get("endRequest") %> </td>
                    <td> <%=record.get("requestTime") %> </td>
                    <td> <%=record.get("device") %> </td>
                    <td> <%=record.get("log_time") %> </td>
                </tr>
            <%}%> 
        </table>
        
    </body>
</html>
