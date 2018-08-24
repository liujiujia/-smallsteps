<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>订单管理管理</title>
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
		<form:form id="inputForm" modelAttribute="oldnewOrder" action="${ctx}/oldnew/order/oldnewOrder/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>	
		<table class="table table-bordered  table-condensed dataTables-example dataTable no-footer">
		   <tbody>
				<tr>
					<td class="width-15 active"><label class="pull-right">订单编号：</label></td>
					<td class="width-35">
						<form:input path="orderCode" htmlEscape="false"    class="form-control "/>
					</td>
					<td class="width-15 active"><label class="pull-right">订单价格：</label></td>
					<td class="width-35">
						<fmt:formatNumber value="${oldnewOrder.orderPrice}" pattern='#,##0.00#'/>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">优惠金额：</label></td>
					<td class="width-35">
						<fmt:formatNumber value="${oldnewOrder.reduceMoney}" pattern='#,##0.00#'/>
					</td>
					<td class="width-15 active"><label class="pull-right">应付金额：</label></td>
					<td class="width-35">
						<fmt:formatNumber value="${oldnewOrder.actualMoney}" pattern='#,##0.00#'/>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">订单状态：</label></td>
					<td class="width-35">
						<form:select path="status" class="form-control ">
							<form:option value="" label=""/>
							<form:options items="${fns:getDictList('order_status')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
						</form:select>
					</td>
					<td class="width-15 active"><label class="pull-right">信息发布ID：</label></td>
					<td class="width-35">
						<form:input path="messageid" htmlEscape="false"    class="form-control "/>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">信息发布人：</label></td>
					<td class="width-35">
						<form:input path="publisherName" htmlEscape="false"    class="form-control "/>
					</td>
					<td class="width-15 active"><label class="pull-right">信息接单人：</label></td>
					<td class="width-35">
						<form:input path="takerName" htmlEscape="false"    class="form-control "/>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">接单时间：</label></td>
					<td class="width-35">
						<fmt:formatDate value="${oldnewOrder.receiveDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
					</td>
					<td class="width-15 active"><label class="pull-right">确认时间：</label></td>
					<td class="width-35">
						<fmt:formatDate value="${oldnewOrder.affirmDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">订单类型：</label></td>
					<td class="width-35">
						<c:choose>
							<c:when test="${oldnewOrder.orgerType == '1' }">求带订单</c:when>
							<c:otherwise>带人订单</c:otherwise>
						</c:choose>
					</td>
					<td class="width-15 active"><label class="pull-right">完成时间：</label></td>
					<td class="width-35">
						<fmt:formatDate value="${oldnewOrder.overDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
					</td>
				</tr>
		 	</tbody>
		</table>
	</form:form>
	<div class="panel panel-default">
		<div class="panel-heading">
			<h4 class="panel-title">
				<a data-toggle="collapse" data-parent="#accordion" 
				   href="#collapseTwo" >
					<!-- 查看考试记录(已学完) -->
					<button value="${oldnewOrder.id}" onclick="getComment(this.value)">查看评论</button>
				</a>
			</h4>
		</div>
		<div id="collapseTwo" class="panel-collapse collapse">
			<div class="panel-body" id="neirong">
			</div>
		</div>
	</div>
	<script type="text/javascript">
		function getComment(a) {
			$.ajax({  
		            url:"${ctx}/oldnew/order/oldnewOrder/getComment",  
		            type:"post",  
		            data:{id:a},  
		            dataType:"JSON",
		            success:function(res){ 
		            	//data = $.parseJSON(res);
		            	var ress = eval(res.body.comment);
		            	 $('#neirong').empty();
		            	 if (ress==0) {
								$("#neirong").append("该订单还没有评论!");
						}
		            	for(var i=0;i<ress.length;i++){  
		                     //访问每一个的属性，根据属性拿到值  
		                         //将拿到的值显示到jsp页面  neirong
		                        $('#neirong').append("评价人Id:"+ress[i].userId+"满意度:"+ress[i].satisfyLevel
		                        		+"粉丝增长:"+ress[i].fansUp+"持续时间:"+ress[i].duration+"<br/>");
		                    }
		            } 
		        })
		}
		
	</script>
</body>

</html>