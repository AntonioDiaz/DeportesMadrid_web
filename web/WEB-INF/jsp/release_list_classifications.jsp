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
            window.location.href = "delete_release_classifications?id_release=" + idRelease;
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
        <th class="col-sm-1">Líneas</th>
        <th class="col-sm-2">procesos</th>
        <th class="col-sm-3">&nbsp;</th>
    </tr>
    </thead>
    <tbody>
        <c:forEach items="${releaseList}" var="releaseMatches">
            <tr>
                <td style="vertical-align: middle;">
                    ${releaseMatches.id} <br>
                </td>
                <td style="vertical-align: middle;">
                        ${releaseMatches.lines}</td>
                <td style="vertical-align: middle;">
                        Bucket: ${releaseMatches.updatedBucket}<br>
                        Classification: ${releaseMatches.updatedClassification}<br>
                </td>
                <td style="vertical-align: middle; text-align: right;">
                    <a class="btn btn-primary" href='${releaseMatches.publishUrl}' role="button"><span class="glyphicon glyphicon-download"></span> Descargar fichero </a>
                    <button id="btnDelete" type="button" class="btn btn-danger" onclick="fDeleteRelease(${releaseMatches.id})">
                        <span class="glyphicon glyphicon-remove-circle"></span>
                    </button>
                </td>
            </tr>
        </c:forEach>
    </tbody>
    </table>
</c:if>
