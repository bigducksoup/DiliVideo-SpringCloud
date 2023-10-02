package com.ducksoup.dilivideocontent.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChunkFile {

    File chunk;

    Integer index;

}
