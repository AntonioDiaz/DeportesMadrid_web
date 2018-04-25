<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script>
    $(document).ready(function () {
    });
</script>
<div id="loader" style="display:none;"></div>
<div id="contentDiv">
    <c:forEach var="sport_count" items="${sports_list_count}">
        <div class="row">
            <label class="control-label col-sm-3">${sport_count.sportName}</label>
            <div class="col-sm-2"><div class="bg-success text-white">${sport_count.groupsCount}</div></div>
        </div>
    </c:forEach>
</div>