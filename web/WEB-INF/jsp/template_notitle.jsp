<!DOCTYPE html>
<html lang="en">
<head>
    <%@ include file="/WEB-INF/jsp/include.jsp"%>
    <title><tiles:insertAttribute name="title" ignore="true" defaultValue="title" /></title>
</head>
<body>
<div class="container">
    <tiles:insertAttribute name="navbar" />
    <div class="jumbotron" style="padding: 10px 30px;height:100%; min-height: 550px; background:transparent !important; border: 1px solid #e7e7e7;">
        <tiles:insertAttribute name="body" />
    </div>
</div>
<%@ include file="/WEB-INF/jsp/popup.jsp" %>
</body>
</html>
