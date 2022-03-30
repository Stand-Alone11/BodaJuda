package com.aeye.thirdeye.dto;

import com.aeye.thirdeye.entity.Project;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDto {
    private Long id;

    private String provider;

    private String title;

    private int goal;

    private String description;

    public ProjectDto(Project project){
        this.id = project.getId();
        this.provider = project.getProvider();
        this.title = project.getTitle();
        this.goal = project.getGoal();
        this.description = project.getDescription();
    }

}
