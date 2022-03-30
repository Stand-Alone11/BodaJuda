package com.aeye.thirdeye.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Project")
@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED)
public class Project {
    @Id
    @GeneratedValue
    private Long id;

    @Size(max = 50)
    private String provider;

    @Size(max = 50)
    private String title;

    private int goal;

    @Size(max = 1024)
    private String description;

    @OneToMany(mappedBy = "project")
    private List<Image> imageList = new ArrayList<>();
}
