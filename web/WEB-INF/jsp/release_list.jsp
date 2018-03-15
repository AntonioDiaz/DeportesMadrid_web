<%@include file="taglibs.jsp" %>
<script>
    $(document).ready(function(){
        <c:if test="${delete_done}">
            showDialogAlert("Se ha borrado la actualización");
        </c:if>
    });

    function fDeleteRelease(idRelease){
        const msgConfirm = "se va a borrar actualización, ¿continuar?";
        showDialogConfirm(msgConfirm, function () {
            window.location.href = "delete?id_release=" + idRelease;
        });
    }

    function fDownloadRelease(urlRelease) {
        alert(urlRelease);
    }

</script>

<c:if test="${empty releaseList}">
    No hay actualizaciones cargadas.
</c:if>

<c:if test="${not empty releaseList}">
    <table class="table table-hover" width="100%">
    <thead>
    <tr>
        <th class="col-sm-1">Fecha</th>
        <th class="col-sm-2">procesos</th>
        <th class="col-sm-3">&nbsp;</th>
    </tr>
    </thead>
    <tbody>
        <c:forEach items="${releaseList}" var="release">
            <tr>
                <td style="vertical-align: middle;">${release.publishDateStr}</td>
                <td style="vertical-align: middle;">
                        Bucket: ${release.updatedBucket}<br>
                        Teams: ${release.updatedTeams}<br>
                        Places: ${release.updatedPlaces}<br>
                        Competitions: ${release.updateCompetitions}<br>
                        Matches: ${release.updatedMatches}<br>
                        Classification: ${release.updatedClassification}<br>

                </td>
                <td style="vertical-align: middle; text-align: right;">
                    <a class="btn btn-primary" href='${release.publishUrlClassification}' role="button"><span class="glyphicon glyphicon-download"></span> C </a>
                    <a class="btn btn-primary" href='${release.publishUrlMatches}' role="button"><span class="glyphicon glyphicon-download"></span> P </a>
                    <button id="btnDelete" type="button" class="btn btn-danger" onclick="fDeleteRelease(${release.id})">
                        <span class="glyphicon glyphicon-remove-circle"></span>
                    </button>
                </td>
            </tr>
        </c:forEach>
    </tbody>
    </table>
</c:if>
