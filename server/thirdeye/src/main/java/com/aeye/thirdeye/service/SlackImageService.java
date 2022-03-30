package com.aeye.thirdeye.service;

import com.aeye.thirdeye.dto.ImageDto;
import com.aeye.thirdeye.entity.Image;
import com.aeye.thirdeye.entity.Project;
import com.aeye.thirdeye.repository.ImageRepository;
import com.aeye.thirdeye.repository.ProjectRepository;
import com.slack.api.Slack;
import com.slack.api.app_backend.interactive_components.payload.BlockActionPayload;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.util.json.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.composition.BlockCompositions.*;
import static com.slack.api.model.block.element.BlockElements.*;
import static com.slack.api.webhook.WebhookPayloads.payload;

@Service
@RequiredArgsConstructor
public class SlackImageService {

    private final ImageRepository imageRepository;

    private final ProjectRepository projectRepository;

    @Value("${notification.slack.token}")
    private String token;

    @Value("${notification.slack.webhook.url}")
    private String url;

    private String checked;

    // Slack request layout
    @Transactional
    public void makeRequestLayout(ImageDto imageDto){

        List<LayoutBlock> layoutBlocks = new ArrayList<>();

        layoutBlocks.add(divider());

        layoutBlocks.add(section(section ->
                section.text(markdownText("*상품명* : " + "*" + imageDto.getTitle() + "*" ))));

        layoutBlocks.add(section(section ->
                section.text(markdownText("*제조사* : " + "*" + imageDto.getProvider() + "*" ))));

        layoutBlocks.add(section(section ->
                section.text(markdownText("*대분류* : " + "*" + imageDto.getTypeA() + "*" ))));

        layoutBlocks.add(section(section ->
                section.text(markdownText("*중분류* : " + "*" + imageDto.getTypeB() + "*" ))));

        layoutBlocks.add(section(section ->
                section.text(markdownText("*소분류* : " + "*" + imageDto.getTypeC() + "*" ))));
//
//        layoutBlocks.add(
//                input(input -> input.element(
//                                plainTextInput(p -> p
//                                        .actionId("typeAaction")
//                                        .placeholder(plainText("대분류를 입력해주세요"))
//                                )
//                        ).label(
//                                plainText(pt -> pt.text("대분류").emoji(true))
//                        ).dispatchAction(true)
//                )
//        );
//
//        layoutBlocks.add(
//                input(input -> input.element(
//                                plainTextInput(p -> p
//                                        .actionId("typeBaction")
//                                        .placeholder(plainText("중분류를 입력해주세요"))
//                                )
//                        ).label(
//                                plainText(pt -> pt.text("중분류").emoji(true))
//                        ).dispatchAction(true)
//                )
//        );
//
//        layoutBlocks.add(
//                input(input -> input.element(
//                                plainTextInput(p -> p
//                                        .actionId("typeCaction")
//                                        .placeholder(plainText("소분류를 입력해주세요"))
//                                )
//                        ).label(
//                                plainText(pt -> pt.text("소분류").emoji(true))
//                        ).dispatchAction(true)
//                )
//        );
        // 전면 / 후면 라디오 버튼
        layoutBlocks.add(
                section(section -> section.text(markdownText("전면 / 후면"))
                        .accessory(radioButtons( radio -> radio.options(asOptions(
                                                        option(o -> o.text(plainText(p->p.text("전면").emoji(true))).value("전면")),
                                                        option(o -> o.text(plainText(p->p.text("후면").emoji(true))).value("후면"))
                                                )
                                        ).actionId("typeDaction")
                                )
                        )

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

        // 위에 만든 payload Slack에 전송
        try {
            Slack.getInstance().send(url,
                    payload(p -> p
                            .text("슬랙에 메시지를 출력하지 못했습니다.")
                            .blocks(layoutBlocks)
                    )
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // 이미지 검수 후 승인, 거절에 따른 처리 Layout 생성 및 DB 정리
    @Transactional
    public BlockActionPayload makeResponseLayout(String payload){

        BlockActionPayload blockActionPayload = GsonFactory.
                createSnakeCase().fromJson(payload, BlockActionPayload.class);

        blockActionPayload.getActions().forEach(action -> {

            if (action.getActionId().equals("action_reject")) {
                int seq = Integer.parseInt(action.getValue());
                Image nowImage = imageRepository.findById(Long.valueOf(seq)).orElse(null);
                if(nowImage != null){
                    nowImage.setImageValidate("N");
                    imageRepository.save(nowImage);
                }
                checked = "";
                int len = blockActionPayload.getMessage().getBlocks().size();
                // 맨 앞에 divider 빼고 모두 삭제
                if (len > 1) {
                    blockActionPayload.getMessage().getBlocks().subList(1, len).clear();
                }
                blockActionPayload.getMessage().getBlocks().add(1,
                        section(section -> section.text(markdownText("*거부 완료*"))));
            } else if(action.getActionId().equals("action_approve")) {
                int seq = Integer.parseInt(action.getValue());

                Image image = imageRepository.findById(Long.valueOf(seq)).orElse(null);
                System.out.println("시퀀스 : " + Long.valueOf(seq));
                if(image != null){
                    // 프로젝트 accepted 갱신 부분
                    System.out.println("들어오나???");
                     Project nowProject = projectRepository.findById(image.getProject().getId()).orElse(null);
                     nowProject.setAccepted(nowProject.getAccepted() + 1);
                     projectRepository.save(nowProject);
                    System.out.println("accept : " + nowProject.getAccepted() + 1);
                    image.setImageValidate("Y");
                    image.setFaceYN(checked);
                    imageRepository.save(image);
                }
                checked = "";
                int len = blockActionPayload.getMessage().getBlocks().size();
                // 맨 앞에 divider 빼고 모두 삭제
                if (len > 1) {
                    blockActionPayload.getMessage().getBlocks().subList(1, len).clear();
                }
                blockActionPayload.getMessage().getBlocks().add(1,
                        section(section -> section.text(markdownText("*승인 완료*"))));
            }
//            else if(action.getActionId().equals("typeAaction")){
//                blockActionPayload.getMessage().getBlocks().remove(3);
//                blockActionPayload.getMessage().getBlocks().add(3,
//                        section(section -> section.text(markdownText("대분류 : " + "*" + action.getValue() + "*"))));
//            }
//            else if(action.getActionId().equals("typeBaction")){
//                blockActionPayload.getMessage().getBlocks().remove(4);
//                blockActionPayload.getMessage().getBlocks().add(4,
//                        section(section -> section.text(markdownText("중분류 : " + "*" + action.getValue() + "*"))));
//            }
//            else if(action.getActionId().equals("typeCaction")){
//                blockActionPayload.getMessage().getBlocks().remove(5);
//                blockActionPayload.getMessage().getBlocks().add(5,
//                        section(section -> section.text(markdownText("소분류 : " + "*" + action.getValue() + "*"))));
//            }
            else if(action.getActionId().equals("typeDaction")){
                checked = action.getSelectedOption().getValue();
            }
        });
        return blockActionPayload;
    }

    public void fileUpload(ImageDto imageDto){
        RestTemplate restTemplate = new RestTemplate();

//        if(inputFile.isEmpty() || inputFile.getOriginalFilename() == null){
//            return;
//        }
//        System.out.println(inputFile.getOriginalFilename());
        if(imageDto.getImage() == null){
            return;
        }
        File file = new File(imageDto.getImage());
        // 파일을 resource 형태로 넣어야함!!!!!!
        Resource resource = new FileSystemResource(file);

        System.out.println(token);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(token); // pass generated token here
        MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
        bodyMap.add("file", resource); // convert file to ByteArrayOutputStream and pass with toByteArray() or pass with new File()
        bodyMap.add("filename", file.getName());
        bodyMap.add("initial_comment", "이미지 라벨링 검수입니다."); // pass comments with file
        bodyMap.add("channels", "project"); // pass channel codeID
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(bodyMap, headers);
        ResponseEntity<Object> responseEntity = restTemplate.postForEntity("https://slack.com/api/files.upload", entity,
                Object.class);

        System.out.println("파일 업로드 결과 확인 " + responseEntity);
    }


}
