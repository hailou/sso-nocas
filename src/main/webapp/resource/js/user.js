
var user = (function ($) {
	'use strict';
	var module = {};
	/**
	 * 查询参数
	 * @type {{}}
	 */
	var queryParams = {};
	/**
	 * 事件方法
	 */
	var _bindEvent = function () {

		$('#userAddButton').on('click',function(){
			addUser();
		});
	}
	/**
	 * 添加用户
	 * @param id
     */
	var addUser = function(){
		$.ajax({
			url:'/user/addUser',
			type:'POST',
			data:$('#userForm').serialize()
		}).done(function(result){
			if(result.data==1){
				alert("保存成功");
			}else{
				alert(result.message);
			}
			loadPageData(queryParams);
		});

	}
	/**
	 * 异步加载线下服务数据
	 * @private
	 */
	var loadPageData = function () {
		return $('#js-pager').pagination({
			proxyUrl: '/user/getPageData',
			pageSize : 2,
			simple:false,
			ajaxParam: queryParams || {},
			beforeSendCall: function () {
				$('#pagination').html('加载中。。。。');
			},
			successCallBack: function (result) {
				if (result && result.success) {
					templateHelper('#pagination', 'users.hbs', result);
				}
			},
			errorCallBack: function () {
				$('#pagination').html("网络错误，请刷新页面重试");
			}
		})
	};
	module.init = function () {

		loadPageData(queryParams);

		_bindEvent();

	};

	return module;
})(jQuery);


user.init();
