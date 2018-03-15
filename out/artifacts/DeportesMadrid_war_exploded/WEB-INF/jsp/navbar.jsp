<%@include file="taglibs.jsp"%>
<nav class="navbar navbar-default" style="margin-bottom: 10px; margin-top: 10px;">
    <div class="container-fluid">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse"
                    data-target="#navbar" aria-expanded="false" aria-controls="navbar">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand remark_production" href="/" style="padding-top: 11px;">
                <img src="/resources/images/logo_horizontal.png" class="img-rounded" width="100px">
            </a>
        </div>
        <tiles:insertAttribute name="menu_elements" />
    </div>
</nav>
