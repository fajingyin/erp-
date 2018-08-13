var isEditingRowIndex = "";
$(function () {
    $('#gridAdd').datagrid({
        columns: [[{
                field: 'goodsuuid',
                title: '商品编号',
                width: 100,
                align: 'center',
                editor: {type: 'numberbox', options: {disabled: true}}
            },
            {
                field: 'goodsname', title: '商品名称', width: 100, align: 'center', editor: {
                type: 'combobox', options: {
                    url: 'goods!list.action',
                    valueField: 'name',
                    textField: 'name',
                    onSelect: function (value) {
                        //alert(value.inprice+'--'+value.uuid)
                        var eduuid = $('#gridAdd').datagrid('getEditor', {
                            index: isEditingRowIndex,
                            field: 'goodsuuid'
                        });
                        $(eduuid.target).val(value.uuid);
                        edprice = $('#gridAdd').datagrid('getEditor', {index: isEditingRowIndex, field: 'price'});
                        $(edprice.target).val(value.outprice);
                        cal();

                    }
                }
            }
            },
            {
                field: 'price',
                title: '商品价格',
                width: 100,
                align: 'center',
                editor: {type: 'numberbox', options: {precision: 2, disabled: true}}
            },
            {field: 'num', title: '商品数量', width: 100, align: 'center', editor: 'numberbox'},
            {
                field: 'money',
                title: '金额',
                width: 100,
                align: 'center',
                editor: {type: 'numberbox', options: {precision: 2, prefix: '￥', disabled: true}}
            },
            {
                field: '-', title: '操作', width: 100, formatter: function (value, row, index) {
                if (row.num == '合计') {
                    return;
                }
                var oper = "<a href=\"javascript:void(0)\" onclick=\"del(" + index + ')">移除</a>';
                return oper;
            }
            }
        ]],
        toolbar: [{
            text: '增加',
            iconCls: 'icon-add',
            handler: function () {
                //alert('编辑按钮')
                //动态增加行
                $('#gridAdd').datagrid('appendRow', {'price': 0, 'num': 0,});
                //结束编辑航编辑
                $('#gridAdd').datagrid('endEdit', isEditingRowIndex);
                //获取当前页的最后一行的索引
                isEditingRowIndex = $('#gridAdd').datagrid('getRows').length - 1;
                //开启编辑行
                $('#gridAdd').datagrid('beginEdit', isEditingRowIndex);
                bindGridEvent();
            }
        },
            {
                text: '提交',
                iconCls: 'icon-save',
                handler: function () {
                    //结束编辑
                    $('#gridAdd').datagrid('endEdit', isEditingRowIndex);
                    var submitData = $('#orderForm').serializeJSON();

                    if (submitData['t.supplieruuid'] == '') {
                        $.messager.alert('提示', '请选择客户');
                        return;
                    }
                    if (submitData['t.ordersuuid'] == '') {
                        $.messager.alert('提示', '请选择客户的原订单号');
                        return;
                    }
                    //将订单信息表转成json字符串
                    submitData['json'] = JSON.stringify($('#gridAdd').datagrid('getRows'));

                    //提交订单
                    $.ajax({
                        url: 'returnorders!addReturnOut.action',
                        data: submitData,
                        dataType: 'json',
                        type: 'post',
                        success: function (oradd) {
                            $.messager.alert('提示', oradd.message, 'info',function(){
                                if(oradd.success){
                                    $('#ordersAdd').dialog('close');
                                    // 清空客户
                                    $('#supplieruuid').combobox('clear');
                                    $('#ordersuuid').combobox('clear');
                                    $('#searchGrid').datagrid('loadData',[]);
                                    // 加载空的数据达到清空的效果
                                    $('#gridAdd').datagrid('loadData',{total:0,rows:[],footer:[{num:'合计',money:0}]});

                                    // 刷新订单列表
                                    $('#grid').datagrid('reload');

                                }
                            });
                        }
                    });
                }
            }
        ],
        singleSelect: true,
        showFooter: true,
        //点击一行的时候触发本事件 rowIndex：点击的行的索引值，该索引值从0开始。
        //rowData：对应于点击行的记录。
        onClickRow: function (rowIndex, rowData) {
            //alert(rowIndex+':'+rowData)
            $('#gridAdd').datagrid('endEdit', isEditingRowIndex);
            isEditingRowIndex = rowIndex;
            $('#gridAdd').datagrid('beginEdit', isEditingRowIndex);
            bindGridEvent();
        }
    });
    //加载客户信息
/*    $('#supplieruuid').combogrid({
        url: 'supplier!list.action?t1.type=2',
        idField: 'uuid',
        textField: 'name',
        panelWidth: 570,
        mode:'remote',//用户输入将会自动发送到名为'q'的http请求参数，向服务器检索新的数据
        columns: [[
            {field: 'uuid', title: '客户编号', width: 50, align: 'center'},
            {field: 'name', title: '客户名称', width: 100, align: 'center'},
            {field: 'address', title: '客户地址', width: 100, align: 'center'},
            {field: 'contact', title: '联系人', width: 100, align: 'center'},
            {field: 'tele', title: '联系电话', width: 100, align: 'center'},
            {field: 'email', title: '邮箱地址', width: 100, align: 'center'}
        ]]
    })*/

    //w尾行
    $('#gridAdd').datagrid('reloadFooter', [
        {num: '合计', money: 0},
    ]);
});
//移除一行
function del(id) {
    //alert(id + '--' + isEditingRowIndex)

    $('#gridAdd').datagrid('endEdit', isEditingRowIndex)
    $('#gridAdd').datagrid('deleteRow', id);
    var data = $('#gridAdd').datagrid('getData');
    $('#gridAdd').datagrid('loadData', data);
    sum();
}
/*
* 计算每行的金额
* */
function cal() {
    //获取数量的编辑框
    var ednum = $('#gridAdd').datagrid('getEditor', {index: isEditingRowIndex, field: 'num'});
    var num = $(ednum.target).val();
    var edprice = $('#gridAdd').datagrid('getEditor', {index: isEditingRowIndex, field: 'price'});
    var price = $(edprice.target).val();
    //数量乘以单价 保留两位小数
    var money = (price * num).toFixed(2);
    var edmoney = $('#gridAdd').datagrid('getEditor', {index: isEditingRowIndex, field: 'money'});
    $(edmoney.target).val(money);
    //将编辑的每行的金额赋值
    $('#gridAdd').datagrid('getRows')[isEditingRowIndex].money = money;

}

/*
* 给数量编辑框绑定事件
*
* */
function bindGridEvent() {
    //获取数量的编辑框
    var ednum = $('#gridAdd').datagrid('getEditor', {index: isEditingRowIndex, field: 'num'});
    //给其绑定键盘录入事件
    $(ednum.target).bind('keyup', function () {
        cal();
        sum();

    });
    //获取数量的编辑框
    var edprice = $('#gridAdd').datagrid('getEditor', {index: isEditingRowIndex, field: 'price'});
    //给其绑定键盘录入事件
    $(edprice.target).bind('keyup', function () {
        cal();
        sum();
    });

}

/*
* 计算所有行的总金额
* */
function sum() {
    var summoney = 0;
    var row = $('#gridAdd').datagrid('getRows');
    for (i = 0; i < row.length; i++) {
        //alert('金额:'+row[i].money);
        summoney += row[i].money * 1;
    }
    //alert(summoney);
    //$('#sum').html(summoney.toFixed(2))
    $('#gridAdd').datagrid('reloadFooter', [
        {num: '合计', money: summoney.toFixed(2)},
    ]);

}