package com.aeye.thirdeye.repository;

import com.aeye.thirdeye.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project,Long> {
}
