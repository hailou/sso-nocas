(function($) {
	var show_other;
	// 分页计算器
	$.PaginationCalculator = function(maxentries, opts) {
		this.maxentries = maxentries;
		this.opts = opts;
	}

	$.extend($.PaginationCalculator.prototype, {
		// 得到总页数
		numPages : function() {
			return Math.ceil(this.maxentries / this.opts.pageSize);
		},
		// 初始化数据
		getInterval : function(current_page) {
			var ne_half = Math.ceil(this.opts.num_display_entries / 2);
			var np = this.numPages(); // 总页数
			var upper_limit = np - this.opts.num_display_entries;
			var start = current_page > ne_half // 起始页
				? Math.max(Math.min(current_page - ne_half,
				upper_limit), 0)
				: 0;
			var end = current_page > ne_half ? Math.min(current_page // 结束页
				+ ne_half, np) : Math.min(
				this.opts.num_display_entries, np);
			return {
				start : start,
				end : end
			};
		}
	});

	// 创建一个空的构造器
	$.PaginationRenderers = {}

	// 链接渲染器
	$.PaginationRenderers.defaultRenderer = function(maxentries, opts) {
		this.maxentries = maxentries;
		this.opts = opts;
		this.pc = new $.PaginationCalculator(maxentries, opts);
	}
	$.extend($.PaginationRenderers.defaultRenderer.prototype, {

		createLink : function(page_id, current_page, appendopts) {
			var lnk, np = this.pc.numPages(); // 总记录数
			page_id = page_id < 0 ? 0 : (page_id < np ? page_id : np - 1);
			appendopts = $.extend({
				text : page_id + 1,
				classes : ""
			}, appendopts || {});
			if (page_id == current_page) {
				if(show_other){
					lnk = $("<span class='current'>" + appendopts.text + "</span>");
				}else{
					if(appendopts.text=="上一页"||appendopts.text=="下一页"){
						lnk = $("<span class='current'>" + appendopts.text + "</span>");
					}else{
						lnk = $("<span class='current'>" + appendopts.text+"/"+np + "</span>");
					}
				}
			} else {
				lnk = $("<a>" + appendopts.text + "</a>").attr('href',
					this.opts.link_to.replace(/__id__/, page_id));
			}

			if (appendopts.classes) {
				lnk.addClass(appendopts.classes);
			}
			lnk.data('page_id', page_id);
			return lnk;
		},

		// 根据条件生成链接
		appendRange : function(container, current_page, start, end, opts) {
			if(this.maxentries>0){
				var i;
				if(show_other){
					for (i = start; i < end; i++) {
						this.createLink(i, current_page, opts).appendTo(container);
					}
				}else{
					for (i = current_page; i <= current_page; i++) {
						this.createLink(i, current_page, opts).appendTo(container);
					}
				}
			}
		},

		getLinks : function(current_page, eventHandler) {

			var begin, end,
				interval = this.pc.getInterval(current_page),
				np = this.pc.numPages(),
				fragment = $("<div class='pagination'></div>");
			if(this.maxentries>0){
				fragment.append("<span class='recodeinfo'>总共 "+this.maxentries+" 条记录</span>");
				// Generate "Previous"-Link
				if(this.opts.prev_text && (current_page > 0 || this.opts.prev_show_always)){
					fragment.append(this.createLink(current_page-1, current_page, {text:this.opts.prev_text, classes:"prev"}));
				}
				// Generate starting points
				if (interval.start > 0 && this.opts.num_edge_entries > 0)
				{
					end = Math.min(this.opts.num_edge_entries, interval.start);
					this.appendRange(fragment, current_page, 0, end, {classes:'sp'});
					if(this.opts.num_edge_entries < interval.start && this.opts.ellipse_text)
					{
						jQuery("<span class='ellipse_text'>"+this.opts.ellipse_text+"</span>").appendTo(fragment);
					}
				}
				// Generate interval links
				this.appendRange(fragment, current_page, interval.start, interval.end);
				// Generate ending points
				if (interval.end < np && this.opts.num_edge_entries > 0)
				{
					if(np-this.opts.num_edge_entries > interval.end && this.opts.ellipse_text)
					{
						jQuery("<span class='ellipse_text'>"+this.opts.ellipse_text+"</span>").appendTo(fragment);
					}
					begin = Math.max(np-this.opts.num_edge_entries, interval.end);
					this.appendRange(fragment, current_page, begin, np, {classes:'ep'});

				}
				// Generate "Next"-Link
				if(this.opts.next_text && (current_page < np-1 || this.opts.next_show_always)){
					fragment.append(this.createLink(current_page+1, current_page, {text:this.opts.next_text, classes:"next"}));
				}
				$('a', fragment).click(eventHandler);
			}else{
//				fragment = $("<div class='nolist'>没有符合的记录</div>");
			}
			return fragment;
		}
	});

	// Extend jQuery
	$.fn.pagination = function(opts) {
		// 初始化设置
		opts = jQuery.extend({
			pageSize : 10,
			current_page : 0,
			num_display_entries : 5,
			num_edge_entries:1,
			link_to : "#",
			prev_text : "上一页",
			next_text : "下一页",
			ellipse_text:"...",
			prev_show_always : true,
			next_show_always : true,
			show_other:true,
			simple:false,
			proxyUrl : '',
			renderer : "defaultRenderer",
			beforeSendCall : function(){return false;},
			successCallBack : function() {
				return false;
			},
			callback : function() {
				return false;
			},
			errorCallBack : function(){
				return false;
			}
		}, opts || {});

		var proxyUrl = opts.proxyUrl;
		var pageSize = opts.pageSize; // 一页显示记录数
		var maxentries = 0; // 总记录数
		var startRecord = 0; // 起始记录
		show_other=opts.show_other;
		var limitRecord = opts.pageSize; // limit记录
		var containers = this, renderer, links, current_page;

		function getRemoteData(type, fn, obj) {
			var ajaxParam = jQuery.extend(obj, opts.ajaxParam || {});
			$.ajax({
				type : "get",
				url : proxyUrl,
				data : ajaxParam, // 参数
				dataType : "json",
				timeout : 30000,
				beforeSend :function(XMLHttpRequest) {
					opts.beforeSendCall();
				},
				success : function(data) {
					eval(data);
					maxentries = data.totalCount; // 总记录数
					if (type == 0) {
						eval(fn + "(" + maxentries + ")");
					} else {
						eval(fn + "(" + obj.pindex + ")");
					}
					opts.successCallBack(data);
				},
				error : function() {
					opts.errorCallBack();
				}
			});
		};

		function paginationClickHandler(evt) {
			var links, new_current_page = $(evt.target).data('page_id'), continuePropagation = selectPage(new_current_page);
			if (!continuePropagation) {
				evt.stopPropagation();
			}
			return continuePropagation;
		}

		function selectPagePrepare(new_current_page) {
			containers.data('current_page', new_current_page);
			links = renderer.getLinks(new_current_page, paginationClickHandler);
			containers.empty();
			links.appendTo(containers);
		}

		function selectPage(new_current_page) {
			var ps = opts.pageSize; // 一页显示记录数
			startRecord = ps * new_current_page + 1;
//			startRecord = ps * new_current_page;
			limitRecord = ps;
			var params = {
				start : startRecord,
				limit : limitRecord,
				param : new_current_page,
				pindex: new_current_page,
				psize : ps

			};
			getRemoteData(1, 'selectPagePrepare', params);
			var continuePropagation = opts.callback(new_current_page,
				containers);
			return continuePropagation;
		}

		// -----------------------------------
		// 初始化容器
		// -----------------------------------
		current_page = opts.current_page;
		containers.data('current_page', current_page);
		opts.pageSize = (!opts.pageSize || opts.pageSize < 0)
			? 1
			: opts.pageSize;

		if (!$.PaginationRenderers[opts.renderer]) {
			throw new ReferenceError("Pagination renderer '" + opts.renderer
				+ "' was not found in jQuery.PaginationRenderers object.");
		}

		function initContainers(maxentries) {
			// 创建renderer
			renderer = new $.PaginationRenderers[opts.renderer](maxentries,
				opts);
			var pc = new $.PaginationCalculator(maxentries, opts);
			var np = pc.numPages(); // 总页数
			containers.bind('setPage', {
				numPages : np
			}, function(evt, page_id) {
				if (page_id >= 0 && page_id < evt.data.numPages) {
					selectPage(page_id);
					return false;
				}
			});
			containers.bind('prevPage', function(evt) {
				var current_page = $(this).data('current_page');
				if (current_page > 0) {
					selectPage(current_page - 1);
				}
				return false;
			});
			containers.bind('nextPage', {
				numPages : np
			}, function(evt) {
				var current_page = $(this).data('current_page');
				if (current_page < evt.data.numPages - 1) {
					selectPage(current_page + 1);
				}
				return false;
			});

			// 生成分页链接
			links = renderer.getLinks(current_page, paginationClickHandler);
			containers.empty();
			links.appendTo(containers);
			opts.callback(current_page, containers);
		}

		// 获得远程数据
		var params = {
			start : startRecord,
			limit : limitRecord,
			param : null,
			pindex: null,
			psize : pageSize
		}
		getRemoteData(0, 'initContainers', params);

	}

})(jQuery);
