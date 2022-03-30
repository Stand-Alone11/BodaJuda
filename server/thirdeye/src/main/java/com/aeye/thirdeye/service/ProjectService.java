package com.aeye.thirdeye.service;

import com.aeye.thirdeye.dto.ProjectDto;
import com.aeye.thirdeye.entity.Project;
import com.aeye.thirdeye.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Transactional
    public Project insertProject(ProjectDto projectDto){
        Project newProject = new Project();
        newProject.setTitle(projectDto.getTitle());
        newProject.setGoal(projectDto.getGoal());
        newProject.setDescription(projectDto.getDescription());
        newProject.setProvider(projectDto.getProvider());
        newProject.setAccepted(0);
        Project result = projectRepository.save(newProject);
        return result;
    }

    public List<Project> getProjects(Pageable pageable){

        Page<Project> resultPage = projectRepository.findAllProjects(pageable);
        return resultPage.getContent();
    }
}
