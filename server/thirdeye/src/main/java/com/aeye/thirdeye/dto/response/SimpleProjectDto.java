package com.aeye.thirdeye.dto.response;

import com.aeye.thirdeye.entity.Project;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleProjectDto {
    private Long id;

    private String provider;

    private String title;

    private int goal;

    private String description;

    private int accepted;

    public SimpleProjectDto(Project project){
        this.id = project.getId();
        this.provider = project.getProvider();
        this.title = project.getTitle();
        this.goal = project.getGoal();
        this.description = project.getDescription();
        this.accepted = project.getAccepted();
    }

}

