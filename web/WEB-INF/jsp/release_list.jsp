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
            window.location.href = "delete_release?id=" + idRelease;
        });
    }
</script>

<c:if test="${empty releaseList}">
    No hay actualizaciones cargadas.
</c:if>

<c:if test="${not empty releaseList}">
    <table class="table table-hover result_list" width="100%">
        <thead>
        <tr>
            <th class="col-sm-10">Fecha</th>
            <th class="col-sm-2">&nbsp;</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${releaseList}" var="release">
            <tr>
                <td style="vertical-align: middle;">
                        ${release}
                </td>
                <td style="vertical-align: middle; text-align: right;">
                    <button id="btnDelete" type="button" class="btn btn-danger" onclick="fDeleteRelease('${release.id}')">
                        <span class="glyphicon glyphicon-remove-circle"></span>
                    </button>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</c:if>
