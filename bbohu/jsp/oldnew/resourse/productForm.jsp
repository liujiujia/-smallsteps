<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<c:set var="user" value="${fns:getUser()}" />
<html>
<head>
	<title>人气订单产品类型管理管理</title>
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
		function chage (a){
			if(a==0){
				$("#taoPrice").hide();
				$("#taoPrice1").hide();
				$("#dan3").show();
				$("#dan4").show();
				$("#miaoshu").show();
				$("#miaoshu1").show();
			}else{
				$("#taoPrice").show();
				$("#taoPrice1").show();
				$("#dan3").hide();
				$("#dan4").hide();
				$("#miaoshu").hide();
				$("#miaoshu1").hide();
			}
		}
	</script>
</head>
<body class="hideScroll">
		<form:form id="inputForm" modelAttribute="product" action="${ctx}/oldnew/resourse/product/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>	
		<table class="table table-bordered  table-condensed dataTables-example dataTable no-footer">
		   <tbody>
		   		<tr>
					<td class="width-15 active"><label class="pull-right">所属公会：</label></td>
					<td class="width-35">
						<c:choose>  
						   <c:when test="${user.isAdmin()}">
						   		<sys:treeselect id="company" name="company.id" value="${activity.company.id}" labelName="company.name" labelValue="${activity.company.name}"
									title="部门" url="/sys/office/treeData?type=1" cssClass="form-control required" allowClear="true" notAllowSelectParent="false"/>
						   </c:when> 
						   <c:otherwise> 
						   		<sys:treeselect id="company" name="company.id" value="${empty activity.company.id ? user.company.parentIds eq '0,'? user.office.id:user.company.id : activity.company.id}" labelName="company.name" labelValue="${empty activity.company.name ?  user.company.parentIds eq '0,'? user.office.name:user.company.name : activity.company.name}"
									title="部门" url="/sys/office/treeData?type=1" cssClass="form-control required" allowClear="true" disabled="disabled" notAllowSelectParent="true"/>
						   </c:otherwise>   
						</c:choose>
					</td>
					<td class="width-15 active"><label class="pull-right">经纪人(短信通知)：</label></td>
					<td class="width-35">
						<form:checkboxes path="contact" items="${list}" itemLabel="name" itemValue="id" htmlEscape="false" class="i-checks required"/> 
						<%-- <c:forEach items="${list }" var="u">
							<input type="checkbox" name="contact" value="${u.id }">${u.name }sdf
						</c:forEach> --%>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right"><font color="red">*</font>产品类型：</label></td>
					<td class="width-35">
						<form:select path="type" class="form-control required" onchange="chage(this.value)">
							<form:option value="" label="-----请选择产品类型-----"/>
							<form:option value="0" label="单项产品"/>
							<form:option value="1" label="套餐产品"/>
						</form:select>
					</td>
					<td class="width-15 active"><label class="pull-right"><font color="red">*</font>平台：</label></td>
					<td class="width-35">
						<form:select path="platform" class="form-control required">
							<form:option value="" label="-----请选择平台-----"/>
							<form:options items="${fns:getDictList('platform')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
						</form:select>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right"><font color="red">*</font>产品名称：</label></td>
					<td class="width-35">
						<form:input path="name" htmlEscape="false" class="form-control required"/>
					</td>
					<td class="width-15 active" id="dan3" style="display: none;"><label class="pull-right"><font color="red">*</font>分类比列：<span style="color: red">(请注意↓)</span></label></td>
					<td class="width-35" id="dan4" style="display: none;">
						<form:input path="proportion" htmlEscape="false"    class="form-control required"/>
					</td>
					<td class="width-15 active" id="taoPrice"><label class="pull-right"><font color="red">*</font>套餐价格：</label></td>
					<td class="width-35" id="taoPrice1">
						<form:input path="price" htmlEscape="false"    class="form-control required"/>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right"><font color="red">*</font>产品详情：</label></td>
					<td class="width-35">
						<form:textarea path="remarks" htmlEscape="false" rows="4"    class="form-control "/>
					</td>
					<td class="width-15 active"><label class="pull-right"><font color="red" id="miaoshu1" style="display: none;">比例详情</font></label></td>
					<td class="width-35">
						<span style="color: red;display: none;" id="miaoshu">例如1000粉丝等于10块钱，比例为100：1
									    500个评论等于3块钱，比例为500:3
									   （注意前后顺序）</span>
					</td>
				</tr>
		 	</tbody>
		</table>
	</form:form>
</body>
</html>