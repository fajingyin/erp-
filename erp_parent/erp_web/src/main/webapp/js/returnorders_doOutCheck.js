$(function(){
	// 订单列表
	$('#grid').datagrid({
		title:'采购订单列表',
		url : 'returnorders!listByPage.action?t1.type=1&t1.state=0', // 只能列出未审核采购订单
		singleSelect : true,
		pagination:true,
		columns : [ [ 
			{field:'uuid',title:'编号',width:100},
			{field:'createtime',title:'创建日期',width:100,formatter:formatDate},
			{field:'checktime',title:'审核日期',width:100,formatter:formatDate},
			{field:'endtime',title:'结束日期',width:100,formatter:formatDate},
			{field:'createrName',title:'创建人',width:100},
			{field:'checkerName',title:'审核人',width:100},
			{field:'enderName',title:'结束人',width:100},
			{field:'supplierName',title:'供应商',width:100},
			{field:'totalmoney',title:'合计金额',width:100},
			{field:'state',title:'状态',width:100,formatter:formatReturnState},
			{field:'ordersuuid',title:'原订单ID',width:100}
		] ],
		onDblClickRow:function(rowIndex, rowData){
			// 弹出详情窗口
			$('#returnordersDlg').dialog('open');
			//$('#id').html(rowData[id]);
			/*$('#ordersTable td').each(function(i,td){
				if(td.id){ // td.id {field:uuid....}
					// 获取列的配置信息
					var columnCfg = $('#grid').datagrid('getColumnOption',td.id);
					//alert(td.id);
					var value = rowData[td.id];
					if(columnCfg.formatter){
						// 有格式化器，调用格式化的方法
						value = columnCfg.formatter(value);
					}
					$(td).html(value);
				}
			});*/
			
			
			$('#uuid').html(rowData.uuid);
			$('#supplierName').html(rowData.supplierName);
			$('#state').html(formatState(rowData.state));
			$('#createrName').html(rowData.createrName);
			$('#checkerName').html(rowData.checkerName);
			$('#enderName').html(rowData.enderName);
			$('#createtime').html(formatDate(rowData.createtime));
			$('#checktime').html(formatDate(rowData.checktime));
			$('#starttime').html(formatDate(rowData.starttime));
			$('#endtime').html(formatDate(rowData.endtime));
			
			// 加载明细的数据
			$('#itemgrid').datagrid('loadData',rowData.returnorderdetails);
		}
	});
	
	// 订单详情窗口初始化
	$('#returnordersDlg').dialog({
		title:'订单详情',
		width:700,
		height:340,
		closed:true,
		modal:true,
		toolbar:[
			{
				text : '审核',
				iconCls : 'icon-search',
				handler : doOutCheck
			}
		]
	});
	
	//明细表格初始化
	$('#itemgrid').datagrid({
		title:'商品列表',
		singleSelect : true,
		columns : [ [ 
			{field:'uuid',title:'编号',width:60},
			{field:'goodsuuid',title:'商品编号',width:80},
			{field:'goodsname',title:'商品名称',width:100},
			{field:'price',title:'价格',width:100},
			{field:'num',title:'数量',width:100},
			{field:'money',title:'金额',width:100},
			{field:'state',title:'状态',width:60,formatter:formatReturnDetailState}
		] ]
	});
});

/**
 * 订单审核
 * @returns
 */
function doOutCheck(){
	$.messager.confirm('确认', '确认要审核吗?', function(yes) {
		if (yes) {
			$.ajax({
				url : 'returnorders!doOutCheck.action',
				data : {id:$('#uuid').html()},// 订单编号
				dataType : 'json',
				type : 'post',
				success : function(rtn) {
					$.messager.alert('提示', rtn.message, 'info', function() {
						if (rtn.success) {
							// 关闭详情窗口
							$('#returnordersDlg').dialog('close');
							// 刷新订单列表
							$('#grid').datagrid('reload');
						}
					});
				}
			});
		}
	});
}
