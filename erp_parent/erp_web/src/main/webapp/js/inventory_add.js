var method = 'addInventory';
$(function(){
	$('#grid').datagrid({
		title:'盘盈盘亏列表',
		url : 'inventory!listByPage.action', // 列出我的仓库列表
		singleSelect : true,
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
				{field:'-',title:'操作',width:100,formatter: function(value,row,index){
	                var oper = "<a href=\"javascript:void(0)\" onclick=\"edit(" + row.uuid+ ',' +row.state + ')">修改</a>';
	                oper += ' <a href="javascript:void(0)" onclick="del(' + row.uuid + ')">删除</a>';
	                return oper;
	          }}
	     ]],	     	     
	     toolbar:[
	  			{
	  				text : '盘盈盘亏登记',
	  				iconCls : 'icon-add',
	  				handler : function() {
	  					// 弹出盈亏的窗口
	  					$('#editDlg').dialog('open');
	  					//清空表单内容
	  					$('#editForm').form('clear');
	  				}
	  			}
	  		]
	});
	// 盘盈盘亏录入窗口初始化
	$('#editDlg').dialog({
		title:'盘盈盘亏',
		width:300,
		height:280,
		closed:true,
		modal:true
	});
	//添加保存的方法
	$('#btnSave').bind('click', function(){    
		var submitData = $('#editForm').serializeJSON();
		$.ajax({
			url: 'inventory!'+ method +'.action',
			data: submitData,
			dataType:'json',
			type:'POST',
			success : function(rtn){
				$.messager.alert('提示',rtn.message,'info',function(){
					if(rtn.success){
						$('#editDlg').dialog('close');
						$('#editForm').form('clear');
						//刷新数据
						$('#grid').datagrid('reload');
					}
				});		
			}
		});
    });  
});
//添加删除的方法
function del(uuid){
	$.messager.confirm("确认","确认要删除吗？",function(yes){
		if(yes){
			$.ajax({
				url: name + 'inventory!delete?id=' + uuid,
				dataType: 'json',
				type: 'post',
				success:function(rtn){
					$.messager.alert("提示",rtn.message,'info',function(){
						//刷新表格数据
						$('#grid').datagrid('reload');
					});
				}
			});
		}
	});
}
/**
 * 修改
 */
function edit(uuid,state){
	if(state * 1 != 0){
		alert("不能更新已审核订单");
		return;
	}
	//弹出窗口
	$('#editDlg').dialog('open');
	
	$("#editForm").form('load','inventory!get.action?id='+uuid);
	
	//设置保存按钮提交的方法为update
	method = "update";
}

