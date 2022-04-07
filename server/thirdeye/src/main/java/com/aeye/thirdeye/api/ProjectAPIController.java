package com.aeye.thirdeye.api;

import com.aeye.thirdeye.dto.ProjectDto;
import com.aeye.thirdeye.dto.response.SimpleProjectDto;
import com.aeye.thirdeye.entity.Project;
import com.aeye.thirdeye.service.ProjectService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@Api(tags = "프로젝트 관련 API")
public class ProjectAPIController {

    private final ProjectService projectService;

    @PostMapping("/admin/project/add")
    @ApiOperation(value = "프로젝트 추가", notes = "")
    @ApiResponses({
            @io.swagger.annotations.ApiResponse(code = 201, message = "프로젝트 추가 성공"),
            @io.swagger.annotations.ApiResponse(code = 400, message = "프로젝트 추가 실패")
    })
    public ResponseEntity<?> insertProject(ProjectDto projectDto){

        Project result = projectService.insertProject(projectDto);

        if(result == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body("ADD Project Success");
    }

    @GetMapping("/admin/project/list")
    @ApiOperation(value = "프로젝트 목록 조회", notes = "파라미터 page, size 입력 (default page = 0, size = 20)")
    @ApiResponses({
            @io.swagger.annotations.ApiResponse(code = 200, message = "프로젝트 목록 조회 성공"),
            @io.swagger.annotations.ApiResponse(code = 400, message = "프로젝트 목록 조회 실패")
    })
    public ResponseEntity<?> getProjects(@PageableDefault(size=20, sort={"goal", "id"}, direction = Sort.Direction.DESC) Pageable pageable){

        List<Project> result = projectService.getProjects(pageable);

        if(result == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        List<SimpleProjectDto> resultProjects = result.stream().map(b -> new SimpleProjectDto(b)).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(resultProjects);
    }

}
