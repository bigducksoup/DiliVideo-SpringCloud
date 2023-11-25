package com.ducksoup.dilivideotext.mainservices;


import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ducksoup.dilivideoentity.constant.CONSTANT_MinIO;
import com.ducksoup.dilivideoentity.dto.FileDeleteDTO;
import com.ducksoup.dilivideoentity.dto.FileInfo;
import com.ducksoup.dilivideoentity.dto.FileUploadDTO;
import com.ducksoup.dilivideoentity.result.ResponseResult;
import com.ducksoup.dilivideofeign.content.ContentServices;
import com.ducksoup.dilivideotext.entity.PostImgs;
import com.ducksoup.dilivideotext.service.PostImgsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.ServiceUnavailableException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
public class PostAssetsService {


    @Autowired
    private ContentServices contentServices;

    @Autowired
    private PostImgsService postImgsService;


    /**
     * 上传文件到 POST_IMG_BUCKET
     * @see CONSTANT_MinIO
     * @param files List<MultipartFile> files
     * @return Map<MultipartFile,FileInfo>
     * @see FileInfo
     * @throws ServiceUnavailableException 上传失败
     */
    public Map<MultipartFile,FileInfo> uploadFiles(List<MultipartFile> files) throws ServiceUnavailableException {

        Map<MultipartFile, FileInfo> map = new HashMap<>();

        if (files != null) {
            for (MultipartFile f : files) {
                FileUploadDTO fileUploadDTO = new FileUploadDTO();
                fileUploadDTO.setFile(f);
                fileUploadDTO.setBucketName(CONSTANT_MinIO.POST_IMG_BUCKET);
                ResponseResult<FileInfo> result = contentServices.uploadFile(fileUploadDTO);
                if (result.getCode() == 200) {
                    map.put(f, result.getData());
                } else {
                    log.error(f.getOriginalFilename() + "上传失败");
                    throw new ServiceUnavailableException();
                }
            }
        }

        return map;
    }


    @Transactional
    public void savePostImg(Map<MultipartFile,FileInfo> map,String moduleId){
        List<PostImgs> postImgs = new ArrayList<>();
        map.forEach((f, fileInfo) -> {
            PostImgs item = new PostImgs();
            item.setId(UUID.randomUUID().toString());
            item.setOriginalName(fileInfo.getOriginalName());
            item.setUniqueName(UUID.randomUUID().toString());
            item.setPath(fileInfo.getPath());
            item.setModuleId(moduleId);
            item.setBucket(CONSTANT_MinIO.POST_IMG_BUCKET);
            item.setUploadTime(fileInfo.getUploadTime());
            item.setSize(fileInfo.getSize());
            item.setState(1);
            item.setFullpath(fileInfo.getFullpath());
            item.setMd5(fileInfo.getMd5());
            postImgs.add(item);
        });

        if (!postImgs.isEmpty()){
            postImgsService.saveBatch(postImgs);
        }
    }


    //删除Post携带的图片
    public void deletePostImg(String moduleId){

        List<PostImgs> imgs = postImgsService.list(new LambdaQueryWrapper<PostImgs>()
                .eq(PostImgs::getModuleId, moduleId)
                .select(PostImgs::getBucket)
                .select(PostImgs::getPath));

        List<FileDeleteDTO> fileDeleteDTOS = new ArrayList<>();


        imgs.forEach(item->{
            FileDeleteDTO deleteDTO = new FileDeleteDTO();
            deleteDTO.setBucket(item.getBucket());
            deleteDTO.setPath(item.getPath());
            fileDeleteDTOS.add(deleteDTO);
        });

        log.info("共计"+fileDeleteDTOS.size()+"张图片待删除，moduleId:"+moduleId);

        contentServices.deleteFile(fileDeleteDTOS);

    }


}
