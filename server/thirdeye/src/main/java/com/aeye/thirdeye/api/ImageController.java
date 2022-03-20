package com.aeye.thirdeye.api;

import com.aeye.thirdeye.dto.ImageDto;
import com.aeye.thirdeye.service.SlackImageService;
import com.slack.api.Slack;
import com.slack.api.app_backend.interactive_components.ActionResponseSender;
import com.slack.api.app_backend.interactive_components.payload.BlockActionPayload;
import com.slack.api.app_backend.interactive_components.response.ActionResponse;
import com.slack.api.model.ModelConfigurator;
import com.slack.api.model.block.ImageBlock;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.util.json.GsonFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.composition.BlockCompositions.markdownText;
import static com.slack.api.model.block.composition.BlockCompositions.plainText;
import static com.slack.api.model.block.element.BlockElements.*;
import static com.slack.api.webhook.WebhookPayloads.payload;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ImageController {

    private final SlackImageService slackImageService;

    @Value("${notification.slack.webhook.url}")
    private String url;

    @PostMapping("/inspect/request")
    public ResponseEntity<?> inspectRequest(@RequestPart(value = "image", required = false) MultipartFile file,
                                  @RequestBody ImageDto imageDto){
        List<LayoutBlock> layoutBlocks = slackImageService.makeRequestLayout(imageDto);

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

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    // Slack 버튼 결과 여기로 받아서 처리
    @RequestMapping(value = "/inspect/response", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> inspectResponse(@RequestParam String payload){

        BlockActionPayload blockActionPayload = slackImageService.makeResponseLayout(payload);
        ActionResponse response = null;
        String nowActionId = blockActionPayload.getActions().get(0).getActionId();
        if(nowActionId.equals("typeAaction")
                || nowActionId.equals("typeBaction")
                || nowActionId.equals("typeCaction")){

            response =
                    ActionResponse.builder()
                            .replaceOriginal(true) // 변경은되는데 여기서 변경이 안돼서
                            .blocks(blockActionPayload.getMessage().getBlocks())
                            .build();
        }
        else {
            response =
                    ActionResponse.builder()
                            .replaceOriginal(true) // 변경은되는데 여기서 변경이 안돼서
                            .deleteOriginal(true) // 삭제로 구현해놓음
                            .blocks(blockActionPayload.getMessage().getBlocks())
                            .build();
        }

        Slack slack = Slack.getInstance();
        ActionResponseSender sender = new ActionResponseSender(slack);
        try {
            sender.send(blockActionPayload.getResponseUrl(), response);
        }catch (IOException e){
            e.printStackTrace();
        }

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }


    // 슬랙 검수 코드 (테스트)
    @PostMapping("/slack/request")
    public ResponseEntity<?> reallytest(){
        List<LayoutBlock> layoutBlocks = slackImageService.makeLayout();

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

        return null;
    }


    // Slack 버튼 결과 여기로 받음 일단 (테스트)
    @RequestMapping(value = "/slack/response", method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> test12(@RequestParam String payload) throws IOException {

        BlockActionPayload blockActionPayload = slackImageService.responseSlackLayout(payload);
        ActionResponse response = null;
        String nowActionId = blockActionPayload.getActions().get(0).getActionId();
        if(nowActionId.equals("typeAaction")
                || nowActionId.equals("typeBaction")
                || nowActionId.equals("typeCaction")){

            response =
                    ActionResponse.builder()
                            .replaceOriginal(true) // 변경은되는데 여기서 변경이 안돼서
                            .blocks(blockActionPayload.getMessage().getBlocks())
                            .build();
        }
        else {
            response =
                    ActionResponse.builder()
                            .replaceOriginal(true) // 변경은되는데 여기서 변경이 안돼서
                            .deleteOriginal(true) // 삭제로 구현해놓음
                            .blocks(blockActionPayload.getMessage().getBlocks())
                            .build();
        }

        Slack slack = Slack.getInstance();
        ActionResponseSender sender = new ActionResponseSender(slack);
        sender.send(blockActionPayload.getResponseUrl(), response);

        return ResponseEntity.status(HttpStatus.OK).body("크허허허허");
    }

}
