<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>主播级别管理</title>
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
		<form:form id="inputForm" modelAttribute="anchorLevel" action="${ctx}/oldnew/config/anchorLevel/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>	
		<table class="table table-bordered  table-condensed dataTables-example dataTable no-footer">
		   <tbody>
				<tr>
					<td class="width-15 active"><label class="pull-right">备注信息：</label></td>
					<td class="width-35">
						<form:textarea path="remarks" htmlEscape="false" rows="4"    class="form-control "/>
					</td>
					<td class="width-15 active"><label class="pull-right"><font color="red">*</font>级别名称：</label></td>
					<td class="width-35">
						<form:input path="name" htmlEscape="false"    class="form-control required"/>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">说明：</label></td>
					<td class="width-35">
						<form:input path="note" htmlEscape="false" maxlength="150"    class="form-control "/>
					</td>
					<td class="width-15 active"><label class="pull-right"><font color="red">*</font>最小粉丝数：</label></td>
					<td class="width-35">
						<form:input path="minFansNum" htmlEscape="false" maxlength="9"    class="form-control required number"/>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">最大粉丝数：</label></td>
					<td class="width-35">
						<form:input path="maxFansNum" htmlEscape="false" maxlength="9"    class="form-control number"/>
					</td>
					<td class="width-15 active"><label class="pull-right"><font color="red">*</font>主播级别（级别越大粉丝越多）：</label></td>
					<td class="width-35">
						<form:input path="level" htmlEscape="false"    class="form-control required"/>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right"><font color="red">*</font>带新价格：</label></td>
					<td class="width-35">
						<form:input path="takePrice" htmlEscape="false" maxlength="9"    class="form-control required number"/>
					</td>
					<td class="width-15 active"><label class="pull-right"><font color="red">*</font>带新次数：</label></td>
					<td class="width-35">
						<form:input path="takeNum" htmlEscape="false" maxlength="9"    class="form-control required number"/>
					</td>
				</tr>
		 	</tbody>
		</table>
	</form:form>
</body>
</html>