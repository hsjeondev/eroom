package com.eroom.project.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eroom.employee.entity.Employee;
import com.eroom.employee.repository.EmployeeRepository;
import com.eroom.project.dto.ProjectTodoElementDto;
import com.eroom.project.dto.ProjectTodoListDto;
import com.eroom.project.entity.ProjectTodoElement;
import com.eroom.project.entity.ProjectTodoList;
import com.eroom.project.repository.ProjectTodoElementRepository;
import com.eroom.project.repository.ProjectTodoListRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectTodoService {
	
	private final ProjectTodoListRepository projectTodoListRepository;
	private final ProjectTodoElementRepository projectTodoElementRepository;
	private final EmployeeRepository employeeRepository;
	
	public List<ProjectTodoListDto> findByProjectNo(Long projectNo) {
	    
	    List<ProjectTodoList> list = projectTodoListRepository
	            .findWithElementsByProjectNo(projectNo);

	    List<ProjectTodoListDto> dtolist = new ArrayList<>();

	    for (ProjectTodoList projectTodoList : list) {
	        ProjectTodoListDto dto = new ProjectTodoListDto().toDto(projectTodoList);
	        dtolist.add(dto);
	    }

	    return dtolist;
	}


	
	@Transactional()
	public int addList(Long projectNo, String listName, String listColor, String position, String standardListId) {
		int result = 0;
		
		try {
			List<ProjectTodoList> projectTodoLists = projectTodoListRepository.findByProjectNoOrderByListSequenceAsc(projectNo);
			
			if(projectTodoLists.isEmpty()) {
				ProjectTodoList newProjectTodoList = ProjectTodoList
														.builder()
														.projectNo(projectNo)
														.listName(listName)
														.listColor(listColor)
														.listSequence(1)
														.build();
				projectTodoListRepository.save(newProjectTodoList);
			} else {
				Long stdId = Long.valueOf(standardListId);
				int stdSeq = -1;
				for (ProjectTodoList item : projectTodoLists) {
				    if (item.getProjectTodoListNo().equals(stdId)) {
				        stdSeq = item.getListSequence();
				        break;
				    }
				}

				// (만약 못 찾았다면 비어있거나 꼬인 상황이니, 새 항목을 맨 끝에 넣어버려도 됩니다)
				// 예: stdSeq == -1 이면 새 시퀀스를  projectTodoLists.size() + 1 로
				if (stdSeq == -1) {
				    stdSeq = projectTodoLists.size() > 0
				           ? projectTodoLists.get(projectTodoLists.size() - 1).getListSequence()
				           : 0;
				}

				// 2) 새 항목의 시퀀스 계산
				int pos = Integer.parseInt(position);  // 1(앞), -1(뒤)
				int newSeq = stdSeq + (pos == 1 ? 0 : 1);

				// 3) 기존 항목들 시퀀스 밀어주기
				for (ProjectTodoList item : projectTodoLists) {
				    int seq = item.getListSequence();
				    if (pos == 1) {
				        // 앞에 삽입: 기준 stdSeq 와 같거나 큰 것부터 +1
				        if (seq >= stdSeq) {
				            item.setListSequence(seq + 1);
				            projectTodoListRepository.save(item);
				        }
				    } else {
				        // 뒤에 삽입: 기준 stdSeq 보다 큰 것부터 +1
				        if (seq > stdSeq) {
				            item.setListSequence(seq + 1);
				            projectTodoListRepository.save(item);
				        }
				    }
				}

				// 4) 새 항목 저장
				ProjectTodoList newItem = ProjectTodoList.builder()
				    .projectNo(projectNo)
				    .listName(listName)
				    .listColor(listColor)
				    .listSequence(newSeq)
				    .build();
				projectTodoListRepository.save(newItem);
			}
			
			result = 1;
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	@Transactional
	public int updateElementPosition(Long listNo, Long todoNo, int newIndex) {
		int result = 0;
		
	    try {
	    	// 1. todoNo로 TodoElement 조회
		    ProjectTodoElement element = projectTodoElementRepository.findById(todoNo)
		        .orElseThrow(() -> new IllegalArgumentException("할 일 항목을 찾을 수 없습니다."));

		    // 2. listNo로 TodoList 조회
		    ProjectTodoList newList = projectTodoListRepository.findById(listNo)
		        .orElseThrow(() -> new IllegalArgumentException("리스트를 찾을 수 없습니다."));

		    // 3. 해당 list의 element 목록 가져오기 (순서 있는 상태)
		    List<ProjectTodoElement> elements = projectTodoElementRepository.findByProjectTodoListOrderByElementSequenceAsc(newList);

		    // 4. 현재 요소를 리스트에서 빼고
		    elements.removeIf(e -> e.getTodoNo().equals(todoNo));

		    // 5. newIndex에 끼워넣기
		    elements.add(newIndex, element);

		    // 6. 리스트와 시퀀스를 새로 세팅
		    for (int i = 0; i < elements.size(); i++) {
		        ProjectTodoElement e = elements.get(i);
		        e.setProjectTodoList(newList); // 리스트가 이동했을 수도 있어서
		        e.setElementSequence(i);
		    }
		    
		    // 7. 한번에 저장
		    projectTodoElementRepository.saveAll(elements);
		    
		    result = 1;
	    } catch(Exception e) {
	    	e.printStackTrace();
	    }
	    
	    return result;
	}
	
	public int createTodoElement(ProjectTodoElementDto dto) {
	    int result = 0;

	    try {
	        // 1. 반드시 ProjectTodoList Entity 조회
	        ProjectTodoList projectTodoList = projectTodoListRepository.findById(dto.getProject_todo_list_no())
	            .orElseThrow(() -> new IllegalArgumentException("ProjectTodoList not found"));

	        // 2. employee도 Entity로 조회
	        Employee employee = employeeRepository.findById(dto.getEmployee_no())
	            .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

	        // 3. element_sequence 구하기
	        Integer maxSequence = projectTodoElementRepository.findMaxElementSequence(projectTodoList.getProjectTodoListNo());
	        int newSequence = (maxSequence != null) ? maxSequence + 1 : 1;

	        // 4. entity 빌더로 생성
	        ProjectTodoElement projectElement = ProjectTodoElement.builder()
	            .projectTodoList(projectTodoList)
	            .todoTitle(dto.getTodo_title())
	            .employee(employee)
	            .elementSequence(newSequence)
	            .emergency(dto.getEmergency() == null ? "N" : dto.getEmergency())
	            .build();

	        // 5. 저장
	        projectTodoElementRepository.save(projectElement);

	        result = 1;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return result;
	}




	
}
