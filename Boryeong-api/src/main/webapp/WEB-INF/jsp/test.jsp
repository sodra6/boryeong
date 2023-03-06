<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>테스트 페이지</title>
<link rel="shortcut icon" href="#">
</head>
<body>
    <div id="testDiv">
        <button type="button" id="testBtn" onclick="test()">눌러!</button>
        <button type="button" id="testGetSettingBtn" onclick="getSetting()">세팅 값 가져와</button>
        <button type="button" id="testPopBtn" onclick="testPopup()">popup Data</button>
        <button type="button" id="testSaveBtn" onclick="testSave()">저장~!</button>
        <button type="button" id="testReportBtn" onclick="testReport()">리포트</button>
        <button type="button" id="testExcelBtn" onclick="tableToExcel()">엑셀</button>
	</div>
</body>
<script src="js/lib/jquery-3.4.1.min.js"></script>
<script type="text/javascript">
	function test(){
		$.ajax({
			url :  "/api/dashBoard/main",
			type : "POST",
			datatype : "JSON",
			async : true,
			success : function(result){				
				console.log(result);
				alert("성공");
			},
			error : function(request, error){
				console.log(request);
				console.log(error);
				//if (request.status == 401) { alert("세션이 종료되었습니다.(세션유지시간 : 30분)"); window.location.href = "/login/actionLogout.do"; return; }
				alert("로딩 중 오류가 발생했습니다.\n message: " + request.responseText + ", error:" + error);
			}
		});
	
	}

    var settingData = null;

    function getSetting() {
        var param = {
            type : "DEPTH"
        };

        $.ajax({
            			url :  "/api/settings",
            			type : "GET",
            			data : param,
            			//async : true,
            			success : function(result){
            				console.log(result);
            				settingData = result;
            				alert("성공");
            			},
            			error : function(request, error){
            				console.log(request);
            				console.log(error);
            				//if (request.status == 401) { alert("세션이 종료되었습니다.(세션유지시간 : 30분)"); window.location.href = "/login/actionLogout.do"; return; }
            				alert("로딩 중 오류가 발생했습니다.\n message: " + request.responseText + ", error:" + error);
            			}
            		});
    }

	function testPopup(){
	        var date = new Date();
	        date.setHours(date.getHours() + 9);
	        var today = new Date();
	        today.setHours(date.getHours() + 9);
	        date.setDate(date.getDate() - 10);
	        var param =  {};
	        //param["kind"] = "TELE";
	        param["kind"] = "DEPTH";
	        param["direction"] = "A";
	        param["fromDate"] = date.toISOString().replace('T', ' ').substring(0, 19);
	        param["toDate"] = today.toISOString().replace('T', ' ').substring(0, 19);
	        console.log(param);
    		$.ajax({
    			url :  "/api/dashboard/popup",
    			type : "GET",
    			data : param,
    			//async : true,
    			success : function(result){
    				console.log(result);
    				alert("성공");
    			},
    			error : function(request, error){
    				console.log(request);
    				console.log(error);
    				//if (request.status == 401) { alert("세션이 종료되었습니다.(세션유지시간 : 30분)"); window.location.href = "/login/actionLogout.do"; return; }
    				alert("로딩 중 오류가 발생했습니다.\n message: " + request.responseText + ", error:" + error);
    			}
    		});

    	}

    	function testSave() {
    	    /*var param = {
    	        id : "PRESS_B5",
    	        warningVal : 20
    	    };*/
    	    settingData[3].warningVal = 20;
    	    var param = settingData[3];

    	    $.ajax({
                    url :  "/api/saveSetting",
                    type : "POST",
                    data : JSON.stringify(param),
                    dataType : 'json',
                    contentType : "application/json;charset=UTF-8",
                    //async : true,
                    success : function(result){
                        alert(result.msg);
                    },
                    error : function(request, error){
                        console.log(request);
                        console.log(error);
                        //if (request.status == 401) { alert("세션이 종료되었습니다.(세션유지시간 : 30분)"); window.location.href = "/login/actionLogout.do"; return; }
                        alert("로딩 중 오류가 발생했습니다.\n message: " + request.responseText + ", error:" + error);
                    }
                });
    	}

    	function testReport() {
    	    var param = { date : '2021-11-20' };
    	    $.ajax({
                    url :  "/api/report",
                    type : "GET",
                    data : param,
                    //async : true,
                    success : function(result){
                        console.log(result);
                    },
                    error : function(request, error){
                        console.log(request);
                        console.log(error);
                        //if (request.status == 401) { alert("세션이 종료되었습니다.(세션유지시간 : 30분)"); window.location.href = "/login/actionLogout.do"; return; }
                        alert("로딩 중 오류가 발생했습니다.\n message: " + request.responseText + ", error:" + error);
                    }
            });
    	}
/*
    	function tableToExcel() {
    	    let file = new Blob([$('#testDiv').html()], {type:"application/vnd.ms-excel"});
            let url = URL.createObjectURL(file);
            let a = $("<a />", {
                href: url,
                download: "filename.xls"}).appendTo("body").get(0).click();
                e.preventDefault();
          }
*/
        function fnExcelDownload() {
        		    var tab_text = '<html xmlns:x="urn:schemas-microsoft-com:office:excel">';
        		    tab_text += '<head><meta http-equiv="content-type" content="application/vnd.ms-excel; charset=UTF-8">';
        		    tab_text += '<xml><x:ExcelWorkbook><x:ExcelWorksheets><x:ExcelWorksheet>'
        		    tab_text += '<x:Name>Sheet</x:Name>';
        		    tab_text += '<x:WorksheetOptions><x:Panes></x:Panes></x:WorksheetOptions></x:ExcelWorksheet>';
        		    tab_text += '</x:ExcelWorksheets></x:ExcelWorkbook></xml></head><body>';
        		    tab_text += "<table border='1px'>";
        		    var exportTable = $('#testDiv').clone();
        		    exportTable.find('input').each(function (index, elem) { $(elem).remove(); });
        		    tab_text += exportTable.html();
        		    tab_text += '</table></body></html>';
        		    var data_type = 'data:application/vnd.ms-excel';
        		    var ua = window.navigator.userAgent;
        		    var msie = ua.indexOf("MSIE ");
        		    var fileName = "계절별 통계분석" + '.xls';

        		    // IE 환경에서 다운로드
        		    if (msie > 0 || !!navigator.userAgent.match(/Trident.*rv\:11\./)) {
        		    if (window.navigator.msSaveBlob) {
        		    var blob = new Blob([tab_text], {
        		    type: "application/csv;charset=utf-8;"
        		    });
        		    navigator.msSaveBlob(blob, fileName);
        		    }
        		    } else {
        		    var blob2 = new Blob([tab_text], {
        		    type: "application/csv;charset=utf-8;"
        		    });
        		    var filename = fileName;
        		    var elem = window.document.createElement('a');
        		    elem.href = window.URL.createObjectURL(blob2);
        		    elem.download = filename;
        		    document.body.appendChild(elem);
        		    elem.click();
        		    document.body.removeChild(elem);
        		    }

        		};
        </script>
</html>
