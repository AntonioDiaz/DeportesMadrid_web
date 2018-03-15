<script>
    $(document).ready(function () {
        $("#teamName").on('keyup', function (e) {
            if (e.keyCode == 13) {
                fSearchTeam();
            }
        });
    });

    function fSearchTeam(){
        if ($("#teamName").val().length >= 3) {
            switchLoading(true);
            var params = {team_name: $("#teamName").val()};
            peticionJqueryAjax("search", params);
        } else {
            showDialogAlert("Son necesarios 3 caracteres para buscar.");
        }
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
            $("#result_list").append("<hr>");
            data.forEach(element => {
                $("#result_list").append($('<div>', {class: 'col-sm-3', html:element.id + " - " + element.name}))
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
            <label class="control-label col-sm-3" >Número total de equipos</label>
            <div class="col-sm-2"><div class="bg-success text-white">${teams_number}</div></div>
        </div>
    </div>
    <br>
    <div class="row">
        <div class="form-group">
            <label class="control-label col-sm-3" >Nombre de Equipo</label>
            <div class="col-sm-6">
                <input id="teamName" type="text" class="form-control"></input>
            </div>
            <div class="col-sm-2">
                <button id="btnSearchTeam" type="button" class="btn btn-primary" onclick="fSearchTeam();">
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
        <div id="result_list" class="row" >
        </div>
    </div>
</div>

