<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>订单评价管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		var validateForm;
		function doSubmit(){//回调函数，在编辑和保存动作时，供openDialog调用提交表单。
		  if(validateForm.form()){
			  $("#inputForm").submit();
			  return true;
		  }
	
		  return false;
		}
		$(document).ready(function() {
			validateForm = $("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
			
		});
	</script>
</head>
<body class="hideScroll">
		<form:form id="inputForm" modelAttribute="oldnewComment" action="${ctx}/oldnew/comment/oldnewComment/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>	
		<table class="table table-bordered  table-condensed dataTables-example dataTable no-footer">
		   <tbody>
				<tr>
					<td class="width-15 active"><label class="pull-right">备注信息：</label></td>
					<td class="width-35">
						<form:textarea path="remarks" htmlEscape="false" rows="4"    class="form-control "/>
					</td>
					<td class="width-15 active"><label class="pull-right">满意度：</label></td>
					<td class="width-35">
						<form:input path="satisfyLevel" htmlEscape="false"    class="form-control "/>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">人气峰值：</label></td>
					<td class="width-35">
						<form:input path="maxPopularity" htmlEscape="false"    class="form-control "/>
					</td>
					<td class="width-15 active"><label class="pull-right">粉丝增长：</label></td>
					<td class="width-35">
						<form:input path="fansUp" htmlEscape="false"    class="form-control "/>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">持续时间：</label></td>
					<td class="width-35">
						<form:input path="duration" htmlEscape="false"    class="form-control "/>
					</td>
					<td class="width-15 active"><label class="pull-right">评价内容：</label></td>
					<td class="width-35">
						<form:input path="content" htmlEscape="false"    class="form-control "/>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">标签：</label></td>
					<td class="width-35">
						<form:input path="label" htmlEscape="false"    class="form-control "/>
					</td>
					<td class="width-15 active"><label class="pull-right">订单id：</label></td>
					<td class="width-35">
						<form:input path="orderId" value="${oldnewComment.orderId }" htmlEscape="false"  readonly="true"  class="form-control "/>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">评价人id：</label></td>
					<td class="width-35">
						<form:input path="userId" htmlEscape="false" value="${oldnewComment.userId }" readonly="true"  class="form-control "/>
					</td>
					<td class="width-15 active"><label class="pull-right">评价类型：</label></td>
					<td class="width-35">
						<form:select path="type" class="form-control ">
							<form:option value="3" label="经纪人评价"/>
							<%-- <form:options items="${fns:getDictList('oldnew_comment_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/> --%>
						</form:select>
					</td>
				</tr>
		 	</tbody>
		</table>
	</form:form>
</body>
</html>