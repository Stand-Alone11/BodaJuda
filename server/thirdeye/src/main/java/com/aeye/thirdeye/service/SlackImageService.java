package com.aeye.thirdeye.service;

import com.aeye.thirdeye.dto.ImageDto;
import com.aeye.thirdeye.dto.TestDto;
import com.aeye.thirdeye.repository.SlackImageRepository;
import com.slack.api.app_backend.interactive_components.payload.BlockActionPayload;
import com.slack.api.model.ModelConfigurator;
import com.slack.api.model.block.ImageBlock;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.composition.DispatchActionConfig;
import com.slack.api.model.block.element.BlockElement;
import com.slack.api.model.block.element.PlainTextInputElement;
import com.slack.api.util.json.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.composition.BlockCompositions.markdownText;
import static com.slack.api.model.block.composition.BlockCompositions.plainText;
import static com.slack.api.model.block.element.BlockElements.*;

@Service
@RequiredArgsConstructor
public class SlackImageService {

    private final SlackImageRepository slackImageRepository;

    // Slack request layout
    public List<LayoutBlock> makeRequestLayout(ImageDto imageDto){

        TestDto testDto = new TestDto(1);
        List<LayoutBlock> layoutBlocks = new ArrayList<>();
        // 텍스트를 남길 SectionBlock
        layoutBlocks.add(section(section -> section.text(markdownText("*이미지 라벨링 검수*"))));
        // Action과 텍스트를 구분하기 위한 Divider
        layoutBlocks.add(divider());
        // $$$테스트용 이미지 ( 나중에 값들어오는 이미지로 바꾼다 )
        layoutBlocks.add(image((ModelConfigurator<ImageBlock.ImageBlockBuilder>) image -> image.imageUrl("https://t1.kakaocdn.net/kakaocorp/kakaocorp/admin/service/a85d0594017900001.jpg")
                .altText("카카오프렌즈 테스트사진")));
        layoutBlocks.add(divider());

        layoutBlocks.add(
                input(input -> input.element(
                                plainTextInput(p -> p
                                        .actionId("typeAaction")
                                        .placeholder(plainText("대분류를 입력해주세요"))
                                )
                        ).label(
                                plainText(pt -> pt.text("대분류").emoji(true))
                        ).dispatchAction(true)
                )
        );

        layoutBlocks.add(
                input(input -> input.element(
                                plainTextInput(p -> p
                                        .actionId("typeBaction")
                                        .placeholder(plainText("중분류를 입력해주세요"))
                                )
                        ).label(
                                plainText(pt -> pt.text("중분류").emoji(true))
                        ).dispatchAction(true)
                )
        );

        layoutBlocks.add(
                input(input -> input.element(
                                plainTextInput(p -> p
                                        .actionId("typeCaction")
                                        .placeholder(plainText("소분류를 입력해주세요"))
                                )
                        ).label(
                                plainText(pt -> pt.text("소분류").emoji(true))
                        ).dispatchAction(true)
                )
        );

        // Action과 텍스트를 구분하기 위한 Divider
        layoutBlocks.add(divider());
        // ActionBlock에 승인 버튼과 거부 버튼을 추가
        layoutBlocks.add(
                actions(actions -> actions
                        .elements(asElements(
                                button(b -> b.text(plainText(pt -> pt.emoji(true).text("승인")))
                                        .value(Long.toString(imageDto.getId()))
                                        .style("primary")
                                        .actionId("action_approve")
                                ),
                                button(b -> b.text(plainText(pt -> pt.emoji(true).text("거부")))
                                        .value(Long.toString(imageDto.getId()))
                                        .style("danger")
                                        .actionId("action_reject")
                                )
                        ))
                )
        );
        return layoutBlocks;
    }

    // 이미지 검수 후 승인, 거절에 따른 처리 Layout 생성 및 DB 정리 (테스트 용)
    public BlockActionPayload makeResponseLayout(String payload){

        BlockActionPayload blockActionPayload = GsonFactory.
                createSnakeCase().fromJson(payload, BlockActionPayload.class);

        blockActionPayload.getActions().forEach(action -> {
            Integer seq = Integer.parseInt(action.getValue());

            if (action.getActionId().equals("action_reject")) {
//                blockActionPayload.getMessage().getBlocks().add(0,
//                        section(section ->
//                                section.text(markdownText("거부거부"))
//                        )
//                );
//                blockActionPayload.getMessage().getBlocks().add(4,
//                        section(section -> section.text(markdownText("*반려 되었습니다*"))));
                // 여기에 DB 이미지 정보 관련(거절) code (DB에서 값 삭제)
//                slackImageRepository.deleteById(Long.valueOf(seq));
            } else {
//                blockActionPayload.getMessage().getBlocks().add(0,
//                        section(section ->
//                                section.text(markdownText("승인승인"))
//                        )
//                );
//                blockActionPayload.getMessage().getBlocks().add(4,
//                        section(section -> section.text(markdownText("*승인 되었습니다*"))));
                // 여기에 DB 이미지 정보 관련(승인) code
//                블라블라~~
            }
        });
        return blockActionPayload;
    }

