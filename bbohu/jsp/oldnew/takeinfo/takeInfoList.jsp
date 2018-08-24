<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>带人信息管理</title>
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
		<h5>带人信息列表 </h5>
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
	<form:form id="searchForm" modelAttribute="takeInfo" action="${ctx}/oldnew/takeinfo/takeInfo/" method="post" class="form-inline">
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
			<shiro:hasPermission name="oldnew:takeinfo:takeInfo:add">
				<table:addRow url="${ctx}/oldnew/takeinfo/takeInfo/form" title="带人信息"></table:addRow><!-- 增加按钮 -->
			</shiro:hasPermission>
			<shiro:hasPermission name="oldnew:takeinfo:takeInfo:edit">
			    <table:editRow url="${ctx}/oldnew/takeinfo/takeInfo/form" title="带人信息" id="contentTable"></table:editRow><!-- 编辑按钮 -->
			</shiro:hasPermission>
			<shiro:hasPermission name="oldnew:takeinfo:takeInfo:del">
				<table:delRow url="${ctx}/oldnew/takeinfo/takeInfo/deleteAll" id="contentTable"></table:delRow><!-- 删除按钮 -->
			</shiro:hasPermission>
			<shiro:hasPermission name="oldnew:takeinfo:takeInfo:import">
				<table:importExcel url="${ctx}/oldnew/takeinfo/takeInfo/import"></table:importExcel><!-- 导入按钮 -->
			</shiro:hasPermission>
			<shiro:hasPermission name="oldnew:takeinfo:takeInfo:export">
	       		<table:exportExcel url="${ctx}/oldnew/takeinfo/takeInfo/export"></table:exportExcel><!-- 导出按钮 -->
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
				<th  class="sort-column nickname">主播昵称</th>
				<th  class="sort-column funsNum">粉丝要求数</th>
				<th  class="sort-column platformcode">直播平台</th>
				<th  class="sort-column timeSection">要求时段</th>
				<th  class="sort-column price">带人价格</th>
				<th  class="sort-column monthOrdernum">每月接单数</th>
				<th  class="sort-column status">是否马上接单</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="takeInfo">
			<tr>
				<td> <input type="checkbox" id="${takeInfo.id}" class="i-checks"></td>
				<td><a  href="#" onclick="openDialogView('查看带人信息', '${ctx}/oldnew/takeinfo/takeInfo/form?id=${takeInfo.id}','800px', '500px')">
					${takeInfo.nickname}
				</a></td>
				<td>
					${takeInfo.funsNum}
				</td>
				<td>
					${fns:getDictLabel(takeInfo.platformcode, 'platform', '')}
				</td>
				<td>
					${takeInfo.timeSection}
				</td>
				<td>
					${takeInfo.price}
				</td>
				<td>
					${takeInfo.monthOrdernum}
				</td>
				<td>
					${fns:getDictLabel(takeInfo.status, 'yes_no', '')}
				</td>
				<td>
					<shiro:hasPermission name="oldnew:takeinfo:takeInfo:view">
						<a href="#" onclick="openDialogView('查看带人信息', '${ctx}/oldnew/takeinfo/takeInfo/form?id=${takeInfo.id}','800px', '500px')" class="btn btn-info btn-xs" ><i class="fa fa-search-plus"></i> 查看</a>
					</shiro:hasPermission>
					<shiro:hasPermission name="oldnew:takeinfo:takeInfo:edit">
    					<a href="#" onclick="openDialog('修改带人信息', '${ctx}/oldnew/takeinfo/takeInfo/form?id=${takeInfo.id}','800px', '500px')" class="btn btn-success btn-xs" ><i class="fa fa-edit"></i> 修改</a>
    				</shiro:hasPermission>
    				<shiro:hasPermission name="oldnew:takeinfo:takeInfo:del">
						<a href="${ctx}/oldnew/takeinfo/takeInfo/delete?id=${takeInfo.id}" onclick="return confirmx('确认要删除该带人信息吗？', this.href)"   class="btn btn-danger btn-xs"><i class="fa fa-trash"></i> 删除</a>
					</shiro:hasPermission>
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