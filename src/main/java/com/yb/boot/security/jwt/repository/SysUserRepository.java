package com.yb.boot.security.jwt.repository;

import com.yb.boot.security.jwt.model.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author yangbiao
 * @Description:
 * @date 2018/11/30
 */
public interface SysUserRepository extends JpaRepository<SysUser,String>, JpaSpecificationExecutor<SysUser> {

    SysUser findByUsernameAndUserFrom(String username, String from);
}
