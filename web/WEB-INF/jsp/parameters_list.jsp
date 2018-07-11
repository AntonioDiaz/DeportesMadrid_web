<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="taglibs.jsp"%>
<script>
    $(document).ready(function() {

    });

    function fAdd() {
        window.location.href = "/parameters/add";
    }

    function fUpdate(id) {
        window.location.href = "/parameters/update?id=" + id;
    }


    function fDelete(id) {
        var bodyTxt = "¿Se va a borrar el parámetro?";
        showDialogConfirm(bodyTxt, function () {
            window.location.href = "/parameters/doDelete?id=" + id;
        })

    }
</script>
<div class="row">
    <div class="col-sm-10">&nbsp;</div>
    <div class="col-sm-2">
        <button type="button" class="btn btn-default" onclick="fAdd()" title="Añadir parametro">
            Nuevo Parámetro &nbsp; &nbsp;<span class="glyphicon glyphicon-plus"></span>
        </button>
    </div>
</div>
<hr>
<div class="row">
    <div class="col-sm-12">
        <table class="table table-hover">
            <thead>
            <tr>
                <th class="col-md-4">Clave</th>
                <th class="col-md-4">Valor</th>
                <th class="col-md-4">&nbsp;</th>
            </tr>
            </thead>
            <tbody>
                <c:if test="${empty parameters}">
                    <tr>
                        <td colspan="10">No hay parametros registradas.</td>
                    </tr>
                </c:if>
                <c:forEach var="parameter" items="${parameters}">
                    <tr>
                        <td style="vertical-align: top;">${parameter.key}</td>
                        <td style="vertical-align: middle;"><div style="word-break: break-all;">[${parameter.value}]</div></td>
                        <td align="right">
                            <button type="button" class="btn btn-default" onclick="fUpdate('${parameter.id}')" title="modificar">
                                Modificar &nbsp; &nbsp;<span class="glyphicon glyphicon-edit"></span>
                            </button>
                            <button type="button" class="btn btn-default" onclick="fDelete('${parameter.id}')" title="eliminar">
                                Eliminar &nbsp; &nbsp;<span class="glyphicon glyphicon-remove"></span>
                            </button>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>