package com.eroom.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.eroom.employee.entity.Employee;
import com.eroom.employee.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeDetailsService implements UserDetailsService {
	
	private final EmployeeRepository employeeRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Employee employee = employeeRepository.findByEmployeeId(username).orElse(null);
		
		if(employee == null) {
			throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: ");
		}
		
		return new EmployeeDetails(employee);
	}
}
