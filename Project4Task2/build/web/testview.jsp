<%-- 
    Document   : index
    Created on : Jan 23, 2019, 7:09:01 PM
    Author     : ebuns
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Test Page</title>
</head>
    <style>
        body {
          background-color: linen;
        }

        p {
          color: maroon;
          margin-left: 40px;
        } 
    </style>
<body>
    <p><h1> Hello user! Welcome to my 1st Distributed Systems Project</h1>
        
    </p>
    <form action="getrecipe" method="GET">
        <label for="text">Enter main ingredient here: </label>
        <input type="text" name="queryString" style ="width:300px; height:40px;"/><br>
         <label for="text">Enter diet preference here: </label>
        <input type="text" name="diet" style ="width:300px; height:40px;"/><br>
        <label for="text">Enter exclude list here: </label>
        <input type="text" name="exclude" style ="width:300px; height:40px;"/><br>
        <label for="text">Enter device here here: </label>
        <input type="text" name="device" style ="width:300px; height:40px;"/><br>
        <label for="text">Enter time stamp here format (2019-04-09 11:18:29.487): </label>
        <input type="text" name="start" style ="width:300px; height:40px;"/><br>
        <input type="submit" value="Submit" />
    </form>
</body>
</html> 
