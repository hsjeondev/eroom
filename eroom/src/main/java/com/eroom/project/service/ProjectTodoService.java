package com.eroom.project.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eroom.employee.entity.Employee;
import com.eroom.employee.repository.EmployeeRepository;
import com.eroom.project.dto.EditTodoDetailRequest;
import com.eroom.project.dto.ProjectTodoElementDto;
import com.eroom.project.dto.ProjectTodoListDto;
import com.eroom.project.entity.ProjectTodoElement;
import com.eroom.project.entity.ProjectTodoElementDetail;
import com.eroom.project.entity.ProjectTodoList;
import com.eroom.project.repository.ProjectTodoElementDetailRepository;
import com.eroom.project.repository.ProjectTodoElementRepository;
import com.eroom.project.repository.ProjectTodoListRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectTodoService {
	
	private final ProjectTodoListRepository projectTodoListRepository;
	private final ProjectTodoElementRepository projectTodoElementRepository;
	private final EmployeeRepository employeeRepository;
	private final ProjectTodoElementDetailRepository projectTodoElementDetailRepository;
	
	@Transactional
	public int softDeleteTodoElement(Long todoNo) {
		int result = 0;
	    try {
	        ProjectTodoElement element = projectTodoElementRepository.findById(todoNo).orElse(null);
	        if (element != null) {
	        	element.setVisibleYn("N");
		        projectTodoElementRepository.save(element);

		        result = 1;
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    return result;
	}

	
	@Transactional
	public int updateTodoElement(Long todoNo, String todoTitle, Long listNo, Long employeeNo, String emergency) {
		int result = 0;
		
	    try {
	        ProjectTodoElement target = projectTodoElementRepository.findById(todoNo).orElse(null);
	        
	        if(target != null) {
	        	target.setTodoTitle(todoTitle);

		        // 리스트와 직원 정보도 수정
		        ProjectTodoList todoList = projectTodoListRepository.findById(listNo).orElse(null);
		        Employee employee = employeeRepository.findById(employeeNo).orElse(null);
		        if (todoList != null) target.setProjectTodoList(todoList);
		        if (employee != null) target.setEmployee(employee);

		        // 긴급 여부 (null이면 "N" 처리)
		        target.setEmergency("Y".equals(emergency) ? "Y" : "N");

		        projectTodoElementRepository.save(target);
		        result = 1;
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    return result;
	}

	
	public Map<String, Object> findTodoElementOne(Long todoElementNo) {
	    ProjectTodoElement entity = projectTodoElementRepository.findById(todoElementNo)
	        .orElse(null);
	    
	    Map<String, Object> result = new HashMap<>();

	    if (entity != null) {

	    result.put("todo_no", entity.getTodoNo());
	    result.put("project_todo_list_no", entity.getProjectTodoList().getProjectTodoListNo());
	    result.put("employee_no", entity.getEmployee().getEmployeeNo());
	    result.put("todo_title", entity.getTodoTitle());
	    result.put("emergency", entity.getEmergency());
	    
	    }

	    return result;
	}


	
	public int deleteListOne(Long projectTodoListNo) {
		int result = 0;
		
		try {
			ProjectTodoList projectTodoList = projectTodoListRepository.findById(projectTodoListNo).orElse(null);
			
			if(projectTodoList != null) {
				projectTodoList.setVisibleYn("N");
				projectTodoListRepository.save(projectTodoList);
			}
			
			result = 1;
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
		return result;
	}
	
	@Transactional
	public int updateList(Long projectTodoListNo, Long projectNo, String listName, String listColor,
	                      String position, String standardListId) {
	    int result = 0;

	    try {
	        ProjectTodoList target = projectTodoListRepository.findById(projectTodoListNo).orElse(null);
	        if (target == null) return 0;

	        target.setListName(listName);
	        target.setListColor(listColor);

	        if (position == null || standardListId == null) {
	            projectTodoListRepository.save(target);
	            return 1;
	        }

	        // ✅ 수정된 부분: visibleYn = 'Y' 조건 추가
	        List<ProjectTodoList> todoList = projectTodoListRepository
	                .findByProjectNoAndVisibleYnOrderByListSequenceAsc(projectNo, "Y");

	        Long stdId = Long.valueOf(standardListId);
	        int stdSeq = -1;

	        for (ProjectTodoList item : todoList) {
	            if (item.getProjectTodoListNo().equals(stdId)) {
	                stdSeq = item.getListSequence();
	                break;
	            }
	        }

	        int oldSeq = target.getListSequence();
	        int stdPos = Integer.parseInt(position);
	        int newSeq = (stdPos == 1) ? stdSeq : (stdSeq < oldSeq ? stdSeq + 1 : stdSeq);

	        if (newSeq == oldSeq) {
	            projectTodoListRepository.save(target);
	            return 1;
	        }

	        for (ProjectTodoList item : todoList) {
	            if (item.getProjectTodoListNo().equals(projectTodoListNo)) continue;

	            int seq = item.getListSequence();

	            if (newSeq < oldSeq) {
	                if (seq >= newSeq && seq < oldSeq) {
	                    item.setListSequence(seq + 1);
	                    projectTodoListRepository.save(item);
	                }
	            } else {
	                if (seq > oldSeq && seq <= newSeq) {
	                    item.setListSequence(seq - 1);
	                    projectTodoListRepository.save(item);
	                }
	            }
	        }

	        target.setListSequence(newSeq);
	        projectTodoListRepository.save(target);

	        result = 1;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return result;
	}






	
	
	public Map<String, String> findProjectTodoListOne(Long projectTodoListNo) {
	    Map<String, String> map = new HashMap<>();

	    Optional<ProjectTodoList> optionalList = projectTodoListRepository.findById(projectTodoListNo);
	    if (optionalList.isEmpty()) return map;

	    ProjectTodoList projectTodoList = optionalList.get();

	    map.put("project_todo_list_no", String.valueOf(projectTodoList.getProjectTodoListNo()));
	    map.put("list_name", projectTodoList.getListName());
	    map.put("list_color", projectTodoList.getListColor());

	    int currentSeq = projectTodoList.getListSequence();
	    Long projectNo = projectTodoList.getProjectNo();

	    System.out.println("▶ projectNo: " + projectNo + ", currentSeq: " + currentSeq);

	    if (currentSeq == 0) {
	        // 처음이면 다음 리스트 중 가장 가까운 걸 찾음
	        List<ProjectTodoList> nextList = projectTodoListRepository.findNextList(projectNo, currentSeq);
	        System.out.println("🔍 다음 리스트 개수: " + nextList.size());

	        if (!nextList.isEmpty()) {
	            ProjectTodoList next = nextList.get(0);
	            System.out.println("✅ 다음 리스트: " + next.getListName() + " (seq=" + next.getListSequence() + ")");
	            map.put("position", "before");
	            map.put("relative_project_todo_list_no", String.valueOf(next.getProjectTodoListNo()));
	            map.put("relative_list_name", next.getListName());
	        } else {
	            System.out.println("❌ 다음 리스트 없음");
	            map.put("position", "none");
	        }
	    } else {
	        // 이전 리스트 먼저 시도
	        List<ProjectTodoList> prevList = projectTodoListRepository.findPrevList(projectNo, currentSeq);
	        System.out.println("🔍 이전 리스트 개수: " + prevList.size());

	        if (!prevList.isEmpty()) {
	            ProjectTodoList prev = prevList.get(0);
	            System.out.println("✅ 이전 리스트: " + prev.getListName() + " (seq=" + prev.getListSequence() + ")");
	            map.put("position", "after");
	            map.put("relative_project_todo_list_no", String.valueOf(prev.getProjectTodoListNo()));
	            map.put("relative_list_name", prev.getListName());
	        } else {
	            System.out.println("❗ 이전 리스트 없음 → 다음 리스트 시도");

	            List<ProjectTodoList> nextList = projectTodoListRepository.findNextList(projectNo, currentSeq);
	            System.out.println("🔍 다음 리스트 개수: " + nextList.size());

	            if (!nextList.isEmpty()) {
	                ProjectTodoList next = nextList.get(0);
	                System.out.println("✅ 다음 리스트: " + next.getListName() + " (seq=" + next.getListSequence() + ")");
	                map.put("position", "before");
	                map.put("relative_project_todo_list_no", String.valueOf(next.getProjectTodoListNo()));
	                map.put("relative_list_name", next.getListName());
	            } else {
	                System.out.println("❌ 앞뒤 리스트 모두 없음");
	                map.put("position", "none");
	            }
	        }
	    }

	    return map;
	}





	
	@Transactional
	public void editTodoElementDetails(EditTodoDetailRequest request) {
	    
	    Long todoElementNo = request.getTodoElementNo();
	    Long projectNo = request.getProjectNo();
	    List<EditTodoDetailRequest.DetailDto> details = request.getDetails();

	    if (todoElementNo == null || projectNo == null || details == null) {
	        throw new IllegalArgumentException("요청 데이터가 불완전합니다.");
	    }

	    // 1. 현재 todoElement에 연결된 세부 할 일들 다 가져온다 (visibleYn = 'Y'인 것만)
	    List<ProjectTodoElementDetail> existingDetails = projectTodoElementDetailRepository
	            .findByProjectTodoElement_TodoNoAndVisibleYn(todoElementNo, "Y");

	    // 2. 기존 세부 할 일 map으로 만들어놓자 (key: detailNo, value: 엔티티)
	    Map<Long, ProjectTodoElementDetail> existingDetailMap = existingDetails.stream()
	        .collect(Collectors.toMap(ProjectTodoElementDetail::getTodoDetailNo, detail -> detail));

	    // 3. 새로 받은 요청을 하나하나 비교해서 처리
	    for (EditTodoDetailRequest.DetailDto incomingDetail : details) {
	        Long detailNo = incomingDetail.getTodoDetailNo();
	        String todoContent = incomingDetail.getTodoContent();

	        if (detailNo != null) {
	            // 3-1. 기존 detail 수정 (내용 업데이트)
	            ProjectTodoElementDetail existing = existingDetailMap.remove(detailNo);
	            if (existing != null) {
	                existing.setTodoContent(todoContent);
	                projectTodoElementDetailRepository.save(existing);
	            }
	        } else {
	            // 3-2. 새로 추가되는 detail (Insert)
	            ProjectTodoElementDetail newDetail = ProjectTodoElementDetail.builder()
	                .projectNo(projectNo)
	                .projectTodoElement(ProjectTodoElement.builder().todoNo(todoElementNo).build())  // FK 연결
	                .todoContent(todoContent)
	                .status("N") // 기본값: 미완료
	                .visibleYn("Y")
	                .build();
	            projectTodoElementDetailRepository.save(newDetail);
	        }
	    }

	    // 4. 요청에 없는 detail은 삭제 처리 (visibleYn = 'N')
	    for (ProjectTodoElementDetail remaining : existingDetailMap.values()) {
	        remaining.setVisibleYn("N");
	        projectTodoElementDetailRepository.save(remaining);
	    }
	}



	
	public Map<String, Object> updateElementDetailStatus(Long todoDetailNo, String status) {
	    Map<String, Object> result = new HashMap<>();

	    try {
	        ProjectTodoElementDetail projectTodoElementDetail = projectTodoElementDetailRepository.findById(todoDetailNo).orElse(null);

	        if (projectTodoElementDetail != null) {
	            projectTodoElementDetail.setStatus(status);
	            projectTodoElementDetailRepository.save(projectTodoElementDetail);

	            Long todoElementNo = projectTodoElementDetail.getProjectTodoElement().getTodoNo();

	            // ✅ visibleYn = 'Y' 조건 추가해서 카운트
	            int totalCount = projectTodoElementDetailRepository.countByProjectTodoElement_TodoNoAndVisibleYn(todoElementNo, "Y");
	            int completedCount = projectTodoElementDetailRepository.countByProjectTodoElement_TodoNoAndStatusAndVisibleYn(todoElementNo, "Y", "Y");

	            result.put("todo_element_no", todoElementNo);
	            result.put("progress_text", completedCount + "/" + totalCount);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return result;
	}


	
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
	
	public List<ProjectTodoListDto> findByProjectNoWithElementCount(Long projectNo) {
	    List<Object[]> results = projectTodoListRepository.findListWithElementCountByProjectNo(projectNo);
	    List<ProjectTodoListDto> dtos = new ArrayList<>();

	    for (Object[] row : results) {
	        ProjectTodoList list = (ProjectTodoList) row[0];
	        Long count = (Long) row[1];

	        ProjectTodoListDto dto = new ProjectTodoListDto().toDto(list);
	        dto.setTodoElementCount(count.intValue()); // count 필드 직접 세팅
	        dtos.add(dto);
	    }

	    return dtos;
	}



	
	@Transactional
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
														.visibleYn("Y")
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
				    .visibleYn("Y")
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
	            .visibleYn("Y")
	            .build();

	        // 5. 저장
	        projectTodoElementRepository.save(projectElement);

	        result = 1;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return result;
	}

	public ProjectTodoElementDto findTodoElement(Long todoElementNo) {
		ProjectTodoElement projectTodoElement = projectTodoElementRepository.findById(todoElementNo).orElse(null);
		ProjectTodoElementDto projectTodoElementDto = null;
		
		if(projectTodoElement != null) {
			projectTodoElementDto = new ProjectTodoElementDto().toDto(projectTodoElement);
		}
		
		return projectTodoElementDto;
	}

	public int countVisibleDetails(Long todoElementNo) {
	    return projectTodoElementDetailRepository.countByProjectTodoElement_TodoNoAndVisibleYn(todoElementNo, "Y");
	}

	public int countVisibleCompletedDetails(Long todoElementNo) {
	    return projectTodoElementDetailRepository.countByProjectTodoElement_TodoNoAndStatusAndVisibleYn(todoElementNo, "Y", "Y");
	}
	
}
