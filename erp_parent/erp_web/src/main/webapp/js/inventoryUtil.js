/*
 * 日期格式化器
 */
function formatDate(value){
	if(value){
		return new Date(value).Format("yyyy-MM-dd");
	}
	return "";
}

/*
 * 盘盈盘亏状态格式化器
 */
function formateInventory(value){
	switch (value * 1) {
		case 0:return '未审核';
		case 1:return '已审核';
	default: return '';
	}	
}

/*
 * 盘盈盘亏类型格式化器
 */
function formateType(value){
	if(value==1){
		return "盘盈";
	} else if(value==0){
		return "盘亏";
	} else{
		return "";
	}
}
