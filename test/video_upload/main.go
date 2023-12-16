package main

import (
	"os"
	"test/utils"
	"test/video_upload/file"
	"test/video_upload/requests"
)

func main() {

	path := "/path/to/a/video/file"

	// 文件分片
	paths := file.ToSlice(path)

	fileMd5 := utils.GetMd5(path)

	info, _ := os.Stat(path)

	//创建任务
	missionId := requests.CreateMission(len(paths), "mp4", fileMd5, info.Size())

	urlMap := requests.GetChunkUploadUrls(paths, missionId)

	requests.MuliUpload(paths, missionId, urlMap)

	requests.CallBack(missionId)

}
