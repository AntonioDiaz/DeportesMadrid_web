<%@include file="taglibs.jsp" %>
<script>

    $(document).ready(function () {});

    function fBtnTask(){
        switchLoading(true);
        peticionJqueryAjax("enqueueTask", {});
    }

    function fBtnCreateReleaseMatches(){
        switchLoading(true);
        peticionJqueryAjax("createReleaseMatches", {});
    }

    function fBtnCreateReleaseClassification(){
        switchLoading(true);
        peticionJqueryAjax("createReleaseClassification", {});
    }

    function fBtnBucketMatches(idRelease){
        switchLoading(true);
        var params = {id_release: idRelease};
        peticionJqueryAjax("loadBucketMatches", params);
    }

    function fBtnBucketClassification(idRelease){
        switchLoading(true);
        var params = {id_release: idRelease};
        peticionJqueryAjax("loadBucketClassification", params);
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
            showDialogAlert("Resultado de la acción: " + data, ()=>{
                window.location.href = "/releases/check";
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
                <td>
                    <div class="row">
                        <div class="col-sm-4">Partidos: ${last_release_available_matches}</div>
                        <div class="col-sm-4">Classificaciones: ${last_release_available_classifications}</div>
                        <div class="col-sm-4">
                            &nbsp;
                        </div>
                    </div>
                </td>
            </tr>
            <tr>
                <td>Última actualización creada</td>
                <td>
                    <div class="row">
                        <div class="col-sm-4">
                            Partidos: ${lastReleaseMatches.id}
                            <br>
                            Líneas fichero ${lastReleaseMatches.lines}
                        </div>
                        <div class="col-sm-4">
                            Classificaciones: ${lastReleaseClassifications.id}
                            <br>
                            Líneas fichero ${lastReleaseClassifications.lines}
                        </div>
                        <div class="col-sm-4">
                            <button type="button" class="btn btn-success btn-block" onclick="fBtnTask()">Lanzar tarea</button>
                        </div>
                    </div>
                </td>
            </tr>
            <tr>
                <td>
                    Crear releaseMatches
                </td>
                <td>
                    <div class="row">
                        <div class="col-sm-8">
                            <c:if test="${lastReleaseMatches!=null}">
                                <div class="bg-success text-white">done</div>
                            </c:if>
                            <c:if test="${lastReleaseMatches==null}">
                                <div class="bg-danger text-white">pendiente</div>
                            </c:if>
                        </div>
                        <div class="col-sm-4">
                            <button type="button" class="btn btn-primary btn-block" onclick="fBtnCreateReleaseMatches()">Lanzar proceso</button>
                        </div>
                    </div>
                </td>
            </tr>
            <tr>
                <td>Copiar a GCS & actualiza líneas
                </td>
                <td>
                    <div class="row">
                        <div class="col-sm-8">
                            <c:if test="${lastReleaseMatches!=null && lastReleaseMatches.updatedBucket}">
                                <div class="bg-success text-white">done</div>
                            </c:if>
                            <c:if test="${lastReleaseMatches==null || !lastReleaseMatches.updatedBucket}">
                                <div class="bg-danger text-white">pendiente</div>
                            </c:if>
                        </div>
                        <div class="col-sm-4">
                            <button type="button" class="btn btn-primary btn-block" onclick="fBtnBucketMatches(${lastReleaseMatches.id})">Lanzar proceso</button>
                        </div>
                    </div>
                </td>
            </tr>
            <tr>
                <td>Actualizar Equipos</td>
                <td>
                    <div class="row">
                        <div class="col-sm-8">
                            <c:if test="${lastReleaseMatches!=null && lastReleaseMatches.updatedTeams}">
                                <div class="bg-success text-white">done (${lastReleaseMatches.linesTeams})</div>
                            </c:if>
                            <c:if test="${lastReleaseMatches==null || !lastReleaseMatches.updatedTeams}">
                                <div class="bg-danger text-white">pendiente (${lastReleaseMatches.linesTeams})</div>
                            </c:if>
                        </div>
                        <div class="col-sm-4">
                            <button type="button" class="btn btn-primary btn-block" onclick="fBtnTeams(${lastReleaseMatches.id})">Lanzar proceso</button>
                        </div>
                    </div>
                </td>
            </tr>
            <tr>
                <td>Actualizar Campos</td>
                <td>
                    <div class="row">
                        <div class="col-sm-8">
                            <c:if test="${lastReleaseMatches!=null && lastReleaseMatches.updatedPlaces}">
                                <div class="bg-success text-white">done (${lastReleaseMatches.linesPlaces})</div>
                            </c:if>
                            <c:if test="${lastReleaseMatches==null || !lastReleaseMatches.updatedPlaces}">
                                <div class="bg-danger text-white">pendiente (${lastReleaseMatches.linesPlaces})</div>
                            </c:if>
                        </div>
                        <div class="col-sm-4">
                            <button type="button" class="btn btn-primary btn-block" onclick="fBtnPlaces(${lastReleaseMatches.id})">Lanzar proceso</button>
                        </div>
                    </div>
                </td>
            </tr>
            <tr>
                <td>Actualizar Competiciones</td>
                <td>
                    <div class="row">
                        <div class="col-sm-8">
                            <c:if test="${lastReleaseMatches!=null && lastReleaseMatches.updatedCompetitions}">
                                <div class="bg-success text-white">done (${lastReleaseMatches.linesCompetitions})</div>
                            </c:if>
                            <c:if test="${lastReleaseMatches==null || !lastReleaseMatches.updatedCompetitions}">
                                <div class="bg-danger text-white">pendiente (${lastReleaseMatches.linesCompetitions})</div>
                            </c:if>
                        </div>
                        <div class="col-sm-4">
                            <button type="button" class="btn btn-primary btn-block" onclick="fBtnCompetitions(${lastReleaseMatches.id})">Lanzar proceso</button>
                        </div>
                    </div>
                </td>
            </tr>
            <tr>
                <td>Actualizar Partidos</td>
                <td>
                    <div class="row">
                        <div class="col-sm-8">
                            <c:if test="${lastReleaseMatches!=null && lastReleaseMatches.updatedMatches}">
                                <div class="bg-success text-white">done (${lastReleaseMatches.linesMatches})</div>
                            </c:if>
                            <c:if test="${lastReleaseMatches==null || !lastReleaseMatches.updatedMatches}">
                                <div class="bg-danger text-white">pendiente (${lastReleaseMatches.linesMatches})</div>
                            </c:if>
                        </div>
                        <div class="col-sm-4">
                            <button type="button" class="btn btn-primary btn-block" onclick="fBtnMatches(${lastReleaseMatches.id})">Lanzar proceso</button>
                        </div>
                    </div>
                </td>
            </tr>
            <tr>
                <td>
                    Crear releaseClassification
                </td>
                <td>
                    <div class="row">
                        <div class="col-sm-8">
                            <c:if test="${lastReleaseClassifications!=null}">
                                <div class="bg-success text-white">done</div>
                            </c:if>
                            <c:if test="${lastReleaseClassifications==null}">
                                <div class="bg-danger text-white">pendiente</div>
                            </c:if>
                        </div>
                        <div class="col-sm-4">
                            <button type="button" class="btn btn-primary btn-block" onclick="fBtnCreateReleaseClassification()">Lanzar proceso</button>
                        </div>
                    </div>
                </td>
            </tr>
            <tr>
                <td>Copiar a GCS & actualiza líneas
                </td>
                <td>
                    <div class="row">
                        <div class="col-sm-8">
                            <c:if test="${lastReleaseClassifications!=null && lastReleaseClassifications.updatedBucket}">
                                <div class="bg-success text-white">done</div>
                            </c:if>
                            <c:if test="${lastReleaseClassifications==null || !lastReleaseClassifications.updatedBucket}">
                                <div class="bg-danger text-white">pendiente</div>
                            </c:if>
                        </div>
                        <div class="col-sm-4">
                            <button type="button" class="btn btn-primary btn-block" onclick="fBtnBucketClassification(${lastReleaseClassifications.id})">Lanzar proceso</button>
                        </div>
                    </div>
                </td>
            </tr>
            <tr>
                <td>Actualizar clasificaciones</td>
                <td>
                    <div class="row">
                        <div class="col-sm-8">
                            <c:if test="${lastReleaseClassifications!=null && lastReleaseClassifications.updatedClassification}">
                                <div class="bg-success text-white">done (${lastReleaseClassifications.linesClassification})</div>
                            </c:if>
                            <c:if test="${lastReleaseClassifications==null || !lastReleaseClassifications.updatedClassification}">
                                <div class="bg-danger text-white">pendiente (${lastReleaseClassifications.linesClassification})</div>
                            </c:if>
                        </div>
                        <div class="col-sm-4">
                            <button type="button" class="btn btn-primary btn-block" onclick="fBtnClassification(${lastReleaseClassifications.id})">Lanzar proceso</button>
                        </div>
                    </div>
                </td>
            </tr>
        </tbody>
    </table>
    <br>
</div>