<script>
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