package com.aeye.thirdeye.api;

import com.aeye.thirdeye.dto.ProjectDto;
import com.aeye.thirdeye.service.ProjectService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequiredArgsConstructor
@Api(tags = "프로젝트 관련 API")
public class ProjectAPIController {

    private final ProjectService projectService;

    @PostMapping("/admin/project/add")
    @ApiOperation(value = "프로젝트 추가", notes = "")
    @ApiResponses({

    })
    public ResponseEntity<?> insertProject(ProjectDto projectDto,
                                           HttpServletRequest request){



        return ResponseEntity.status(HttpStatus.OK).body("ADD Project Success");
    }

}
