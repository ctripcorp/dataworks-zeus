$(function() {

	$('#switch_qlogin').click(
			function() {
				$('#switch_login').removeClass("switch_btn_focus").addClass(
						'switch_btn');
				$('#switch_qlogin').removeClass("switch_btn").addClass(
						'switch_btn_focus');
				$('#switch_bottom').animate({
					left : '0px',
					width : '70px'
				});
				$('#qlogin').css('display', 'none');
				$('#web_qr_login').css('display', 'block');

			});
	$('#switch_login').click(
			function() {

				$('#switch_login').removeClass("switch_btn").addClass(
						'switch_btn_focus');
				$('#switch_qlogin').removeClass("switch_btn_focus").addClass(
						'switch_btn');
				$('#switch_bottom').animate({
					left : '154px',
					width : '70px'
				});

				$('#qlogin').css('display', 'block');
				$('#web_qr_login').css('display', 'none');
			});
	if (getParam("a") == '0') {
		$('#switch_login').trigger('click');
	}

});

function logintab() {
	scrollTo(0);
	$('#switch_qlogin').removeClass("switch_btn_focus").addClass('switch_btn');
	$('#switch_login').removeClass("switch_btn").addClass('switch_btn_focus');
	$('#switch_bottom').animate({
		left : '154px',
		width : '96px'
	});
	$('#qlogin').css('display', 'none');
	$('#web_qr_login').css('display', 'block');

}

// 根据参数名获得该参数 pname等于想要的参数名
function getParam(pname) {
	var params = location.search.substr(1); // 获取参数 平且去掉？
	var ArrParam = params.split('&');
	if (ArrParam.length == 1) {
		// 只有一个参数的情况
		return params.split('=')[1];
	} else {
		// 多个参数参数的情况
		for (var i = 0; i < ArrParam.length; i++) {
			if (ArrParam[i].split('=')[0] == pname) {
				return ArrParam[i].split('=')[1];
			}
		}
	}
}
function validate(event) {
	var target = $(event.target), reg = target.attr("data-reg"), val = target
			.val(), status = true, disabled = false;
	if (reg) {
		switch (reg) {
		case 'email':
			if (!/^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/
					.test(val)) {
				status = false;
			}
			break;
		case 'noempty':
			if (val == '' || /^\s+$/.test(val)) {
				status = false;
			}
			break;
		case 'number':
			if (val == '' || !/^\d+$/.test(val)) {
				status = false;
			}
			break;
		}
	}
	if (!status) {
		target.removeClass('has-success').addClass('has-error');
	} else {
		target.removeClass('has-error').addClass('has-success');
	}

}

var reMethod = "GET", pwdmin = 6;

$(document)
		.ready(
				function() {
					$("#button").click(function() {
						var username = $("#username").val();
						var password = $("#password").val();
						if (username == "") {
							$("#username").addClass("has-error");
							return false;
						}
						if (password == "") {
							$("#password").addClass("has-error");
							return false;
						}
						$.ajax({
							url : "login.do",
							async : false,
							data : {
								username : username,
								password : password
							},
							type : "post",

							error : function(response) {
								alert("请求发送失败");

							},
							success : function(response) {
//								console.log(response);
								if (response == "null") {
									alert("用户名不存在");
								} else if (response == "error") {
									alert("账户错误");
								} else {
									window.location.href = '/zeus-web';
								}
							}
						});
					});

					$("#container input,textarea,select").on(
							'input propertychange', function(event) {
								validate(event);
							});
					$('#register')
							.click(
									function() {
										var user = $("#user").val();
										var passwd = $("#passwd").val();
										var email = $("#email").val();
										var phone = $("#phone").val();
										var userType = $('input[name="userTypes"]:checked').val();
										if ($('#user').val() == "") {
											$('#user').focus().css({
												border : "1px solid red",
												boxShadow : "0 0 2px red"
											});
											$('#userCue')
													.html(
															"<font color='red'><b>×用户名不能为空</b></font>");
											return false;
										}

										if ($('#user').val().length < 4
												|| $('#user').val().length > 16) {

											$('#user').focus().css({
												border : "1px solid red",
												boxShadow : "0 0 2px red"
											});
											$('#userCue')
													.html(
															"<font color='red'><b>×用户名位4-16字符</b></font>");
											return false;

										}
										/*$.ajax({
													type : reMethod,
													url : "/member/ajaxyz.php",
													data : "uid="
															+ $("#user").val()
															+ '&temp='
															+ new Date(),
													dataType : 'html',
													success : function(result) {

														if (result.length > 2) {
															$('#user')
																	.focus()
																	.css(
																			{
																				border : "1px solid red",
																				boxShadow : "0 0 2px red"
																			});
															$("#userCue").html(
																	result);
															return false;
														} else {
															$('#user')
																	.css(
																			{
																				border : "1px solid #D7D7D7",
																				boxShadow : "none"
																			});
														}

													}
												});*/

										if ($('#passwd').val().length < pwdmin) {
											$('#passwd').focus();
											$('#userCue').html(
													"<font color='red'><b>×密码不能小于"
															+ pwdmin
															+ "位</b></font>");
											return false;
										}
										if ($('#passwd2').val() != $('#passwd')
												.val()) {
											$('#passwd2').focus();
											$('#userCue')
													.html(
															"<font color='red'><b>×两次密码不一致！</b></font>");
											return false;
										}

										/*var sqq = /^[1-9]{1}[0-9]{4,9}$/;
										if (!sqq.test($('#qq').val())
												|| $('#qq').val().length < 5
												|| $('#qq').val().length > 12) {
											$('#qq').focus().css({
												border : "1px solid red",
												boxShadow : "0 0 2px red"
											});
											$('#userCue')
													.html(
															"<font color='red'><b>×QQ号码格式不正确</b></font>");
											return false;
										} else {
											$('#qq').css({
												border : "1px solid #D7D7D7",
												boxShadow : "none"
											});

										}

										$('#regUser').submit();*/
										$.ajax({
											url : "register.do",
											async : false,
											data : {
												user : user,
												passwd : passwd,
												email : email,
												phone : phone,
												userType : userType
											},
											type : "post",

											error : function(response) {
												alert("请求发送失败");

											},
											success : function(response) {
//												console.log(response);
												if (response == "exist") {
													$('#userCue')
													.html(
															"<font color='red'><b>用户名已经存在！</b></font>");
												} else if (response == "error") {
													$('#userCue')
													.html(
															"<font color='red'><b>用户注册失败！</b></font>");
												} else{
													$('#userCue')
													.html(
															"<font color='green'><b>用户注册成功！</b></font>");
													$("#user").val("");
													$("#passwd").val("");
													$("#passwd2").val("");
													$("#email").val("");
													$("#phone").val("");
												}
											}
										});
									});
				});