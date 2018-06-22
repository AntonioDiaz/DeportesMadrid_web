<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<form:hidden path="id"></form:hidden>
<div class="form-group">
    <label class="control-label col-sm-2">Nombre</label>
    <div class="col-sm-6">
        <form:input path="key" class="form-control"></form:input>
    </div>
    <label class="control-label col-sm-4" style="text-align: left;"><form:errors path="key" cssClass="text-danger" /></label>
</div>
<div class="form-group">
    <label class="control-label col-sm-2">Valor</label>
    <div class="col-sm-6">
        <form:textarea path="value" class="form-control"></form:textarea>
    </div>
    <label class="control-label col-sm-4" style="text-align: left;"><form:errors path="value" cssClass="text-danger" /></label>
</div>
