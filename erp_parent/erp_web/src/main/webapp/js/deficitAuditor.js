$(function(){
	$('#grid').datagrid({
		title:'盘盈盘亏审核',
	    url:'inventory!listByPage.action?t1.state=0',//状态设置为'0',只显示未审核的信息
	    singleSelect:true,
	    pageSize:10,
	    pageList:[5,10,15],
	    pagination:true,
	    columns:[[    
				{field:'uuid',title:'编号',width:100},
				{field:'goodsName',title:'商品',width:100},
				{field:'storeName',title:'仓库',width:100},
				{field:'num',title:'数量',width:100},
				{field:'type',title:'类型',width:100,formatter:formateType},
				{field:'createtime',title:'登记日期',width:100,formatter:formatDate},
				{field:'checktime',title:'审核日期',width:100,formatter:formatDate},
				{field:'createrName',title:'登记人',width:100},
				{field:'checkerName',title:'审核人',width:100},
				{field:'state',title:'状态',width:100,formatter:formateInventory},
				{field:'remark',title:'备注',width:100},
	    ]],
	    onDblClickRow:function(rowIndex,rowData){
	    	$('#auditorDlg').dialog('open');
	    	$('#uuid').html(rowData.uuid);
	    	$('#createtime').html(formatDate(rowData.createtime));
	    	$('#goodsName').html(rowData.goodsName);
	    	$('#storeName').html(rowData.storeName);
	    	$('#num').html(rowData.num);
	    	$('#type').html(formateType(rowData.type));
	    	$('#remark').html(rowData.remark);   	
	    }
	}); 
	
	
	// 盘盈盘亏审核窗口初始化
	$('#auditorDlg').dialog({
		title:'盘盈盘亏审核',
		width:250,
		height:230,
		closed:true,
		modal:true,
		toolbar:[
		     {
		    	text:'审核',
		    	iconCls : 'icon-search',
		    	handler:doCheck
		     }
		]
	});	
	
});


//审核处理的方法
function doCheck(){
	$.messager.confirm('确认','你确定要审核该条信息吗?',function(yes){
		if(yes){
			$.ajax({
				url:'inventory!doCheck.action',
				data:{id:$('#uuid').html()},
				dataType:'json',
				type:'post',
				success:function(rtn){
					$.messager.alert('提示',rtn.message,'info',function(){
						if (rtn.success) {
							// 关闭详情窗口
							$('#auditorDlg').dialog('close');
							// 刷新订单列表
							$('#grid').datagrid('reload');
						}
					});
				}	
			});
		}		
	});	
}



