<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>订单管理管理</title>
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
		<h5>订单管理列表 </h5>
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
	<form:form id="searchForm" modelAttribute="oldnewOrder" action="${ctx}/oldnew/order/oldnewOrder/" method="post" class="form-inline">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<table:sortColumn id="orderBy" name="orderBy" value="${page.orderBy}" callback="sortOrRefresh();"/><!-- 支持排序 -->
		<div class="form-group">
			<span>订单编号：</span>
				<form:input path="orderCode" htmlEscape="false"  class=" form-control input-sm"/>
			<span>信息发布人：</span>
				<form:select path="publisherId" class="form-control">
					<form:option value="" label=""/>
					<form:options items="${user }" itemLabel="nickname" itemValue="id" htmlEscape="false"/>
				</form:select>
			<span>信息接单人：</span>
				<form:select path="takerId" class="form-control">
					<form:option value="" label=""/>
					<form:options items="${user }" itemLabel="nickname" itemValue="id" htmlEscape="false"/>
				</form:select>
		 </div>	
	</form:form>
	<br/>
	</div>
	</div>
	
	<!-- 工具栏 -->
	<div class="row">
	<div class="col-sm-12">
		<div class="pull-left">
			<%-- <shiro:hasPermission name="oldnew:order:oldnewOrder:add">
				<table:addRow url="${ctx}/oldnew/order/oldnewOrder/form" title="订单管理"></table:addRow><!-- 增加按钮 -->
			</shiro:hasPermission>
			<shiro:hasPermission name="oldnew:order:oldnewOrder:edit">
			    <table:editRow url="${ctx}/oldnew/order/oldnewOrder/form" title="订单管理" id="contentTable"></table:editRow><!-- 编辑按钮 -->
			</shiro:hasPermission> --%>
			<shiro:hasPermission name="oldnew:order:oldnewOrder:del">
				<table:delRow url="${ctx}/oldnew/order/oldnewOrder/deleteAll" id="contentTable"></table:delRow><!-- 删除按钮 -->
			</shiro:hasPermission>
			<shiro:hasPermission name="oldnew:order:oldnewOrder:import">
				<table:importExcel url="${ctx}/oldnew/order/oldnewOrder/import"></table:importExcel><!-- 导入按钮 -->
			</shiro:hasPermission>
			<shiro:hasPermission name="oldnew:order:oldnewOrder:export">
	       		<table:exportExcel url="${ctx}/oldnew/order/oldnewOrder/export"></table:exportExcel><!-- 导出按钮 -->
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
				<th  class="sort-column orderCode">订单编号</th>
				<th  class="sort-column orderPrice">订单价格</th>
				<th  class="sort-column reduceMoney">优惠金额</th>
				<th  class="sort-column actualMoney">应付金额</th>
				<th  class="sort-column status">订单状态</th>
				<th  class="sort-column publisherId">信息发布人</th>
				<th  class="sort-column takerId">信息接单人</th>
				<th  class="sort-column createDate">创建时间</th>
				<th  class="sort-column receiveDate">接单时间</th>
				<th  class="sort-column affirmDate">确认时间</th>
				<th  class="sort-column orgerType">订单类型</th>
				<th  class="sort-column overDate">完成时间</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="oldnewOrder">
			<tr>
				<td> <input type="checkbox" id="${oldnewOrder.id}" class="i-checks"></td>
				<td><a  href="#" onclick="openDialogView('查看订单管理', '${ctx}/oldnew/order/oldnewOrder/form?id=${oldnewOrder.id}','800px', '500px')">
					${oldnewOrder.orderCode}
				</a></td>
				<td>
					<fmt:formatNumber value="${oldnewOrder.orderPrice}" type="currency"/>
				</td>
				<td>
					<fmt:formatNumber value="${oldnewOrder.reduceMoney}" type="currency"/>
				</td>
				<td>
					<fmt:formatNumber value="${oldnewOrder.actualMoney}" type="currency"/>
				</td>
				<td>
					${fns:getDictLabel(oldnewOrder.status, 'order_status', '')}
				</td>
				<td>
					${oldnewOrder.publisherName}
				</td>
				<td>
					${oldnewOrder.takerName}
				</td>
				<td>
					<fmt:formatDate value="${oldnewOrder.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<fmt:formatDate value="${oldnewOrder.receiveDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<fmt:formatDate value="${oldnewOrder.affirmDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<c:choose>
						<c:when test="${oldnewOrder.orgerType == '1' }">求带订单</c:when>
						<c:otherwise>带人订单</c:otherwise>
					</c:choose>
				</td>
				<td>
					<fmt:formatDate value="${oldnewOrder.overDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<shiro:hasPermission name="oldnew:order:oldnewOrder:view">
						<a href="#" onclick="openDialogView('查看订单管理', '${ctx}/oldnew/order/oldnewOrder/form?id=${oldnewOrder.id}','800px', '500px')" class="btn btn-info btn-xs" ><i class="fa fa-search-plus"></i> 查看</a>
					</shiro:hasPermission>
					<shiro:hasPermission name="oldnew:order:oldnewOrder:edit">
    					<a href="#" onclick="openDialog('修改订单管理', '${ctx}/oldnew/order/oldnewOrder/form?id=${oldnewOrder.id}','800px', '500px')" class="btn btn-success btn-xs" ><i class="fa fa-edit"></i> 修改</a>
    				</shiro:hasPermission>
    				<c:if test="${not empty oldnewOrder.takerName}">
						<a href="#" onclick="openDialog('经纪人评论', '${ctx}/oldnew/comment/oldnewComment/form?orderId=${oldnewOrder.id}','800px', '500px')" class="btn btn-info btn-xs" >去评论</a>
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