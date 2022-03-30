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

    @Query(value = "SELECT CONCAT(CAST(COUNT(*) AS CHAR(10)), i.image_validate) " +
            "from image i where i.user_id = :userId group by i.image_validate " +
            "order by i.image_validate "
            , nativeQuery = true)
    String[] getCategoryUpload(@Param("userId") Long userid);

    @Query("select count(i) from Image i where i.user.id = :userId")
    int getTotalUpload(@Param("userId") Long userid);

    @Query(value = "SELECT CONCAT(CAST(COUNT(*) AS CHAR(10)), i.image_validate) " +
            "from image i where i.user_id = :userId and i.project_id = :projectId group by i.image_validate " +
            "order by i.image_validate "
            , nativeQuery = true)
    String[] getCategoryUploadWithProject(@Param("userId") Long userid, @Param("projectId") Long projectid);

    @Query(value="SELECT * FROM (SELECT rank() over(order by count(*) DESC), i.user_id " +
            "from image i group by i.user_id ) ranked WHERE ranked.user_id = :userId"
            , nativeQuery = true)
    Optional<Integer> getRank(@Param("userId") Long userid);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from Image i where i.user.id = :id")
    void deleteAllByUser(@Param("id") Long id);
}
