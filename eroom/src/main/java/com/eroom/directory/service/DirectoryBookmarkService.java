package com.eroom.directory.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.eroom.directory.dto.DirectoryBookmarkDto;
import com.eroom.directory.dto.DirectoryMemoDto;
import com.eroom.directory.entity.Directory;
import com.eroom.directory.entity.DirectoryBookmark;
import com.eroom.directory.entity.DirectoryMemo;
import com.eroom.directory.repository.DirectoryBookmarkRepository;
import com.eroom.directory.repository.DirectoryRepository;
import com.eroom.employee.entity.Employee;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DirectoryBookmarkService {
	
	private final DirectoryBookmarkRepository directoryBookmarkRepository;
	private final DirectoryRepository directoryRepository;
	
	public List<DirectoryBookmark> selectTargetBookmark(Long employeeNo, Long id) {
		List<DirectoryBookmark> directoryBookmark = directoryBookmarkRepository.findByEmployee_employeeNoAndDirectory_Employee_employeeNoAndVisibleYn(employeeNo, id, "Y");
		if(directoryBookmark != null) {
			return directoryBookmark;
		} else {
			return null;
		}
	}

	public DirectoryBookmark changeBookmark(Employee employee, DirectoryBookmarkDto dto) {
		DirectoryBookmark result = null;
		try {
			Directory directoryEntity = directoryRepository.findById(dto.getDirectory_no()).orElse(null);
			List<DirectoryBookmark> directoryBookmark = directoryBookmarkRepository.findByEmployee_employeeNoAndDirectory_Employee_employeeNoAndVisibleYn(employee.getEmployeeNo(), directoryEntity.getEmployee().getEmployeeNo(), "Y");
			DirectoryBookmark directoryBookmarkEntity = new DirectoryBookmark();
			if(directoryBookmark.isEmpty() && directoryEntity != null) {
				// 북마크가 없는 경우
				directoryBookmarkEntity = DirectoryBookmark.builder()
											.directory(directoryEntity)
											.directoryBookmarkYn("Y")
											.directoryBookmarkCreator(employee.getEmployeeId())
											.employee(employee)
											.build();
			} else {
				// 북마크가 있는 경우
				directoryBookmarkEntity = directoryBookmark.get(directoryBookmark.size() - 1);
				String beforeBookmark = directoryBookmarkEntity.getDirectoryBookmarkYn();
				if(beforeBookmark.equals("Y")) {
					// Y -> N
					directoryBookmarkEntity.setDirectoryBookmarkYn("N");
				} else {
					// N -> Y
					directoryBookmarkEntity.setDirectoryBookmarkYn("Y");
				}
				directoryBookmarkEntity.setDirectoryBookmarkEditor(employee.getEmployeeId());
			}
			DirectoryBookmark saved = directoryBookmarkRepository.save(directoryBookmarkEntity);
			result = saved;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	// 북마크 우선 조회 기능
	public String findBookmarkYnByEmployeeNo(Long employeeNo, Long id) {
		String result = null;
		List<DirectoryBookmark> directoryBookmarkList = directoryBookmarkRepository.findByEmployee_employeeNoAndDirectory_Employee_employeeNoAndVisibleYnAndDirectoryBookmarkYn(employeeNo, id, "Y", "Y");
		if(directoryBookmarkList.isEmpty()) {
			return null;
		} else {
			DirectoryBookmark directoryBookmark = directoryBookmarkList.get(directoryBookmarkList.size() - 1);
			result = directoryBookmark.getDirectoryBookmarkYn();
		}
		return result;
	}

}
