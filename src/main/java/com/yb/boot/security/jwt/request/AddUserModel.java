package com.yb.boot.security.jwt.request;

import com.yb.boot.security.jwt.common.CommonDic;
import com.yb.boot.security.jwt.exception.ParameterErrorException;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Basic;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * Description:用户添加信息封装类
 * author yangbiao
 * date 2018/12/12
 */
public class AddUserModel {

    /**
     * 用户名
     */
    @Length(max = 50, message = "用户id过长")
    private String id;

    /**
     * 用户名
     */
    @Length(max = 20, message = "用户名不能大于20字")
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 用户部门
     */
    @Length(max = 25, message = "用户部门不能大于25字")
    private String department;

    /**
     * 用户职位
     */
    @Length(max = 25, message = "用户职位不能大于25字")
    private String position;

    /**
     * 用户电话
     */
    @NotBlank(message = "电话不能为空")
    @Pattern(regexp = "^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$", message = "电话有误")
    private String phone;

    /**
     * 用户类型(前台或后台等)
     */
    @Length(max = 10, message = "用户类型长度过长")
    @NotBlank(message = "用户类型不能为空")
    private String from;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (StringUtils.isNotBlank(username)) {
            username = username.trim();
        }
        this.username = username;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        if (StringUtils.isNotBlank(department)) {
            department = department.trim();
        }
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        if (StringUtils.isNotBlank(position)) {
            position = position.trim();
        }
        this.position = position;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        if (StringUtils.isNotBlank(phone)) {
            phone = phone.trim();
        }
        this.phone = phone;
    }

    public String getFrom() {
        if ("1".equals(this.from) || "前台".equals(this.from)) {
            return CommonDic.FROM_FRONT;
        } else if ("2".equals(this.from) || "后台".equals(this.from)) {
            return CommonDic.FROM_BACK;
        } else {
            ParameterErrorException.message("未知的用户类型");
        }
        return null;
    }

    public void setFrom(String from) {
        if (StringUtils.isNotBlank(from)) {
            from = from.trim();
        }
        this.from = from;
    }
}
