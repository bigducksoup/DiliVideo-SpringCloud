package com.ducksoup.dilivideocontent.controller;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.http.HttpStatus;
import com.ducksoup.dilivideocontent.controller.Params.MissionDoneCallbackParams;
import com.ducksoup.dilivideocontent.controller.Params.UploadChunkParams;
import com.ducksoup.dilivideocontent.controller.Params.UploadMissionParams;
import com.ducksoup.dilivideocontent.entity.VideoUploadMission;
import com.ducksoup.dilivideocontent.mainservices.Video.Impl.VideoUploadService;
import com.ducksoup.dilivideocontent.mainservices.cover.CoverManageService;
import com.ducksoup.dilivideocontent.vo.ChunkUploadInfo;
import com.ducksoup.dilivideoentity.result.ResponseResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/v2/video_upload")
@AllArgsConstructor
public class VideoUploadController {



    private final VideoUploadService videoUploadService;

    private final CoverManageService coverManageService;
    /**
     * 创建分片上传任务
     * @param params UploadMissionParams
     * @return 任务Id
     */
    @SaCheckLogin
    @PostMapping("/create_upload_mission")
    public ResponseResult<String> createUploadMission(@RequestBody @Valid UploadMissionParams params){

        VideoUploadMission mission = videoUploadService.createUploadMission(params);

        return new ResponseResult<>(HttpStatus.HTTP_OK,"上传任务创建成功",mission.getId());

    }


    /**
     * 获取单个分片的上传地址
     * @param params UploadChunkParams
     * @return ChunkUploadInfo
     */
    @SaCheckLogin
    @PostMapping("/get_chunk_upload_url")
    public ResponseResult<ChunkUploadInfo> getChunkUploadUrl(@RequestBody @Valid UploadChunkParams params){

        try {
            ChunkUploadInfo chunkUploadInfo = videoUploadService.getChunkUploadInfo(params);
            return new ResponseResult<>(HttpStatus.HTTP_OK,"获取分片上传地址成功",chunkUploadInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 获取多个分片的上传地址
     * @param paramsList List<UploadChunkParams>
     * @return List<ChunkUploadInfo>
     */
    @SaCheckLogin
    @PostMapping("/get_multi_chunk_upload_url")
    public ResponseResult<Map<Integer,ChunkUploadInfo>> getChunkUploadUrl(@RequestBody @Valid List<UploadChunkParams> paramsList){

        try {

            Map<Integer,ChunkUploadInfo> map = new HashMap<>();

            for (UploadChunkParams params : paramsList) {
                log.info("检查并获取分片上传地址,total={},index={},md5={}",params.getTotal(),params.getIndex(),params.getMd5());
                ChunkUploadInfo uploadInfo = videoUploadService.getChunkUploadInfo(params);
                map.put(params.getIndex(),uploadInfo);
            }
            return new ResponseResult<>(HttpStatus.HTTP_OK,"获取分片上传地址成功",map);
        }catch (Exception e){
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }

    }






    @SaCheckLogin
    @PostMapping("/mission_done_callback")
    public ResponseResult<Boolean> missionDoneCallBack(@RequestBody @Valid MissionDoneCallbackParams params){


        log.info("{} call back",params.getMissionId());

        boolean done = videoUploadService.handleCallBack(params);
        if (!done){
            return new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR,"回调失败", false);
        }
        return new ResponseResult<>(HttpStatus.HTTP_OK,"回调成功", true);


    }


    @SaCheckLogin
    @GetMapping("/get_cover_upload_url")
    public ResponseResult<String> getCoverUploadUrl(@RequestParam String missionId, @RequestParam String type){

        try {
            return new ResponseResult<>(HttpStatus.HTTP_OK,"获取成功",coverManageService.getCoverUploadUrl(missionId,type));
        } catch (Exception e) {
            return new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR,"获取失败",e.getMessage());
        }

    }


}
