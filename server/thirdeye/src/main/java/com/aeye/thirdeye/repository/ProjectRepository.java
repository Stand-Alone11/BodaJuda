package com.aeye.thirdeye.repository;

import com.aeye.thirdeye.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProjectRepository extends JpaRepository<Project,Long> {

    Project findByTitle(String Title);

    @Query("select p from Project p where p.goal > p.accepted")
    Page<Project> findAllProjects(Pageable pageable);
}
