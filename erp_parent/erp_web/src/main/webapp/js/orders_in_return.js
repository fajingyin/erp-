$(function(){
	// 订单列表
	$('#grid').datagrid({
		title:'采购退货列表',
		url : 'returnorders!listByPage.action?t1.type=1', // 列出我的采购退货订单
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
			$('#ordersDlg').dialog('open');
			$('#uuid').html(rowData.uuid);
			$('#supplierName').html(rowData.supplierName);
			$('#state').html(formatReturnState(rowData.state));
			$('#createrName').html(rowData.createrName);
			$('#checkerName').html(rowData.checkerName);
			$('#enderName').html(rowData.enderName);
			$('#createtime').html(formatDate(rowData.createtime));
			$('#checktime').html(formatDate(rowData.checktime));
			$('#starttime').html(formatDate(rowData.starttime));
			$('#endtime').html(formatDate(rowData.endtime));
			
			// 加载明细的数据
			$('#itemgrid').datagrid('loadData',rowData.returnorderdetails);
		},
		toolbar:[
			{
				text : '采购退货申请',
				iconCls : 'icon-add',
				handler : function() {
					// 弹出采购申请的窗口
					$('#addOrdersDlg').dialog('open');
				}
			}
		]
	});
	
	// 订单详情窗口初始化
	$('#ordersDlg').dialog({
		title:'订单详情',
		width:700,
		height:340,
		closed:true,
		modal:true
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
	
	// 采购申请窗口初始化
	$('#addOrdersDlg').dialog({
		title:'采购申请',
		width:700,
		height:400,
		closed:true,
		modal:true
	});
	
	//订单详情回显
	 searchGrid();
	 
});




function searchGrid(){
	//订单详情回显
    $('#searchGrid').datagrid({
        title: '订单详情',
        columns: [[
            {field: 'uuid', title: '商品编号', width: 80, align: 'center'},
            {field: 'goodsname', title: '商品名称', width: 200, align: 'center'},
            {field: 'price', title: '商品价格', width: 100, align: 'center'},
            {field: 'num', title: '商品数量', width: 80, align: 'center'},
            {field: 'money', title: '金额', width: 100, align: 'center'},
            {field: 'state', title: '状态', width: 100, align: 'center', formatter: getRtnOrdersBuyState}
        ]],
        rownumbers: true,
        singleSelect: true,
        fitColumns: true

    });
}






/**
 * 用于采购退货订单明细的状态
 * @param value
 * @param type
 * @returns {*}
 */

function getRtnOrdersBuyState(value) {

        if (value == 0) {
            return '未入库';
        }
        if (value == 1) {
            return '已入库';
        }
}