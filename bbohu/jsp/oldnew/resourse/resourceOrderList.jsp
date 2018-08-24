<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>人气订单管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
		});
	</script>
</head>
<body class="gray-bg">
	<div class="wrapper wrapper-content">
	<div class="ibox">
	<div class="ibox-title">
		<h5>人气订单管理列表 </h5>
		<div class="ibox-tools">
			<a class="collapse-link">
				<i class="fa fa-chevron-up"></i>
			</a>
			<a class="dropdown-toggle" data-toggle="dropdown" href="#">
				<i class="fa fa-wrench"></i>
			</a>
			<ul class="dropdown-menu dropdown-user">
				<li><a href="#">选项1</a>
				</li>
				<li><a href="#">选项2</a>
				</li>
			</ul>
			<a class="close-link">
				<i class="fa fa-times"></i>
			</a>
		</div>
	</div>
    
    <div class="ibox-content">
	<sys:message content="${message}"/>
	
	<!--查询条件-->
	<div class="row">
	<div class="col-sm-12">
	<form:form id="searchForm" modelAttribute="resourceOrder" action="${ctx}/oldnew/resourse/resourceOrder/" method="post" class="form-inline">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<table:sortColumn id="orderBy" name="orderBy" value="${page.orderBy}" callback="sortOrRefresh();"/><!-- 支持排序 -->
		<div class="form-group">
		 </div>	
	</form:form>
	<br/>
	</div>
	</div>
	
	<!-- 工具栏 -->
	<div class="row">
	<div class="col-sm-12">
		<div class="pull-left">
			<shiro:hasPermission name="oldnew:resourse:resourceOrder:add">
				<table:addRow url="${ctx}/oldnew/resourse/resourceOrder/form" title="资源订单管理"></table:addRow><!-- 增加按钮 -->
			</shiro:hasPermission>
			<shiro:hasPermission name="oldnew:resourse:resourceOrder:edit">
			    <table:editRow url="${ctx}/oldnew/resourse/resourceOrder/form" title="资源订单管理" id="contentTable"></table:editRow><!-- 编辑按钮 -->
			</shiro:hasPermission>
			<shiro:hasPermission name="oldnew:resourse:resourceOrder:del">
				<table:delRow url="${ctx}/oldnew/resourse/resourceOrder/deleteAll" id="contentTable"></table:delRow><!-- 删除按钮 -->
			</shiro:hasPermission>
			<shiro:hasPermission name="oldnew:resourse:resourceOrder:import">
				<table:importExcel url="${ctx}/oldnew/resourse/resourceOrder/import"></table:importExcel><!-- 导入按钮 -->
			</shiro:hasPermission>
			<shiro:hasPermission name="oldnew:resourse:resourceOrder:export">
	       		<table:exportExcel url="${ctx}/oldnew/resourse/resourceOrder/export"></table:exportExcel><!-- 导出按钮 -->
	       	</shiro:hasPermission>
	       <button class="btn btn-white btn-sm " data-toggle="tooltip" data-placement="left" onclick="sortOrRefresh()" title="刷新"><i class="glyphicon glyphicon-repeat"></i> 刷新</button>
		
			</div>
		<div class="pull-right">
			<button  class="btn btn-primary btn-rounded btn-outline btn-sm " onclick="search()" ><i class="fa fa-search"></i> 查询</button>
			<button  class="btn btn-primary btn-rounded btn-outline btn-sm " onclick="reset()" ><i class="fa fa-refresh"></i> 重置</button>
		</div>
	</div>
	</div>
	
	<!-- 表格 -->
	<table id="contentTable" class="table table-striped table-bordered table-hover table-condensed dataTables-example dataTable">
		<thead>
			<tr>
				<th> <input type="checkbox" class="i-checks"></th>
				<th  class="sort-column platForm">直播平台</th>
				<th  class="sort-column messageId">信息发布人</th>
				<th  class="sort-column roomNum">房间号</th>
				<th  class="sort-column orderCode">订单编号</th>
				<th  class="sort-column typeId">产品类型</th>
				<th  class="sort-column price">价格</th>
				<th  class="sort-column status">订单状态</th>
				<th  class="sort-column remarks">备注信息</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="resourceOrder">
			<tr>
				<td> <input type="checkbox" id="${resourceOrder.id}" class="i-checks"></td>
				<td><a  href="#" onclick="openDialogView('查看资源订单管理', '${ctx}/oldnew/resourse/resourceOrder/form?id=${resourceOrder.id}','800px', '500px')">
					${fns:getDictLabel(resourceOrder.platForm, 'platform', '')}
				</a></td>
				<td>
					${resourceOrder.messName}
				</td>
				<td>
					${resourceOrder.roomNum}
				</td>
				<td>
					${resourceOrder.orderCode}
				</td>
				<td>
					${resourceOrder.name}
				</td>
				<td>
					${resourceOrder.price}元
				</td>
				<td>
					<c:if test="${resourceOrder.status == 0}">
						未付款
					</c:if>
					<c:if test="${resourceOrder.status == 1}">
						已付款
					</c:if>
					<c:if test="${resourceOrder.status == 2}">
						已完成
					</c:if>
				</td>
				<td>
					${resourceOrder.remarks}
				</td>
				<td>
					<shiro:hasPermission name="oldnew:resourse:resourceOrder:view">
						<a href="#" onclick="openDialogView('查看资源订单管理', '${ctx}/oldnew/resourse/resourceOrder/form?id=${resourceOrder.id}','800px', '500px')" class="btn btn-info btn-xs" ><i class="fa fa-search-plus"></i> 查看</a>
					</shiro:hasPermission>
					<shiro:hasPermission name="oldnew:resourse:resourceOrder:edit">
    					<a href="#" onclick="openDialog('修改资源订单管理', '${ctx}/oldnew/resourse/resourceOrder/form?id=${resourceOrder.id}','800px', '500px')" class="btn btn-success btn-xs" ><i class="fa fa-edit"></i> 修改</a>
    				</shiro:hasPermission>
    				<c:if test="${resourceOrder.status == 1}">
	    				<shiro:hasPermission name="oldnew:resourse:resourceOrder:del">
							<a href="${ctx}/oldnew/resourse/resourceOrder/delete?id=${resourceOrder.id}" onclick="return confirmx('确认发货了吗？', this.href)"   class="btn btn-danger btn-xs"> 发货</a>
						</shiro:hasPermission>
					</c:if>
					<c:if test="${resourceOrder.status == 2}">
	    				<shiro:hasPermission name="oldnew:resourse:resourceOrder:del">
							<a href="#" class="btn btn-info btn-xs" > 已发货</a>
						</shiro:hasPermission>
					</c:if>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	
		<!-- 分页代码 -->
	<table:page page="${page}"></table:page>
	<br/>
	<br/>
	</div>
	</div>
</div>
</body>
</html>