<script>
    $(document).ready(function () {
        $("#competitionName").on('keyup', function (e) {
            if (e.keyCode == 13) {
                fSearch();
            }
        })
    });

    function fSearch(){
        switchLoading(true);
        var params = {competition_name: $("#competitionName").val()};
        peticionJqueryAjax("search", params);
    }

    function peticionJqueryAjax (url, params) {
        $.ajax({
            dataType: "json",
            url: url,
            data: params
        }).done((data, textStatus, jqXHR) => {
            console.log('La solicitud se ha completado correctamente.');
            console.log( data );
            $("#results").show();
            $("#results_count").text(data.length);
            $("#result_list").empty();
            let resultList = $("#result_list").append("<ul class=\"list-group\">");
            let divRow = $('<div/>', {class: 'row'});
            divRow.append($('<div/>', {class:'col-sm-1', text: "id" }));
            divRow.append($('<div/>', {class:'col-sm-1', text: "temporada"}));
            divRow.append($('<div/>', {class:'col-sm-1', text: "competición"}));
            divRow.append($('<div/>', {class:'col-sm-1', text: "fase"}));
            divRow.append($('<div/>', {class:'col-sm-1', text: "grupo"}));
            divRow.append($('<div/>', {class:'col-sm-3', html: "nombre"}));
            divRow.append($('<div/>', {class:'col-sm-2', html: "deporte"}));
            divRow.append($('<div/>', {class:'col-sm-2', html: "distrito"}));
            resultList.append($('<li>', {class: 'list-group-item', html:divRow}));
            data.forEach(element => {
                let nombres = element.nombreTemporada;
                nombres += " <br> " + element.nombreCompeticion;
                nombres += " <br> " + element.nombreFase;
                nombres += " <br> " + element.nombreGrupo;
                let divRow = $('<div/>', {class: 'row'});
                divRow.append($('<div/>', {class:'col-sm-1', text: element.id }));
                divRow.append($('<div/>', {class:'col-sm-1', text: element.codTemporada}));
                divRow.append($('<div/>', {class:'col-sm-1', text: element.codCompeticion}));
                divRow.append($('<div/>', {class:'col-sm-1', text: element.codFase}));
                divRow.append($('<div/>', {class:'col-sm-1', text: element.codGrupo}));
                divRow.append($('<div/>', {class:'col-sm-3', html: nombres}));
                divRow.append($('<div/>', {class:'col-sm-2', html: element.deporte}));
                divRow.append($('<div/>', {class:'col-sm-2', html: element.distrito}));
                resultList.append($('<li>', {class: 'list-group-item', html:divRow}));
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
<div id="loader" style="display:none;"></div>
<div id="contentDiv">
    <div class="row">
        <div class="form-group">
            <label class="control-label col-sm-3" >Número total de competiciones</label>
            <div class="col-sm-2"><div class="bg-success text-white">${competitions_number}</div></div>
        </div>
    </div>
    <br>
    <div class="row">
        <div class="form-group">
            <label class="control-label col-sm-3">Nombre de Competición</label>
            <div class="col-sm-6">
                <input id="competitionName" class="form-control"></input>
            </div>
            <div class="col-sm-2">
                <button type="button" class="btn btn-primary" onclick="fSearch();">
                    <span class="glyphicon glyphicon-search"></span>
                </button>
            </div>
        </div>
    </div>
    <div id="results" style="display: none">
        <br>
        <div class="row" >
            <div class="form-group">
                <label class="control-label col-sm-3" >Resultado busqueda</label>
                <div class="col-sm-2"><div id="results_count" class="bg-info text-white"></div></div>
            </div>
        </div>
        <div id="result_list" class="row result_list" >
        </div>
    </div>
</div>