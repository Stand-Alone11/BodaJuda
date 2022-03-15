package com.aeye.thirdeye.service;

import com.aeye.thirdeye.dto.TestDto;
import com.aeye.thirdeye.repository.SlackImageRepository;
import com.slack.api.app_backend.interactive_components.payload.BlockActionPayload;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.util.json.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.composition.BlockCompositions.markdownText;
import static com.slack.api.model.block.composition.BlockCompositions.plainText;
import static com.slack.api.model.block.element.BlockElements.asElements;
import static com.slack.api.model.block.element.BlockElements.button;

@Service
@RequiredArgsConstructor
public class SlackImageService {

    private final SlackImageRepository slackImageRepository;

    // 처음 정보 받았을 때 Slack에 넘기는 layout 생성
    public List<LayoutBlock> makeLayout(){
        TestDto testDto = new TestDto(1);
        List<LayoutBlock> layoutBlocks = new ArrayList<>();
        // 텍스트를 남길 SectionBlock
        layoutBlocks.add(section(section -> section.text(markdownText("*이미지 라벨링 검수*"))));
        // Action과 텍스트를 구분하기 위한 Divider
        layoutBlocks.add(divider());
        // $$$테스트용 이미지 ( 나중에 값들어오는 이미지로 바꾼다 )
        layoutBlocks.add(image(image -> image.imageUrl("https://t1.kakaocdn.net/kakaocorp/kakaocorp/admin/service/a85d0594017900001.jpg")
                .altText("카카오프렌즈 테스트사진")));
        // Action과 텍스트를 구분하기 위한 Divider
        layoutBlocks.add(divider());
        // ActionBlock에 승인 버튼과 거부 버튼을 추가
        layoutBlocks.add(
                actions(actions -> actions
                        .elements(asElements(
                                button(b -> b.text(plainText(pt -> pt.emoji(true).text("승인")))
                                        .value(Integer.toString(testDto.getSeq()))
                                        .style("primary")
                                        .actionId("action_approve")
                                ),
                                button(b -> b.text(plainText(pt -> pt.emoji(true).text("거부")))
                                        .value(Integer.toString(testDto.getSeq()))
                                        .style("danger")
                                        .actionId("action_reject")
                                )
                        ))
                )
        );
        return layoutBlocks;
    }

    // 이미지 검수 후 승인, 거절에 따른 처리 Layout 생성 및 DB 정리
    public BlockActionPayload responseSlackLayout(String payload){

        BlockActionPayload blockActionPayload = GsonFactory.
                createSnakeCase().fromJson(payload, BlockActionPayload.class);

//        // Block 수정 (현재 안되는 이유 모름) 삭제로 구현해놓음
//        blockActionPayload.getMessage().getBlocks().remove(0);
//        blockActionPayload.getActions().forEach(action -> {
//            Integer seq = Integer.parseInt(action.getValue());
//
//            if (action.getActionId().equals("action_reject")) {
//                blockActionPayload.getMessage().getBlocks().add(0,
//                        section(section ->
//                                section.text(markdownText("거부거부"))
//                        )
//                );
////                blockActionPayload.getMessage().getBlocks().add(4,
////                        section(section -> section.text(markdownText("*반려 되었습니다*"))));
//                // 여기에 DB 이미지 정보 관련(거절) code (DB에서 값 삭제)
////                slackImageRepository.deleteById(Long.valueOf(seq));
//            } else {
//                blockActionPayload.getMessage().getBlocks().add(0,
//                        section(section ->
//                                section.text(markdownText("승인승인"))
//                        )
//                );
////                blockActionPayload.getMessage().getBlocks().add(4,
////                        section(section -> section.text(markdownText("*승인 되었습니다*"))));
//                // 여기에 DB 이미지 정보 관련(승인) code
////                블라블라~~
//            }
//        });
        return blockActionPayload;
    }



}