    // 처음 정보 받았을 때 Slack에 넘기는 layout 생성 (테스트 용)
    public List<LayoutBlock> makeLayout(){
        List<String> triggers = new ArrayList<>();
        triggers.add("on_character_entered");

        TestDto testDto = new TestDto(1);
        List<LayoutBlock> layoutBlocks = new ArrayList<>();
        // 텍스트를 남길 SectionBlock
        layoutBlocks.add(section(section -> section.text(markdownText("*이미지 라벨링 검수*"))));
        // Action과 텍스트를 구분하기 위한 Divider
        layoutBlocks.add(divider());
        // $$$테스트용 이미지 ( 나중에 값들어오는 이미지로 바꾼다 )
        layoutBlocks.add(image((ModelConfigurator<ImageBlock.ImageBlockBuilder>) image -> image.imageUrl("https://t1.kakaocdn.net/kakaocorp/kakaocorp/admin/service/a85d0594017900001.jpg")
                .altText("카카오프렌즈 테스트사진")));
        layoutBlocks.add(divider());

        layoutBlocks.add(
                input(input -> input.element(
                                plainTextInput(p -> p
                                        .actionId("typeAaction")
                                        .placeholder(plainText("대분류를 입력해주세요"))
                                )
                        ).label(
                                plainText(pt -> pt.text("대분류").emoji(true))
                        ).dispatchAction(true)
                )
        );

        layoutBlocks.add(
                input(input -> input.element(
                                plainTextInput(p -> p
                                        .actionId("typeBaction")
                                        .placeholder(plainText("중분류를 입력해주세요"))
                                )
                        ).label(
                                plainText(pt -> pt.text("중분류").emoji(true))
                        ).dispatchAction(true)
                )
        );

        layoutBlocks.add(
                input(input -> input.element(
                                plainTextInput(p -> p
                                        .actionId("typeCaction")
                                        .placeholder(plainText("소분류를 입력해주세요"))
                                )
                        ).label(
                                plainText(pt -> pt.text("소분류").emoji(true))
                        ).dispatchAction(true)
                )
        );

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

    // 이미지 검수 후 승인, 거절에 따른 처리 Layout 생성 및 DB 정리 (테스트 용)
    public BlockActionPayload responseSlackLayout(String payload){

        BlockActionPayload blockActionPayload = GsonFactory.
                createSnakeCase().fromJson(payload, BlockActionPayload.class);

        blockActionPayload.getActions().forEach(action -> {
            if (action.getActionId().equals("action_reject")) {
                Integer seq = Integer.parseInt(action.getValue());
//                blockActionPayload.getMessage().getBlocks().add(0,
//                        section(section ->
//                                section.text(markdownText("거부거부"))
//                        )
//                );
////                blockActionPayload.getMessage().getBlocks().add(4,
////                        section(section -> section.text(markdownText("*반려 되었습니다*"))));
//                // 여기에 DB 이미지 정보 관련(거절) code (DB에서 값 삭제)
////                slackImageRepository.deleteById(Long.valueOf(seq));
            } else if(action.getActionId().equals("action_approve")){
                Integer seq = Integer.parseInt(action.getValue());
//                blockActionPayload.getMessage().getBlocks().add(0,
//                        section(section ->
//                                section.text(markdownText("승인승인"))
//                        )
//                );
////                blockActionPayload.getMessage().getBlocks().add(4,
////                        section(section -> section.text(markdownText("*승인 되었습니다*"))));
//                // 여기에 DB 이미지 정보 관련(승인) code
////                블라블라~~
                // 이거 쓰면 될듯
                String typeAstr = blockActionPayload.getMessage().getBlocks().get(4).toString();
                String typeBstr = blockActionPayload.getMessage().getBlocks().get(5).toString();
                String typeCstr = blockActionPayload.getMessage().getBlocks().get(6).toString();
                System.out.println(typeAstr.split("\\*")[1]);
                System.out.println(typeBstr.split("\\*")[1]);
                System.out.println(typeCstr.split("\\*")[1]);
            }
            else if(action.getActionId().equals("typeAaction")){
                blockActionPayload.getMessage().getBlocks().remove(2);
                blockActionPayload.getMessage().getBlocks().add(2,
                        image((ModelConfigurator<ImageBlock.ImageBlockBuilder>) image -> image.imageUrl("https://t1.kakaocdn.net/kakaocorp/kakaocorp/admin/service/a85d0594017900001.jpg")
                                .altText("카카오프렌즈 테스트사진"))
                );
                blockActionPayload.getMessage().getBlocks().remove(4);
                blockActionPayload.getMessage().getBlocks().add(4,
                        section(section -> section.text(markdownText("대분류 : " + "*" + action.getValue() + "*"))));
            }
            else if(action.getActionId().equals("typeBaction")){
                blockActionPayload.getMessage().getBlocks().remove(2);
                blockActionPayload.getMessage().getBlocks().add(2,
                        image((ModelConfigurator<ImageBlock.ImageBlockBuilder>) image -> image.imageUrl("https://t1.kakaocdn.net/kakaocorp/kakaocorp/admin/service/a85d0594017900001.jpg")
                                .altText("카카오프렌즈 테스트사진"))
                );
                blockActionPayload.getMessage().getBlocks().remove(5);
                blockActionPayload.getMessage().getBlocks().add(5,
                        section(section -> section.text(markdownText("중분류 : " + "*" + action.getValue() + "*"))));
            }
            else if(action.getActionId().equals("typeCaction")){
                blockActionPayload.getMessage().getBlocks().remove(2);
                blockActionPayload.getMessage().getBlocks().add(2,
                        image((ModelConfigurator<ImageBlock.ImageBlockBuilder>) image -> image.imageUrl("https://t1.kakaocdn.net/kakaocorp/kakaocorp/admin/service/a85d0594017900001.jpg")
                                .altText("카카오프렌즈 테스트사진"))
                );
                blockActionPayload.getMessage().getBlocks().remove(6);
                blockActionPayload.getMessage().getBlocks().add(6,
                        section(section -> section.text(markdownText("소분류 : " + "*" + action.getValue() + "*"))));
            }
        });
        return blockActionPayload;
    }



}
