

$(function () {

    //加载表格数据
    $('#grid').datagrid({
        url: 'returnorders!myListByPage.action?t1.type=2',
        columns:[[{field: 'uuid', title: '编号', width: 40},
            {field: 'createtime', title: '生成日期', width: 80, formatter: formatDate},
            {field: 'endtime', title: '出库日期', width: 100, formatter: formatDate},
            {field:'checktime',title:'检查日期',width:100,formatter: formatDate},
            {field: 'createrName', title: '销售员', width: 80},
            {field:'checkerName',title:'审核员工编号',width:100},
            {field: 'enderName', title: '库管员', width: 80},
            {field: 'supplierName', title: '客户', width: 100},
            {field: 'totalmoney', title: '合计金额', width: 100},
            {field: 'state', title: '状态', width: 100, formatter: getOrderState2},
            {field: 'waybillsn', title: '运单号', width: 100},
            {field:'ordersuuid',title:'原订单编号',width:100}
        ]],
        singleSelect: true,
        pagination: true,
        toolbar:
        [{
            text: '销售退货登记',
            iconCls: 'icon-add',
            handler: function () {
                $('#ordersAdd').dialog('open');

            }
        }]
    });


    /*
    * 双击某一行的时候触发
    * rowIndex：点击的行的索引值，该索引值从0开始。
        rowData：对应于点击行的记录。
    *
    * */
    $('#grid').datagrid({
        onDblClickRow: function (rowIndex, rowData) {
            
            $('#ordersWindow').window('open');
            $('#uuid').html(rowData.uuid);
            $('#supplier').html(rowData.supplierName);
            $('#state').html(getOrderState2(rowData.state));
            $('#createrName').html(rowData.createrName);
            $('#checkerName').html(rowData.checkerName);
            $('#enderName').html(rowData.enderName);
            $('#createtime').html(formatDate(rowData.createtime));
            $('#checktime').html(formatDate(rowData.checktime));
            $('#endtime').html(formatDate(rowData.endtime));
            $('#waybillns').html(rowData.waybillsn);

            //加载到orderItem表中
            $('#orderItem').datagrid('loadData', rowData.returnorderdetails);

        }
    });

    //订单明细表
    $('#orderItem').datagrid({
        title: '订单明细',
        columns: [[
            {field: 'uuid', title: '商品编号', width: 80, align: 'center'},
            {field: 'goodsname', title: '商品名称', width: 200, align: 'center'},
            {field: 'price', title: '商品价格', width: 100, align: 'center'},
            {field: 'num', title: '商品数量', width: 80, align: 'center'},
            {field: 'money', title: '金额', width: 100, align: 'center'},
            {field: 'state', title: '状态', width: 100, align: 'center', formatter: getEnderState2}
        ]],
        rownumbers: true,
        singleSelect: true,
        fitColumns: true

    });

    /*
       初始化采购申请窗口
    */

    $('#ordersAdd').dialog({
        title: '销售退货登记',
        width: 750,
        height: 400,
        closed: true,
        cache: true,
        modal: true
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
            {field: 'state', title: '状态', width: 100, align: 'center', formatter: getRtnOrdersSalesState}
        ]],
        rownumbers: true,
        singleSelect: true,
        fitColumns: true

    });
}

function getOrderdetailState(value) {
    if (value == 1) {
        return '采购';
    }
    if (value == 2) {
        return '销售';
    }
}

/*
    转换时间格式
 */
function formatDate(value) {
    if (value) {
        return new Date(value).Format("yyyy-MM-dd");
    }
    return "";
}



/**
 * 用于返回销售订单明细和采购订单明细的状态
 * @param value
 * @param type
 * @returns {*}
 */

function getEnderState2(value) {

        if (value == 0) {
            return '未入库';
        }
        if (value == 1) {
            return '已入库';
        }
}

/**
 * 用于销售退货订单明细的状态
 * @param value
 * @param type
 * @returns {*}
 */

function getRtnOrdersSalesState(value) {

        if (value == 0) {
            return '未出库';
        }
        if (value == 1) {
            return '已出库';
        }
}

/**
 * 用于返回销售订单的状态
 * @param value
 * @returns {*}
 */
function getOrderState2(value) {
    if (value == 0) {
        return '未审核';
    }
    if (value == 1) {
        return '未入库';
    }
    if (value == 2) {
        return '已入库';
    }
}