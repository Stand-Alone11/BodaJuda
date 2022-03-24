package com.aeye.thirdeye.repository;

import com.aeye.thirdeye.dto.LeaderBoardDto;
import com.aeye.thirdeye.entity.Image;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {

    @Query("select count(i) from Image i where i.user.id = :userId group by i.imageValidate order by i.imageValidate ")
    int[] getCategoryUpload(@Param("userId") Long userid);

    @Query("select count(i) from Image i where i.user.id = :userId")
    int getTotalUpload(@Param("userId") Long userid);

    @Query(value="SELECT * FROM (SELECT i.user_id, rank() over(order by count(*) DESC) " +
            "from Image i group by i.user_id ) ranked WHERE ranked.user_id = :userId"
            , nativeQuery = true)
    Optional<Integer> getRank(@Param("userId") Long userid);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from Image i where i.user.id = :id")
    void deleteAllByUser(@Param("id") Long id);
}
