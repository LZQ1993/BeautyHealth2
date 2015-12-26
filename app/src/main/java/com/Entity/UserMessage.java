package com.Entity;

import com.infrastructure.CWDomain.EntityBase;

public class UserMessage extends EntityBase{
	public String UserID;//用户手机号
	public String UUID;//用户的注册码
	public String Password;//用户密码
	public String UserNetPassword;//验证码
	public String PasswordType;//表示密码类型，1表示验证码，2表示用户密码
	public String UserRealName;//用户的真实姓名
	public String UserSex;
	public String UserTel;//用户的其它电话
	public String UserBirthday;
	public String UserAge;
	public String UserAddress;
}
