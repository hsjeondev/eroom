package com.eroom.project.service;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.eroom.drive.dto.DriveDto;
import com.eroom.drive.entity.Drive;
import com.eroom.drive.repository.DriveRepository;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.entity.Separator;
import com.eroom.employee.repository.SeparatorRepository;
import com.eroom.project.dto.GithubPullRequestDto;
import com.eroom.project.dto.ProjectDto;
import com.eroom.project.dto.ProjectMemberDto;
import com.eroom.project.entity.Project;
import com.eroom.project.entity.ProjectMember;
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
    private final DriveRepository driveRepository;
    private final SeparatorRepository separatorRepository;
    
 // 파일 저장 경로 
 	 @Value("${ffupload.location}")
 	 private String fileDir;
 	 
 	 public List<DriveDto> findProjectFiles(String separatorCode, Long projectNo) {
 		List<Drive> drives = driveRepository.findBySeparatorCodeContainingAndParam1AndVisibleYn(separatorCode, projectNo, "Y");
		List<DriveDto> result = new ArrayList<>();
		
		for (Drive drive : drives) {
			result.add(DriveDto.toDto(drive));
		}
		
		return result;
 	 }
    
    @Transactional(rollbackFor = Exception.class)
    public Long saveProject(ProjectDto projectDto, List<ProjectMemberDto> memberDtos) {
    	Long result = 0L;
    	
    	try {
    		String encryptedToken = rsaCryptor.encrypt(projectDto.getProject_github_token());
    		projectDto.setProject_github_token(encryptedToken);
    		
    		LocalDate today     = LocalDate.now();
            LocalDate startDate = projectDto.getProject_start();
            LocalDate endDate   = projectDto.getProject_end();

            if (endDate != null && endDate.isEqual(today)) {
            	projectDto.setProceed("완료");
            } else if (!startDate.isAfter(today)) {
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
    
    @Transactional(rollbackFor = Exception.class)
    public Long updateProject(ProjectDto dto, List<ProjectMemberDto> memberDtos) {
        Long result = 0L;
        
        try {
        	
        	Project findProject = projectRepository.findById(dto.getProject_no()).orElse(null);
        	
            String encryptedToken = rsaCryptor.encrypt(dto.getProject_github_token());
            dto.setProject_github_token(encryptedToken);
            
            LocalDate today     = LocalDate.now();
            LocalDate startDate = dto.getProject_start();
            LocalDate endDate   = dto.getProject_end();

            if (endDate != null && endDate.isEqual(today)) {
                dto.setProceed("완료");
            } else if (!startDate.isAfter(today)) {
                dto.setProceed("진행 중");
            } else {
                dto.setProceed("진행 예정");
            }
            
            dto.setProgress(findProject.getProgress());
            
            Project project = dto.toEntity();
            projectRepository.save(project);

            List<ProjectMember> existing = 
                projectMemberRepository.findByProject_ProjectNo(project.getProjectNo());
            for (ProjectMember pm : existing) {
                pm.setVisibleYn("N");
                projectMemberRepository.save(pm);
            }

            for (ProjectMemberDto mDto : memberDtos) {
                Long   empNo   = mDto.getProject_member().getEmployeeNo();
                String pmFlag  = mDto.getProject_manager();
                String mgrFlag = mDto.getIs_manager();

                ProjectMember found = null;
                for (ProjectMember pm : existing) {
                    if (pm.getEmployee().getEmployeeNo().equals(empNo)) {
                        found = pm;
                        break;
                    }
                }

                if (found != null) {
                    found.setVisibleYn("Y");
                    found.setProjectManager(pmFlag);
                    found.setIsManager(mgrFlag);
                } else {
                    ProjectMember pm = ProjectMember.builder()
                        .project(project)
                        .employee(Employee.builder().employeeNo(empNo).build())
                        .visibleYn("Y")
                        .projectManager(pmFlag)
                        .isManager(mgrFlag)
                        .build();
                    projectMemberRepository.save(pm);
                }
            }

            result = project.getProjectNo();

        } catch(Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public int holdingProject(Long projectNo) {
    	int result = 0;
    	
    	try {
    		
    		Project project = projectRepository.findById(projectNo).orElse(null);
    		
    		if(project != null) {
    			project.setProceed("보류");
    			projectRepository.save(project);

    			result = 1;
    		}
    		
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    	
    	return result;
    }
    
    public int doneProject(Long projectNo) {
    	int result = 0;
    	
    	try {
    		
    		Project project = projectRepository.findById(projectNo).orElse(null);
    		
    		if(project != null) {
    			project.setProceed("완료");
    			project.setProjectEnd(LocalDate.now());
    			projectRepository.save(project);

    			result = 1;
    		}
    		
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    	
    	return result;
    }
    
    public int uploadProjectFiles(DriveDto driveDto, Long employeeNo) {
        int result = 0;

        if (driveDto.getSeparatorCode() == null || driveDto.getSeparatorCode().isEmpty()) {
            driveDto.setSeparatorCode(
                separatorRepository.findBySeparatorName("개인")
                    .map(Separator::getSeparatorCode)
                    .orElse("E001")
            );
        }

        List<String> descriptions = driveDto.getDriveDescriptions();
        List<MultipartFile> files = driveDto.getDriveFiles();

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            try {
                String oriName = file.getOriginalFilename();
                String ext     = oriName.substring(oriName.lastIndexOf("."));
                String newName = UUID.randomUUID().toString().replace("-", "") + ext;

                // project/ 폴더에 저장
                String path = fileDir + "project/" + newName;
                File savedFile = new File(path);
                savedFile.getParentFile().mkdirs();
                file.transferTo(savedFile);

                String description = (descriptions != null && descriptions.size() > i)
                                       ? descriptions.get(i)
                                       : null;

                Drive drive = Drive.builder()
                    .uploader(Employee.builder().employeeNo(employeeNo).build())
                    .separatorCode(driveDto.getSeparatorCode())
                    .driveOriName(oriName)
                    .driveNewName(newName)
                    .driveType(ext)
                    .driveSize(file.getSize())
                    .drivePath("project/" + newName)
                    .driveDescription(description)
                    .downloadCount(0L)
                    .visibleYn("Y")
                    .param1(driveDto.getParam1())
                    .build();

                driveRepository.save(drive);
                result++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }



}
