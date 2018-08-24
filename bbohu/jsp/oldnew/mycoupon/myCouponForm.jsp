<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>我的优惠券管理</title>
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
			laydate({
	            elem: '#getime', //目标元素。由于laydate.js封装了一个轻量级的选择器引擎，因此elem还允许你传入class、tag但必须按照这种方式 '#id .class'
	            event: 'focus' //响应事件。如果没有传入event，则按照默认的click
	        });
			laydate({
	            elem: '#disableDate', //目标元素。由于laydate.js封装了一个轻量级的选择器引擎，因此elem还允许你传入class、tag但必须按照这种方式 '#id .class'
	            event: 'focus' //响应事件。如果没有传入event，则按照默认的click
	        });
		});
	</script>
</head>
<body class="hideScroll">
		<form:form id="inputForm" modelAttribute="myCoupon" action="${ctx}/oldnew/mycoupon/myCoupon/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>	
		<table class="table table-bordered  table-condensed dataTables-example dataTable no-footer">
		   <tbody>
				<tr>
					<td class="width-15 active"><label class="pull-right">优惠券名称：</label></td>
					<td class="width-35">
						<form:input path="name" htmlEscape="false" maxlength="50"    class="form-control "/>
					</td>
					<td class="width-15 active"><label class="pull-right">优惠券价格：</label></td>
					<td class="width-35">
						<form:input path="price" htmlEscape="false"    class="form-control  number"/>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">获取时间：</label></td>
					<td class="width-35">
						<input id="getime" name="getime" type="text" maxlength="20" class="laydate-icon form-control layer-date "
							value="<fmt:formatDate value="${myCoupon.getime}" pattern="yyyy-MM-dd HH:mm:ss"/>"/>
					</td>
					<td class="width-15 active"><label class="pull-right">截止时间：</label></td>
					<td class="width-35">
						<input id="disableDate" name="disableDate" type="text" maxlength="20" class="laydate-icon form-control layer-date "
							value="<fmt:formatDate value="${myCoupon.disableDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"/>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">优惠券状态：</label></td>
					<td class="width-35">
						<form:select path="status" class="form-control ">
							<form:option value="" label=""/>
							<form:options items="${fns:getDictList('is_used')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
						</form:select>
					</td>
					<td class="width-15 active"><label class="pull-right">用户：</label></td>
					<td class="width-35">
						<form:select path="userId" class="form-control required">
							<form:option value="" label=""/>
							<form:options items="${user }" itemLabel="name" itemValue="userId" htmlEscape="false"/>
						</form:select>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">优惠券类型：</label></td>
					<td class="width-35">
						<%-- <form:input path="couponType" htmlEscape="false"    class="form-control  number"/> --%>
						<c:if test="${not empty myCoupon.couponType}">
							<c:if test="${myCoupon.couponType == 1 }">
								<select name="couponType" class="form-control ">
									<option value="1">订单优惠券</option>
								</select>
							</c:if>
							<c:if test="${myCoupon.couponType == 2 }">
								<select name="couponType" class="form-control ">
									<option value="2">资源优惠券</option>
								</select>
							</c:if>
						</c:if>
						<c:if test="${empty myCoupon.couponType }">
							<select name="couponType" class="form-control ">
								<option value="">----请选择优惠券类型----</option>
								<option value="1">订单优惠券</option>
								<option value="2">资源优惠券</option>
							</select>
						</c:if>
					</td>
					<td class="width-15 active"><label class="pull-right">备注信息：</label></td>
					<td class="width-35">
						<form:textarea path="remarks" htmlEscape="false" rows="4"    class="form-control "/>
					</td>
		  		</tr>
		 	</tbody>
		</table>
	</form:form>
</body>
</html>