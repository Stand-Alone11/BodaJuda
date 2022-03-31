package com.aeye.thirdeye.repository;

import com.aeye.thirdeye.entity.LeaderBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaderBoardRepository extends JpaRepository<LeaderBoard, Long> {


    Page<LeaderBoard> findAll(Pageable pageable);

}
