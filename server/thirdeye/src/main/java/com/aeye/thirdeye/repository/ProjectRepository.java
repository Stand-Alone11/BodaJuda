package com.aeye.thirdeye.repository;

import com.aeye.thirdeye.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project,Long> {

    @Query("select p from Project p " +
            "where p.id in (select i.project.id from Image i where i.user.id = :id) " +
            "order by p.id desc")
    List<Project> findHistory(@Param("id") Long id, Pageable pageable);

    @Query("select p from Project p where p.goal > p.accepted")
    Page<Project> findAllProjects(Pageable pageable);
}
