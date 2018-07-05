<%@include file="taglibs.jsp"%>

<script>
    $(document).ready(function() {
        $('#btnBack').on('click', function (event) {
            event.preventDefault();
            window.location.href = "/";
        });
    });

    function fSendNotification(){
        if ($("#body").val()<=0 ) {
            showDialogAlert("El body es obligatorio.")
        } else {
            const msgConfirm = "Se va a enviar la notiticación a todos los dispositivos, ¿desea continuar?";
            showDialogConfirm(msgConfirm, function () {
                var params = {
                    title: $("#title").val(),
                    body: $("#body").val()};
                peticionJqueryAjax("/notifications/doSend", params);
            });
        }
    }

    function peticionJqueryAjax (url, params) {
        console.log("asking url " + url);
        $.ajax({
            dataType: "text",
            url: url,
            data: params
        }).done((data, textStatus, jqXHR) => {
            showDialogAlert("Resultado de la acción: " + data, ()=>{
                window.location.href = "/notifications/send";
            });
            switchLoading(false);
        }).fail((jqXHR, textStatus, errorThrown) => {
            switchLoading(false);
            showDialogAlert("error resultado: " + textStatus);
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
    <div class="form-group row">
        <label class="control-label col-sm-2">Título</label>
        <div class="col-sm-6">
            <input id="title" placeholder="cool title" class="form-control" value="Deportes Madrid">
        </div>
    </div>
    <div class="form-group row">
        <label class="control-label col-sm-2">Cuerpo</label>
        <div class="col-sm-6">
            <textarea id="body" placeholder="cool body" rows="2" class="form-control"></textarea>
        </div>
    </div>
    <div class="form-group row">
        <label class="control-label col-sm-4">&nbsp;</label>
        <div class="col-sm-2">
            <button id="btnBack" type="button" class="btn btn-default btn-block">cancelar</button>
        </div>
        <div class="col-sm-2">
            <button class="btn btn-primary btn-block" onclick="fSendNotification();">enviar</button>
        </div>
        <div class="col-sm-4">&nbsp;</div>
    </div>
</div>