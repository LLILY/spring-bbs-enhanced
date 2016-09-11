$(function(){


    var initConfigureData = function(){
        $.ajax({
            url:'/loadConfigure',
            data:{
                catatlog:'db'
            },
            success:function(json){
                var dbConfigure = eval('(' + json + ')');
                $("#connectUrl").val(dbConfigure["db.url"]);
                $("#driver").val(dbConfigure["db.driver"]);
                $("#userName").val(dbConfigure["db.username"]);
                $("#password").val(dbConfigure["db.password"]);
                $("#maxActive").val(dbConfigure["db.maxActive"]);
                $("#initialSize").val(dbConfigure["db.initialSize"]);
                $("#maxWait").val(dbConfigure["db.maxWait"]);
                $("#maxIdle").val(dbConfigure["db.maxIdle"]);
                $("#minIdle").val(dbConfigure["db.minIdle"]);
                $("#removeAbandoned").val(dbConfigure["db.removeAbandoned"]);
                $("#removeAbandonedTimeout").val(dbConfigure["db.removeAbandonedTimeout"]);
                $("#timeBetweenEvictionRunsMillis").val(dbConfigure["db.timeBetweenEvictionRunsMillis"]);
                $("#minEvictableIdleTimeMillis").val(dbConfigure["db.minEvictableIdleTimeMillis"]);
                $("#connectionProperties").val(dbConfigure["db.connectionProperties"]);

            }
        })
    }();


    $(".ibox").on('click','#submitBtn',function(){
        $("#form").submit();
    })



})