<%@include file="taglibs.jsp" %>
<script>

    $(document).ready(function () {});

    function fBtnCreate(){
        switchLoading(true);
        var params = {};
        peticionJqueryAjax("createRelease", params);
    }

    function fBtnBucket(idRelease){
        switchLoading(true);
        var params = {id_release: idRelease};
        peticionJqueryAjax("loadBucket", params);
    }

    function fBtnTeams(idRelease){
        switchLoading(true);
        var params = {id_release: idRelease};
        peticionJqueryAjax("loadTeams", params);
    }

    function fBtnPlaces(idRelease){
        switchLoading(true);
        var params = {id_release: idRelease};
        peticionJqueryAjax("loadPlaces", params);
    }

    function fBtnCompetitions(idRelease){
        switchLoading(true);
        var params = {id_release: idRelease};
        peticionJqueryAjax("loadCompetitions", params);
    }

    function fBtnMatches(idRelease){
        switchLoading(true);
        var params = {id_release: idRelease};
        peticionJqueryAjax("loadMatches", params);
    }

    function fBtnClassification(idRelease){
        switchLoading(true);
        var params = {id_release: idRelease};
        peticionJqueryAjax("loadClassifications", params);
    }

    function peticionJqueryAjax (url, params) {
        $.ajax({
            dataType: "text",
            url: url,
            data: params
        }).done((data, textStatus, jqXHR) => {
            console.log('La solicitud se ha completado correctamente.');
            console.log( data );
            showDialogAlert("done resultado: " + data, ()=>{
                window.location.href = "/release/check";
            });
            switchLoading(false);
        }).fail((jqXHR, textStatus, errorThrown) => {
            console.log("La solicitud a fallado: ${textStatus}");
            console.log("La solicitud a fallado: ${errorThrown}");
            switchLoading(false);
            showDialogAlert("error resultado: " + textStatus);
            //window.location.href = "/updates/check";
        });
    }

    function switchLoading(showLoading) {
        if (showLoading) {
            document.getElementById("loader").style.display = "block";
            document.getElementById("contentDiv").style.display = "none";
        } else {
            document.getElementById("contentDiv").style.display = "block";
            document.getElementById("loader").style.display = "none";
        }
    }

</script>
<div id="loader" style="display:none;">
</div>
<div id="contentDiv">
    <table class="table">
        <tbody>
            <tr>
                <td class="col-sm-3">Actualizacion disponible</td>
                <td>${last_release_available}</td>
            </tr>
            <tr>
                <td>Última actualización cargada</td>
                <td>${last_release_loaded.publishDateStr}</td>
            </tr>
            <tr>
                <td>Crear release
                </td>
                <td>
                    <c:if test="${need_create}">
                        <button id="btnBucket" type="button" class="btn btn-primary" onclick="fBtnCreate()">Lanzar proceso</button>
                    </c:if>
                    <c:if test="${not need_create}">
                        <div class="bg-success text-white">DONE</div>
                    </c:if>
                </td>
            </tr>
            <tr>
                <td>Copiar ficheros a GCS
                </td>
                <td>
                    <c:if test="${need_bucket}">
                        <button id="btnBucket" type="button" class="btn btn-primary" onclick="fBtnBucket(${last_release_loaded.id})">Lanzar proceso</button>
                    </c:if>
                    <c:if test="${is_last_release_loaded && last_release_loaded.updatedBucket}">
                        <div class="bg-success text-white">DONE</div>
                    </c:if>
                </td>
            </tr>
            <tr>
                <td>Actualizar Equipos</td>
                <td>
                    <c:if test="${need_teams}">
                        <button id="btnTeams" type="button" class="btn btn-primary" onclick="fBtnTeams(${last_release_loaded.id})">Lanzar proceso</button>
                    </c:if>
                    <c:if test="${is_last_release_loaded && last_release_loaded.updatedTeams}">
                        <div class="bg-success text-white">DONE</div>
                    </c:if>
                </td>
            </tr>
            <tr>
                <td>Actualizar Campos</td>
                <td>
                    <c:if test="${need_places}">
                        <button type="button" class="btn btn-primary" onclick="fBtnPlaces(${last_release_loaded.id})">Lanzar proceso</button>
                    </c:if>
                    <c:if test="${is_last_release_loaded && last_release_loaded.updatedPlaces}">
                        <div class="bg-success text-white">DONE</div>
                    </c:if>
                </td>
            </tr>
            <tr>
                <td>Actualizar Competiciones</td>
                <td>
                    <c:if test="${need_competitions}">
                        <button type="button" class="btn btn-primary" onclick="fBtnCompetitions(${last_release_loaded.id});">Lanzar proceso</button>
                    </c:if>
                    <c:if test="${is_last_release_loaded && last_release_loaded.updateCompetitions}">
                        <div class="bg-success text-white">DONE</div>
                    </c:if>
                </td>
            </tr>
            <tr>
                <td>Actualizar Partidos</td>
                <td>
                    <c:if test="${need_matches}">
                        <button type="button" class="btn btn-primary" onclick="fBtnMatches(${last_release_loaded.id})">Lanzar proceso</button>
                    </c:if>
                    <c:if test="${is_last_release_loaded && last_release_loaded.updatedMatches}">
                        <div class="bg-success text-white">DONE</div>
                    </c:if>
                </td>
            </tr>
            <tr>
                <td>Actualizar clasificaciones</td>
                <td>
                    <c:if test="${need_classification}">
                        <button type="button" class="btn btn-primary" onclick="fBtnClassification(${last_release_loaded.id})">Lanzar proceso</button>
                    </c:if>
                    <c:if test="${is_last_release_loaded && last_release_loaded.updatedClassification}">
                        <div class="bg-success text-white">DONE</div>
                    </c:if>
                </td>
            </tr>
        </tbody>
    </table>
    <br>
</div>