$(function(){
	$("#grid").datagrid({
		title:'盘盈盘亏记录',
		url:'inventory!listByPage.action',
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
        ]]
	})
	
	//点击查询按钮
	$('#btnSearch').bind('click',function(){
		//把表单数据转换成json对象
		var formData = $('#searchForm').serializeJSON();
		if(formData['t1.createtime'] != ''){
			formData['t1.createtime'] += " 23:59:59";
		}
		if(formData['t1.checktime'] != ''){
			formData['t1.checktime'] += " 23:59:59";
		}
		if(formData['t2.createtime'] != ''){
			formData['t2.createtime'] += " 23:59:59";
		}
		if(formData['t2.checktime'] != ''){
			formData['t2.checktime'] += " 23:59:59";
		}
		$('#grid').datagrid('load',formData);
	});
})


