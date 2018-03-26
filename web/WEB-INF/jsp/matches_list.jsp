<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script>
    $(document).ready(function () {
        $("#temporada").prop('selectedIndex', 1);
        $("#temporada").trigger( "change" );
        setTimeout(function(){
            $("#competicion").prop('selectedIndex', 1);
            $("#competicion").trigger("change");
            setTimeout(function(){
                $("#fase").prop('selectedIndex', 1);
                $("#fase").trigger("change");
                setTimeout(function(){
                    $("#grupo").prop('selectedIndex', 15);
                }, 1000);
            }, 1000);
        }, 1000);


    });

    function fCountMatches() {
        switchLoading(true);
        peticionJqueryAjax("count", "", function (data) {
            $("#matches_count").text(" - ");
            if (data!=null) {
                $("#matches_count").text(data);
            }
            switchLoading(false);
        });
    }

    function fCountClassificationEntries(){
        switchLoading(true);
        peticionJqueryAjax("countClassifications", "", function (data) {
            $("#classification_count").text(" - ");
            if (data!=null) {
                $("#classification_count").text(data);
            }
            switchLoading(false);
        });
    }

    function peticionJqueryAjax (url, params, myCallBack) {
        $.ajax({
            dataType: "json",
            url: url,
            data: params
        }).done((data, textStatus, jqXHR) => {
            myCallBack(data);
        }).fail((jqXHR, textStatus, errorThrown) => {
            myCallBack(null);
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

    function fFindClassification () {
        if ($("#temporada").prop('selectedIndex')==0
            || $("#competicion").prop('selectedIndex')==0
            || $("#grupo").prop('selectedIndex')==0
            || $("#fase").prop('selectedIndex')==0) {
            showDialogAlert("seleccione: temporada > competicion > fase > grupo")
        } else {
            var idCompeticion = $("#temporada").val();
            idCompeticion += "|" + $("#competicion").val();
            idCompeticion += "|" + $("#fase").val();
            idCompeticion += "|" + $("#grupo").val();
            var params = {cod_competicion: idCompeticion};
            peticionJqueryAjax("findClassification", params, function(classificationEntries) {
                $("#results").show();
                $("#results_count").text(classificationEntries.length);
                $("#result_list").empty();
                let resultList = $("#result_list").append("<ul class=\"list-group\">");
                classificationEntries.forEach((entry, index) => {
                    if (index==0) {
                        let competicion = "<br>temporada: " + entry.competition.nombreTemporada;
                        competicion += " <br>competición: " + entry.competition.nombreCompeticion;
                        competicion += " <br>fase: " + entry.competition.nombreFase;
                        competicion += " <br>grupo: " + entry.competition.nombreGrupo;
                        competicion += " <br>deporte: " + entry.competition.deporte;
                        competicion += " <br>distrito: " + entry.competition.distrito;
                        $("#results_count").append(competicion);
                        let divRow = $('<div/>', {class: 'row'});
                        divRow.append($('<div/>', {class:'col-sm-1', html: "posición"}));
                        divRow.append($('<div/>', {class:'col-sm-3', html: "equipo"}));
                        divRow.append($('<div/>', {class:'col-sm-1', html: "pj"}));
                        divRow.append($('<div/>', {class:'col-sm-1', html: "pg"}));
                        divRow.append($('<div/>', {class:'col-sm-1', html: "pe"}));
                        divRow.append($('<div/>', {class:'col-sm-1', html: "pp"}));
                        divRow.append($('<div/>', {class:'col-sm-1', html: "gf"}));
                        divRow.append($('<div/>', {class:'col-sm-1', html: "gc"}));
                        divRow.append($('<div/>', {class:'col-sm-1', html: "puntos"}));
                        resultList.append($('<li>', {class: 'list-group-item', html:divRow}));
                    }
                    let divRow = $('<div/>', {class: 'row'});
                    divRow.append($('<div/>', {class:'col-sm-1', html: entry.position}));
                    divRow.append($('<div/>', {class:'col-sm-3', html: entry.team!=null ? entry.team.name : " - "}));
                    divRow.append($('<div/>', {class:'col-sm-1', html: entry.matchesPlayed}));
                    divRow.append($('<div/>', {class:'col-sm-1', html: entry.matchesWon}));
                    divRow.append($('<div/>', {class:'col-sm-1', html: entry.matchesDrawn}));
                    divRow.append($('<div/>', {class:'col-sm-1', html: entry.matchesLost}));
                    divRow.append($('<div/>', {class:'col-sm-1', html: entry.pointsFavor}));
                    divRow.append($('<div/>', {class:'col-sm-1', html: entry.pointsAgainst}));
                    divRow.append($('<div/>', {class:'col-sm-1', html: entry.points}));
                    resultList.append($('<li>', {class: 'list-group-item', html:divRow}));
                });
                switchLoading(false);
            });
        }
    }

    function fFindMatches() {
        if ($("#temporada").prop('selectedIndex')==0
                || $("#competicion").prop('selectedIndex')==0
                || $("#grupo").prop('selectedIndex')==0
                || $("#fase").prop('selectedIndex')==0) {
            showDialogAlert("seleccione: temporada > competicion > fase > grupo")
        } else {
            var idCompeticion = $("#temporada").val();
            idCompeticion += "|" + $("#competicion").val();
            idCompeticion += "|" + $("#fase").val();
            idCompeticion += "|" + $("#grupo").val();
            var params = {cod_competicion: idCompeticion};
            peticionJqueryAjax("findMatches", params, function(matchesFound) {
                $("#results").show();
                $("#results_count").text(matchesFound.length);
                $("#result_list").empty();
                let resultList = $("#result_list").append("<ul class=\"list-group\">");
                matchesFound.forEach((match, index) => {
                    if (index==0) {
                        let competicion = "<br>temporada: " + match.competition.nombreTemporada;
                        competicion += " <br>competición: " + match.competition.nombreCompeticion;
                        competicion += " <br>fase: " + match.competition.nombreFase;
                        competicion += " <br>grupo: " + match.competition.nombreGrupo;
                        competicion += " <br>deporte: " + match.competition.deporte;
                        competicion += " <br>distrito: " + match.competition.distrito;
                        $("#results_count").append(competicion);
                        let divRow = $('<div/>', {class: 'row'});
                        divRow.append($('<div/>', {class:'col-sm-1', html: "jornada"}));
                        divRow.append($('<div/>', {class:'col-sm-1', html: "partido"}));
                        divRow.append($('<div/>', {class:'col-sm-1', html: "estado"}));
                        divRow.append($('<div/>', {class:'col-sm-1', html: "fecha"}));
                        divRow.append($('<div/>', {class:'col-sm-2', html: "local"}));
                        divRow.append($('<div/>', {class:'col-sm-2', html: "visitante"}));
                        divRow.append($('<div/>', {class:'col-sm-1', html: "marc. loc."}));
                        divRow.append($('<div/>', {class:'col-sm-1', html: "marc. vis."}));
                        divRow.append($('<div/>', {class:'col-sm-1', html: "campo"}));
                        resultList.append($('<li>', {class: 'list-group-item', html:divRow}));
                    }

                    let divRow = $('<div/>', {class: 'row'});
                    divRow.append($('<div/>', {class:'col-sm-1', html: match.numWeek}));
                    divRow.append($('<div/>', {class:'col-sm-1', html: match.numMatch}));
                    divRow.append($('<div/>', {class:'col-sm-1', html: match.state}));
                    divRow.append($('<div/>', {class:'col-sm-1', html: moment(new Date(match.date)).format('DD/MM/YYYY HH:mm')}));
                    divRow.append($('<div/>', {class:'col-sm-2', html: match.teamLocal != null ? match.teamLocal.name : " - "}));
                    divRow.append($('<div/>', {class:'col-sm-2', html: match.teamVisitor != null ? match.teamVisitor.name : " - "}));
                    divRow.append($('<div/>', {class:'col-sm-1', html: match.scoreLocal}));
                    divRow.append($('<div/>', {class:'col-sm-1', html: match.scoreVisitor}));
                    divRow.append($('<div/>', {class:'col-sm-1', html: match.place!=null ? match.place.name : " - "}));
                    resultList.append($('<li>', {class: 'list-group-item', html:divRow}));
                });
                switchLoading(false);
            });
        }
    }
    
    function fUpdatedTemporada() {
        //disable and empty
        $('#competicion').prop('disabled', true);
        $('#grupo').prop('disabled', true);
        $('#fase').prop('disabled', true);
        $('#competicion').empty();
        $('#grupo').empty();
        $('#fase').empty();
        if ($("#temporada").prop('selectedIndex')>0) {
            $('#competicion').prop('disabled', false);
            var params = {cod_temporada: $("#temporada").val()};
            peticionJqueryAjax("competitions", params, function (data) {
                $('#competicion').append($('<option>', {
                    value: "",
                    text : ""
                }));
                data.forEach(competionLoop => {
                    $('#competicion').append($('<option>', {
                        value: competionLoop.codCompeticion,
                        text : competionLoop.codCompeticion
                    }));
                });
                switchLoading(false);
            });
        }
    }

    function fUpdatedCompeticion() {
        //disable and empty
        $('#grupo').prop('disabled', true);
        $('#fase').prop('disabled', true);
        $('#grupo').empty();
        $('#fase').empty();
        if ($("#competicion").prop('selectedIndex')>0) {
            $('#fase').prop('disabled', false);
            var params = {
                cod_temporada: $("#temporada").val(),
                cod_competicion: $("#competicion").val()};
            peticionJqueryAjax("fases", params, function (data) {
                $('#fase').append($('<option>', {
                    value: "",
                    text : ""
                }));
                data.forEach(competionLoop => {
                    $('#fase').append($('<option>', {
                        value: competionLoop.codFase,
                        text : competionLoop.codFase
                    }));
                });
                switchLoading(false);
            });
        }
    }
    function fUpdatedFase() {
        //disable and empty
        $('#grupo').prop('disabled', true);
        $('#grupo').empty();
        if ($("#fase").prop('selectedIndex')>0) {
            $('#grupo').prop('disabled', false);
            var params = {
                cod_temporada: $("#temporada").val(),
                cod_competicion: $("#competicion").val(),
                cod_fase: $("#fase").val()};
            peticionJqueryAjax("grupos", params, function (data) {
                $('#grupo').append($('<option>', {
                    value: "",
                    text : ""
                }));
                data.forEach(competionLoop => {
                    $('#grupo').append($('<option>', {
                        value: competionLoop.codGrupo,
                        text : competionLoop.codGrupo
                    }));
                });
                switchLoading(false);
            });
        }
    }


</script>
<div id="loader" style="display:none;"></div>
<div id="contentDiv">
    <div class="row">
        <div class="form-group">
            <label class="control-label col-sm-2" >Número de partidos</label>
            <div class="col-sm-2"><div class="bg-success text-white" id="matches_count">&nbsp;</div></div>
            <div class="col-sm-6">&nbsp;</div>
            <div class="col-sm-2">
                <button type="button" class="btn btn-primary" onclick="fCountMatches();">
                    <span class="glyphicon glyphicon-search"></span>
                </button>
            </div>
        </div>
    </div>
    <hr>
    <div class="row">
        <div class="form-group">
            <label class="control-label col-sm-2" >Número de classificaciones</label>
            <div class="col-sm-2"><div class="bg-success text-white" id="classification_count">&nbsp;</div></div>
            <div class="col-sm-6">&nbsp;</div>
            <div class="col-sm-2">
                <button type="button" class="btn btn-primary" onclick="fCountClassificationEntries();">
                    <span class="glyphicon glyphicon-search"></span>
                </button>
            </div>
        </div>
    </div>
    <hr>
    <div class="row">
        <div class="form-group">
            <label class="control-label col-sm-2" >Buscar partidos</label>
            <div class="col-sm-2">
                Temporada
                <br>
                <select class="form-control" id="temporada" onchange="fUpdatedTemporada();">
                    <option></option>
                    <c:forEach items="${temporadas}" var="temporada">
                        <option value="${temporada}">${temporada}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="col-sm-2">
                Competición
                <br>
                <select class="form-control" id="competicion" disabled onchange="fUpdatedCompeticion();">
                    <option></option>
                </select>
            </div>
            <div class="col-sm-2">
                Fase
                <br>
                <select class="form-control" id="fase" disabled onchange="fUpdatedFase();">
                    <option></option>
                </select>
            </div>
            <div class="col-sm-2">
                Grupo
                <br>
                <select class="form-control" id="grupo" disabled>
                    <option></option>
                </select>
            </div>
            <div class="col-sm-2">
                <br>
                <button type="button" class="btn btn-primary" onclick="fFindMatches();">
                    <span class="glyphicon glyphicon-search"></span> Par.
                </button>
                <button type="button" class="btn btn-success" onclick="fFindClassification();">
                    <span class="glyphicon glyphicon-search"></span> Cla.
                </button>
            </div>
        </div>
    </div>
    <div id="results" style="display: none">
        <br>
        <div class="row" >
            <div class="form-group">
                <label class="control-label col-sm-2" >Resultado busqueda</label>
                <div class="col-sm-10"><div id="results_count" class="bg-info text-white"></div></div>
            </div>
        </div>
        <div id="result_list" class="row result_list" >
        </div>
    </div>
</div>