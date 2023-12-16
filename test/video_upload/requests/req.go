package requests

import (
	"bytes"
	"encoding/json"
	"io"
	"log"
	"net/http"
	"os"
	"test/utils"
	"time"
)

type Response[T any] struct {
	Code int    `json:"code"`
	Msg  string `json:"msg"`
	Data T      `json:"data"`
}

var token string = "1867ff59-33c1-4610-a9ae-755fe5e12400"

func CreateMission(total int, fileType string, md5 string, size int64) string {

	url := "http://127.0.0.1:8084/content/v2/video_upload/create_upload_mission"

	reqbody := map[string]any{
		"total":     total,
		"fileType":  "mp4",
		"md5":       md5,
		"size":      size,
		"timestamp": time.Now().Unix(),
	}

	data, _ := json.Marshal(reqbody)

	req, err := http.NewRequest("POST", url, bytes.NewReader(data))

	if err != nil {
		panic(err)
	}

	req.Header.Set("Content-Type", "application/json")
	req.Header.Set("token", token)

	resp, err := http.DefaultClient.Do(req)

	if err != nil {
		panic(err)
	}

	bodyData, err := io.ReadAll(resp.Body)

	if err != nil {
		panic(err)
	}

	response := Response[string]{}

	err = json.Unmarshal(bodyData, &response)

	if err != nil {
		panic(err)
	}

	return response.Data

}

func GetChunkUploadUrls(paths []string, missionId string) map[int]map[string]any {

	url := "http://127.0.0.1:8084/content/v2/video_upload/get_multi_chunk_upload_url"

	reqBody := []map[string]any{}

	for i, v := range paths {

		file, err := os.Stat(v)
		if err != nil {
			panic(err)
		}

		reqBody = append(reqBody, map[string]any{
			"index":     i,
			"missionId": missionId,
			"md5":       utils.GetMd5(v),
			"size":      file.Size(),
			"timestamp": time.Now().Unix(),
			"fileName":  file.Name(),
			"total":     len(paths),
		})

	}

	data, err := json.Marshal(reqBody)

	if err != nil {
		panic(err)
	}

	req, err := http.NewRequest("POST", url, bytes.NewReader(data))
	if err != nil {
		panic(err)
	}

	req.Header.Set("Content-Type", "application/json")
	req.Header.Set("token", token)

	resp, err := http.DefaultClient.Do(req)

	if err != nil {
		panic(err)
	}

	bodyData, err := io.ReadAll(resp.Body)

	if err != nil {
		panic(err)
	}

	response := Response[map[int]map[string]any]{}

	err = json.Unmarshal(bodyData, &response)

	if err != nil {
		panic(err)
	}

	return response.Data

}

func MuliUpload(paths []string, missionId string, urlMap map[int]map[string]any) {

	for i, v := range paths {

		info := urlMap[i]

		if info["exists"].(bool) {
			continue
		}

		content, err := os.ReadFile(v)

		if err != nil {
			panic(err)
		}

		url := info["url"].(string)

		req, err := http.NewRequest("PUT", url, bytes.NewReader(content))

		if err != nil {
			panic(err)
		}

		resp, err := http.DefaultClient.Do(req)

		if err != nil {
			panic(err)
		}

		if resp.StatusCode != 200 {
			panic(resp.Status)
		}

	}

}

func CallBack(missionId string) {

	url := "http://127.0.0.1:8084/content/v2/video_upload/mission_done_callback"

	params := map[string]any{
		"title":       "这是一个前端文件分片上传的测试",
		"partitionId": 1,
		"ifOriginal":  true,
		"description": "test multi chunk upload",
		"authorId":    1,
		"missionId":   missionId,
		"tagIds":      []string{},
	}

	bodyData, err := json.Marshal(params)

	if err != nil {
		panic(err)
	}

	req, err := http.NewRequest("POST", url, bytes.NewReader(bodyData))

	if err != nil {
		panic(err)
	}

	req.Header.Set("Content-Type", "application/json")
	req.Header.Set("token", token)

	resp, err := http.DefaultClient.Do(req)

	if err != nil {
		panic(err)
	}

	respData, err := io.ReadAll(resp.Body)

	if err != nil {
		panic(err)
	}

	log.Println(string(respData))
}
