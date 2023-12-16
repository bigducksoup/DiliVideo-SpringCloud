package utils

import (
	"crypto/md5"
	"fmt"
	"io"
	"os"
)

func GetMd5(path string) (md5res string) {

	file, err := os.Open(path)

	defer file.Close()

	if err != nil {
		panic(err)
	}

	h := md5.New()

	io.Copy(h, file)

	md5Hash := h.Sum(nil)
	md5res = fmt.Sprintf("%x", md5Hash)
	return

}
