package com.aeye.thirdeye.scheduler;


import com.aeye.thirdeye.dto.LeaderBoardDto;
import lombok.RequiredArgsConstructor;
import org.qlrm.mapper.JpaResultMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Component
@RequiredArgsConstructor
public class LeaderBoardScheduler {

    @PersistenceContext
    EntityManager em;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void updateLeaderBoard(){

        deleteLeaderBoard();

        String query = "INSERT INTO leader_board(user_id, nick_name, ranking, total, profile_image) " +
                "SELECT u.id user_id, u.nick_name nick_name, ranked.ranking ranking, ranked.total total, u.profile_image profile_image " +
                "FROM (SELECT i.user_id, rank() over(order by count(*) DESC) AS RANKING, " +
                "COUNT(*) total from image i WHERE i.image_validate = \"Y\" group by i.user_id ) ranked, user u " +
                "WHERE ranked.user_id = u.id order by ranked.ranking asc; ";

        JpaResultMapper result = new JpaResultMapper();
        Query resultQuery = em.createNativeQuery(query);
        resultQuery.executeUpdate();

    }

    @Transactional
    public void deleteLeaderBoard(){

        String query = "DELETE FROM leader_board";

        JpaResultMapper result = new JpaResultMapper();
        Query resultQuery = em.createNativeQuery(query);
        resultQuery.executeUpdate();
    }

}
