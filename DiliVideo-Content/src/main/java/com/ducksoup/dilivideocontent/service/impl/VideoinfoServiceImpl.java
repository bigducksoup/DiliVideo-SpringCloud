package com.ducksoup.dilivideocontent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ducksoup.dilivideocontent.aop.annonation.PerformanceLog;
import com.ducksoup.dilivideocontent.entity.Cover;
import com.ducksoup.dilivideocontent.entity.Videoinfo;
import com.ducksoup.dilivideocontent.mainservices.UserOperation.LikeOperationService;
import com.ducksoup.dilivideocontent.mainservices.UserOperation.ViewsService;
import com.ducksoup.dilivideocontent.service.CoverService;
import com.ducksoup.dilivideocontent.service.VideoinfoService;
import com.ducksoup.dilivideocontent.mapper.VideoinfoMapper;
import com.ducksoup.dilivideocontent.utils.OSSUtils;
import com.ducksoup.dilivideoentity.constant.CONSTANT_STATUS;
import com.ducksoup.dilivideoentity.vo.VideoInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author meichuankutou
* @description 针对表【ct_videoinfo】的数据库操作Service实现
* @createDate 2023-03-08 19:16:37
*/
@Slf4j
@Service
public class VideoinfoServiceImpl extends ServiceImpl<VideoinfoMapper, Videoinfo>
    implements VideoinfoService{

    @Autowired
    private CoverService coverService;


    @Autowired
    private OSSUtils ossUtils;

    @Resource
    private ViewsService viewsService;

    @Resource
    private LikeOperationService likeOperationService;


    @PerformanceLog
    @Override
    public List<VideoInfoVo> getVideoInfoVoByVideoInfo(List<Videoinfo> videoinfos) {



        List<VideoInfoVo> videoInfoVos = new ArrayList<>();

        if (videoinfos.size()==0){
            return videoInfoVos;
        }

        List<String> coverIds = videoinfos.stream().map(Videoinfo::getCoverId).collect(Collectors.toList());


        //直接查所有cover避免for循环查询
        List<Cover> covers = coverService.list(new LambdaQueryWrapper<Cover>().in(Cover::getId, coverIds));

        //根据视频id分类
        Map<String, List<Cover>> collect = covers.stream().collect(Collectors.groupingBy(c -> {
            for (Videoinfo item : videoinfos) {
                if (c.getId().equals(item.getCoverId())) {
                    return item.getId();
                }
            }
            return "none";
        }));


        videoinfos.forEach((item)->{
//            Cover cover = coverService.getOne(new LambdaQueryWrapper<Cover>().eq(Cover::getId, item.getCoverId()));
            Cover cover = collect.get(item.getId()).get(0);
            VideoInfoVo videoInfoVo = new VideoInfoVo();
            videoInfoVo.setVideoInfoId(item.getId());
            videoInfoVo.setVideoAuthorName(item.getAuthorName());
            videoInfoVo.setVideoAuthorId(item.getAuthorid());
            videoInfoVo.setCollectCount(item.getCollectCount());
            videoInfoVo.setCommentCount(item.getCommentCount());
            videoInfoVo.setCreateTime(item.getCreateTime());
            videoInfoVo.setIsOriginal(item.getIsOriginal());
            videoInfoVo.setWatchCount(item.getWatchCount());
            videoInfoVo.setLikeCount(item.getLikeCount());
            videoInfoVo.setIsPublish(item.getIsPublish());
            videoInfoVo.setOpenComment(item.getOpenComment());
            videoInfoVo.setTitle(item.getTitle());
            videoInfoVo.setSummary(item.getSummary());
            videoInfoVo.setVideoFileUrl("null");
            videoInfoVo.setVideoFileName("null");
            videoInfoVo.setCoverId(item.getCoverId());
            videoInfoVo.setCoverName(cover.getUniqueName());

            String url = ossUtils.makeUrl(cover.getBucket(), cover.getPath());

            videoInfoVo.setCoverUrl(url);
            videoInfoVo.setVideoFileId(item.getVideofileId());
            videoInfoVo.setPartitionId(item.getPartitionId());
            videoInfoVos.add(videoInfoVo);

        });




        viewsService.setVideoListViewCount(videoInfoVos);
        likeOperationService.setVideoLikeStatus(videoInfoVos);


        return videoInfoVos;
    }


    @Override
    public List<VideoInfoVo> getPublishedVideoById(String userId,int page,int pageSize) {


        Page<Videoinfo> pager = new Page<>(page,pageSize);

        this.page(pager,new LambdaQueryWrapper<Videoinfo>().eq(Videoinfo::getStatus,1).eq(Videoinfo::getMarkStatus, CONSTANT_STATUS.VIDEO_STATUS_READY).eq(Videoinfo::getAuthorid,userId).orderByDesc(Videoinfo::getCreateTime));

        List<Videoinfo> videoinfos = pager.getRecords();


        return this.getVideoInfoVoByVideoInfo(videoinfos);
    }
}




