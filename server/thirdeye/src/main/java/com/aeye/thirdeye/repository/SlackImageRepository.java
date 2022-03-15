package com.aeye.thirdeye.repository;

import com.aeye.thirdeye.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SlackImageRepository extends JpaRepository<Image, Long> {

}

