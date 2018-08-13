$(function() {
	var date = new Date();
	var year = date.getFullYear();// 获取年份值
	// 设置年份值
	$('#year').combobox('select',year);
	
	$('#grid').datagrid({
		title:'销售退货趋势列表',
		url : 'report!returnorderstrendReport.action',
		singleSelect : true,
		queryParams:{year:year},
		columns : [ [ 
			{field : 'name',title : '月份',width : 100}, 
			{field : 'y',title : '销售退货金额',width : 100},
			{field : 'num',title:'退货订单数量',width: 100}
		] ],
		onLoadSuccess:function(data){
			//在数据加载成功的时候触发。
//			alert(JSON.stringify(data));
			showChart(data.rows);
			
		}
	});
	
	//点击查询按钮
	$('#btnSearch').bind('click',function(){
		//把表单数据转换成json对象
		var formData = $('#searchForm').serializeJSON();
		$('#grid').datagrid('reload',formData);
	});
	
	
});

function showChart(_data){
//	alert(JSON.stringify(_data));
	$.each(_data,function(i,d){
		d.drilldown=true;
	})
	var data1 = [];
	for(j= 0;j<12;j++){
		data1[j]=_data[j]
	}
	var data2 = [];
	for(k= 0;k<12;k++){
		data2[k]=_data[k+12]
	}
	var months = [];
	for(var i = 1; i <=12; i++){
		months.push(i + "月");
	}
	$('#chart').highcharts({
	    chart: {
	    	type: 'column',
            events: {
				drillup: function(e) {
					// 上钻回调事件
					console.log(e.seriesOptions);
				},
				drilldown: function (e) {
					
					if (!e.seriesOptions) {
						var chart = this;
					//alert(JSON.stringify(_data));
						
						var timeY=$('#year').combobox('getValue')
						alert(timeY+"-"+e.point.name);
						var returnTime= timeY+"-"+e.point.name;
						
						//chart.showLoading('正在努力加载中 ...');
						$.ajax({
							url : 'report!salesReturnTime.action',
							data : {returnTime:returnTime},
							dataType : 'json',
							type : 'post',
							success : function(rtn) {
								//var data = new Array();
								
								var obj = {};
								obj.name=e.point.name;
								//obj.id=e.point.name;
								obj.data=rtn;
								chart.hideLoading();
								
								chart.addSeriesAsDrilldown(e.point, obj);
							}
						});
						// Show the loading label
						//chart.showLoading('Simulating Ajax ...');
						//setTimeout(function () {
						//	chart.hideLoading();
						//	chart.addSeriesAsDrilldown(e.point, series);
						//}, 1000);
					}
				}
			}
        },
        title: {
            text: $('#year').combobox('getValue')+'年度销售退货趋势图'
        },
       
        credits: {
        	enabled: true,
        	href: "http://www.itheima.com",
        	text: "itheima.com"
        },
        subtitle: {
            text: 'TeamFour Skr'
        },
		 xAxis: {
	        	categories: months,
	            crosshair: true
        },
        yAxis: [{ 
            labels: {
                format: '{value}元',
                style: {
                    color: Highcharts.getOptions().colors[1]
                }
            }
        }, { // Secondary yAxis
            title: {
                text: '退货金额',
                style: {
                    color: Highcharts.getOptions().colors[0]
                }
            }
        }],
        plotOptions: {
			series: {
				borderWidth: 0,
				dataLabels: {
					enabled: true
				}
			}
		},
        tooltip: {
            shared: true
        },
        
        series: [ {
            name: '退货金额',
            type: 'column',
            yAxis: 1,
            data: data1,
            tooltip: {
                valuePrefix: ' ￥'
            }

        }],
		drilldown: {
			series: []
		}
	});
}

/*function showChart(_data){
	var month=[];
	var money=[];
	$.each(_data,function(i,d){
		//d {};
		d.drilldown=true;
		month.push(_data.name)
		money.push(_data.y)
	});
	$('#chart').highcharts();
	// Create the chart
	$('#chart').highcharts({
		chart: {
			type: 'column',
			events: {
				drillup: function(e) {
					// 上钻回调事件
					console.log(e.seriesOptions);
				},
				drilldown: function (e) {
					if (!e.seriesOptions) {
						var chart = this;
						//alert(JSON.stringify(e.point.name));//e.point是点击图的数据
						var name = e.point.month;
						//var formData = $('#searchForm').serializeJSON();
						var timeY=$('#year').combobox('getValue')
						//alert(timeY+"-"+e.point.name);
						var returnTime= timeY+"-"+e.point.name;
						
						
						//formData.month=e.point.name;
						
						//alert(JSON.stringify(formData))
						chart.showLoading('正在努力加载中 ...');
						$.ajax({
							url : 'report!salesReturnTime.action',
							data : {returnTime:returnTime},
							dataType : 'json',
							type : 'post',
							success : function(rtn) {
								var obj = {};
								obj.data=rtn;
								obj.name=rtn.name;
								chart.hideLoading();
								chart.addSeriesAsDrilldown(e.point, obj);
							}
						});
					}
				}
			}
		},
		title: {
			text: 'Async drilldown'
		},
		 xAxis: {
	        	categories: month,
	            crosshair: true
        },
        yAxis: {
            title: {
            	 text: '退货金额 (元)'
            }
        },
		legend: {
			enabled: false
		},
		plotOptions: {
			series: {
				borderWidth: 0,
				dataLabels: {
					enabled: true
				}
			}
		},
		series: [{
			name: 'Things',
			colorByPoint: true,
			data: _data
		}],
		drilldown: {
			series: []
		}
	});
	
}*/