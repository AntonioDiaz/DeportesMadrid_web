<%@include file="taglibs.jsp"%>
<div id="navbar" class="navbar-collapse collapse">
    <ul class="nav navbar-nav">
        <sec:authorize access="hasRole('ROLE_ADMIN')">
            <li class="dropdown"><a class="dropdown-toggle" data-toggle="dropdown" href="#">Actualizaciones<span class="caret"></span></a>
                <ul class="dropdown-menu">
                    <li><a href="/releases/release_list">Lista actualizaciones</a></li>
                    <li><a href="/releases/check">Comprobar actualización</a></li>

                </ul>
            </li>
            <li class="dropdown"><a class="dropdown-toggle" data-toggle="dropdown" href="#">Entidades<span class="caret"></span></a>
                <ul class="dropdown-menu">
                    <li><a href="/teams/list">Equipos</a></li>
                    <li><a href="/places/list">Campos</a></li>
                    <li><a href="/groups/list">Grupos</a></li>
                    <li><a href="/groups/sports_list">Deportes</a></li>
                    <li><a href="/matches/list">Partidos</a></li>
                </ul>
            </li>
        </sec:authorize>
    </ul>
    <ul class="nav navbar-nav navbar-right">
        <li>
            <a href="<c:url value="j_spring_security_logout" />">
                <b>
                    <sec:authentication property="principal" var="userSession"></sec:authentication>
                    <c:out value="${userSession.username}"></c:out>&nbsp;
                    <c:if test="${!userSession.admin}">
                        (<c:out value="${userSession.townEntity.name}"></c:out>)&nbsp;
                    </c:if>
                    <c:if test="${userSession.admin}">
                        (admin)&nbsp;
                    </c:if>
                    <span class="glyphicon glyphicon-log-in"></span>
                </b>
            </a>
        </li>
    </ul>
</div>
