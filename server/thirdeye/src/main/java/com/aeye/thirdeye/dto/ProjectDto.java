package com.aeye.thirdeye.dto;

import com.aeye.thirdeye.entity.Project;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDto {
    private Long id;

    private String provider;

    private String title;

    private int goal;

    private String description;

    private String typeA;

    private String typeB;

    private String typeC;

    public ProjectDto(Project project){
        this.id = project.getId();
        this.provider = project.getProvider();
        this.title = project.getTitle();
        this.goal = project.getGoal();
        this.description = project.getDescription();
        this.typeA = project.getTypeA();
        this.typeB = project.getTypeB();
        this.typeC = project.getTypeC();
    }

}
