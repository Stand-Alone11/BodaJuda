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
                section.text(markdownText("*?????????* : " + "*" + imageDto.getTitle() + "*" ))));

        layoutBlocks.add(section(section ->
                section.text(markdownText("*?????????* : " + "*" + imageDto.getProvider() + "*" ))));

        layoutBlocks.add(section(section ->
                section.text(markdownText("*?????????* : " + "*" + imageDto.getTypeA() + "*" ))));

        layoutBlocks.add(section(section ->
                section.text(markdownText("*?????????* : " + "*" + imageDto.getTypeB() + "*" ))));

        layoutBlocks.add(section(section ->
                section.text(markdownText("*?????????* : " + "*" + imageDto.getTypeC() + "*" ))));
//
//        layoutBlocks.add(
//                input(input -> input.element(
//                                plainTextInput(p -> p
//                                        .actionId("typeAaction")
//                                        .placeholder(plainText("???????????? ??????????????????"))
//                                )
//                        ).label(
//                                plainText(pt -> pt.text("?????????").emoji(true))
//                        ).dispatchAction(true)
//                )
//        );
//
//        layoutBlocks.add(
//                input(input -> input.element(
//                                plainTextInput(p -> p
//                                        .actionId("typeBaction")
//                                        .placeholder(plainText("???????????? ??????????????????"))
//                                )
//                        ).label(
//                                plainText(pt -> pt.text("?????????").emoji(true))
//                        ).dispatchAction(true)
//                )
//        );
//
//        layoutBlocks.add(
//                input(input -> input.element(
//                                plainTextInput(p -> p
//                                        .actionId("typeCaction")
//                                        .placeholder(plainText("???????????? ??????????????????"))
//                                )
//                        ).label(
//                                plainText(pt -> pt.text("?????????").emoji(true))
//                        ).dispatchAction(true)
//                )
//        );
        // ?????? / ?????? ????????? ??????
        layoutBlocks.add(
                section(section -> section.text(markdownText("?????? / ??????"))
                        .accessory(radioButtons( radio -> radio.options(asOptions(
                                                        option(o -> o.text(plainText(p->p.text("??????").emoji(true))).value("??????")),
                                                        option(o -> o.text(plainText(p->p.text("??????").emoji(true))).value("??????"))
                                                )
                                        ).actionId("typeDaction")
                                )
                        )

                )
        );

        // Action??? ???????????? ???????????? ?????? Divider
        layoutBlocks.add(divider());
        // ActionBlock??? ?????? ????????? ?????? ????????? ??????
        layoutBlocks.add(
                actions(actions -> actions
                        .elements(asElements(
                                button(b -> b.text(plainText(pt -> pt.emoji(true).text("??????")))
                                        .value(Long.toString(imageDto.getId()))
                                        .style("primary")
                                        .actionId("action_approve")
                                ),
                                button(b -> b.text(plainText(pt -> pt.emoji(true).text("??????")))
                                        .value(Long.toString(imageDto.getId()))
                                        .style("danger")
                                        .actionId("action_reject")
                                )
                        ))
                )
        );

        // ?????? ?????? payload Slack??? ??????
        try {
            Slack.getInstance().send(url,
                    payload(p -> p
                            .text("????????? ???????????? ???????????? ???????????????.")
                            .blocks(layoutBlocks)
                    )
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // ????????? ?????? ??? ??????, ????????? ?????? ?????? Layout ?????? ??? DB ??????
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
                // ??? ?????? divider ?????? ?????? ??????
                if (len > 1) {
                    blockActionPayload.getMessage().getBlocks().subList(1, len).clear();
                }
                blockActionPayload.getMessage().getBlocks().add(1,
                        section(section -> section.text(markdownText("*?????? ??????*"))));
            } else if(action.getActionId().equals("action_approve")) {
                int seq = Integer.parseInt(action.getValue());

                Image image = imageRepository.findById(Long.valueOf(seq)).orElse(null);
                System.out.println("????????? : " + Long.valueOf(seq));
                if(image != null){
                    // ???????????? accepted ?????? ??????
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
                // ??? ?????? divider ?????? ?????? ??????
                if (len > 1) {
                    blockActionPayload.getMessage().getBlocks().subList(1, len).clear();
                }
                blockActionPayload.getMessage().getBlocks().add(1,
                        section(section -> section.text(markdownText("*?????? ??????*"))));
            }
//            else if(action.getActionId().equals("typeAaction")){
//                blockActionPayload.getMessage().getBlocks().remove(3);
//                blockActionPayload.getMessage().getBlocks().add(3,
//                        section(section -> section.text(markdownText("????????? : " + "*" + action.getValue() + "*"))));
//            }
//            else if(action.getActionId().equals("typeBaction")){
//                blockActionPayload.getMessage().getBlocks().remove(4);
//                blockActionPayload.getMessage().getBlocks().add(4,
//                        section(section -> section.text(markdownText("????????? : " + "*" + action.getValue() + "*"))));
//            }
//            else if(action.getActionId().equals("typeCaction")){
//                blockActionPayload.getMessage().getBlocks().remove(5);
//                blockActionPayload.getMessage().getBlocks().add(5,
//                        section(section -> section.text(markdownText("????????? : " + "*" + action.getValue() + "*"))));
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
        // ????????? resource ????????? ????????????!!!!!!
        Resource resource = new FileSystemResource(file);

        System.out.println(token);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(token); // pass generated token here
        MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
        bodyMap.add("file", resource); // convert file to ByteArrayOutputStream and pass with toByteArray() or pass with new File()
        bodyMap.add("filename", file.getName());
        bodyMap.add("initial_comment", "????????? ????????? ???????????????."); // pass comments with file
        bodyMap.add("channels", "project"); // pass channel codeID
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(bodyMap, headers);
        ResponseEntity<Object> responseEntity = restTemplate.postForEntity("https://slack.com/api/files.upload", entity,
                Object.class);

        System.out.println("?????? ????????? ?????? ?????? " + responseEntity);
    }


}
