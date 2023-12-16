package file

import (
	"fmt"
	"io"
	"os"
)

func ToSlice(path string) (slicePaths []string) {

	chunkSize := 5 * 1024 * 1024

	file, err := os.Open(path)

	if err != nil {
		panic(err)
	}

	buffer := make([]byte, chunkSize)

	for i := 0; ; i++ {

		n, err := file.Read(buffer)

		if err != nil && err != io.EOF {
			panic(err)
		}

		if n == 0 {
			break
		}

		chunkFileName := fmt.Sprintf("%s.%d", file.Name(), i)

		chunk, err := os.Create(chunkFileName)

		if err != nil {
			panic(err)
		}

		chunk.Write(buffer[:n])

		slicePaths = append(slicePaths, chunk.Name())

	}

	return

}
