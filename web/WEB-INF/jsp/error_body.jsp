<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="taglibs.jsp"%>
<script>
    $(document).ready(function() { });
</script>
<h3>Se ha producido un error, contacte con el administrador del sistema.</h3>
<br>
<button class="btn btn-default" data-toggle="collapse" data-target="#demo">Ver detalles</button>
<br><br>
<div id="demo" class="collapse">
    Failed URL: ${url}
    <br>
    <small>Exception:  ${exception.message}</small>
    <br>
    <small>
        <c:forEach items="${exception.stackTrace}" var="ste">
            ${ste}
        </c:forEach>
        <small/>
</div>
<br>