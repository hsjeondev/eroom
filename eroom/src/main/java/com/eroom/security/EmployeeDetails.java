package com.eroom.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.eroom.employee.entity.Authority;
import com.eroom.employee.entity.Employee;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EmployeeDetails implements UserDetails {
	
private static final long serialVersionUID = 1L;
	
	private final Employee employee;
	
	public Employee getEmployee() {
		return employee;
	}

	// 사용자 권한 설정
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
	    List<GrantedAuthority> employeeAuthorities = new ArrayList<GrantedAuthority>();

	    for (Authority authority : employee.getAuthorities()) {
	        String roleName = authority.getAuthorityName();
	        employeeAuthorities.add(new SimpleGrantedAuthority(roleName));
	    }

	    return employeeAuthorities;
	}
	
	// 사용자 비밀번호 반환
	@Override
	public String getPassword() {
		return employee.getEmployeePw();
	}

	// 사용자 이름 반환
	@Override
	public String getUsername() {
		// 이메일, 아이디, 아이피 등등 쓸 수 있음
		return employee.getEmployeeId();
	}
	
	// 계정 사용 가능 여부
	@Override
	public boolean isEnabled() {
		boolean enabled = true;
		
		if(employee.getEmployeeEmploymentYn().equals("N")) {
			enabled =  false;
		}
		return enabled;
	}

	public Long getEmployeeNo() {
	   return employee.getEmployeeNo();
	}
}
