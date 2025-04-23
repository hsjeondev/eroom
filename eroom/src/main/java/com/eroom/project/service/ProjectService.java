package com.eroom.project.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eroom.project.dto.GithubPullRequestDto;
import com.eroom.project.dto.ProjectDto;
import com.eroom.project.dto.ProjectMemberDto;
import com.eroom.project.entity.Project;
import com.eroom.project.repository.ProjectMemberRepository;
import com.eroom.project.repository.ProjectRepository;
import com.eroom.rsacryption.RSACryptor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectService {

	private final RSACryptor rsaCryptor;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    
    @Transactional(rollbackFor = Exception.class)
    public Long saveProject(ProjectDto projectDto, List<ProjectMemberDto> memberDtos) {
    	Long result = 0L;
    	
    	try {
    		String encryptedToken = rsaCryptor.encrypt(projectDto.getProject_github_token());
    		projectDto.setProject_github_token(encryptedToken);
    		
    		// proceed에 시작일이 오늘이거나 오늘보다 이전이면 진행 중, 오늘보다 이후면 진행 예정 설정
    	    LocalDate today = LocalDate.now();
    	    LocalDate startDate = projectDto.getProject_start();
    	    
    	    if (!startDate.isAfter(today)) {
    	        projectDto.setProceed("진행 중");
    	    } else {
    	        projectDto.setProceed("진행 예정");
    	    }
    		
    		Project project = projectRepository.save(projectDto.toEntity());
    		
    		if(project.getProjectNo() != 0) {
    			for(ProjectMemberDto projectMemberDto : memberDtos) {
        			projectMemberDto.setProject(project);
        			projectMemberRepository.save(projectMemberDto.toEntity());
        		}
    		}
    		
    		result = project.getProjectNo();
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    	
    	return result;
    }

    public List<ProjectDto> findAllProject() {
        List<Project> projects = projectRepository.findAll();
        List<ProjectDto> projectDtos = new ArrayList<>();

        for (Project project : projects) {
            projectDtos.add(new ProjectDto().toDto(project));
        }

        return projectDtos;
    }
    
    public Long getProjectCount() {
        return projectRepository.count();
    }
    
    public ProjectDto findByProjectNo(Long projectNo) {
    	Project project = projectRepository.findById(projectNo).orElse(null);
    	ProjectDto projectdto = null;
    	
    	if(project != null) {
    		projectdto = new ProjectDto().toDto(project);
    		String encryptedToken = projectdto.getProject_github_token();
            String decryptedToken = rsaCryptor.decrypt(encryptedToken);
            projectdto.setProject_github_token(decryptedToken);
    	}
    	
    	return projectdto;
    }
    
    public List<GithubPullRequestDto> fetchPullRequests(Long projectNo) {
        try {
            Project project = projectRepository.findById(projectNo).orElse(null);
            if (project == null) {
                System.out.println("해당 프로젝트가 없습니다: " + projectNo);
                return null;
            }

            String repoUrl = project.getProjectGithubUrl();
            String encryptedToken = project.getProjectGithubToken();
            String decryptedToken = rsaCryptor.decrypt(encryptedToken);

            String[] split = repoUrl.replace("https://github.com/", "").split("/");
            String owner = split[0];
            String repo = split[1].replace(".git", "");

            String apiUrl = "https://api.github.com/repos/" + owner + "/" + repo + "/pulls?state=all";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Authorization", "Bearer " + decryptedToken)
                    .header("Accept", "application/vnd.github+json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Status: " + response.statusCode());

            if (response.statusCode() == 200) {
                String json = response.body();

                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(
                        json,
                        new TypeReference<List<GithubPullRequestDto>>() {}
                );

            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
