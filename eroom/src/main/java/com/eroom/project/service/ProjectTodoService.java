package com.eroom.project.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eroom.project.dto.ProjectTodoListDto;
import com.eroom.project.entity.ProjectTodoList;
import com.eroom.project.repository.ProjectTodoListRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectTodoService {
	
	private final ProjectTodoListRepository projectTodoListRepository;
	
	public List<ProjectTodoListDto> findByProjectNo(Long projectNo) {
		
		List<ProjectTodoList> list = projectTodoListRepository
		        .findByProjectNoOrderByListSequenceAsc(projectNo);

	    List<ProjectTodoListDto> dtolist = new ArrayList<>();

	    for (ProjectTodoList projectTodoList : list) {
	        ProjectTodoListDto dto = ProjectTodoListDto.builder()
	            .project_todo_list_no(projectTodoList.getProjectTodoListNo())
	            .project_no(projectTodoList.getProjectNo())
	            .list_name(projectTodoList.getListName())
	            .list_sequence(projectTodoList.getListSequence())
	            .list_color(projectTodoList.getListColor())
	            .build();
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


	
}
