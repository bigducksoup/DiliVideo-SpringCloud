package com.ducksoup.dilivideocontent.controller.Params;


import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class MissionDoneCallbackParams {

    @NotNull
    private String title;

    @NotNull
    //分区Id
    private String partitionId;

    @NotNull
    //是否原创
    private boolean ifOriginal;

    @NotNull
    private String description;

    @NotNull
    private String authorId;

    @NotNull
    private String missionId;

    @NotNull
    @Size(min = 0, max = 30)
    private List<String> tagIds;
}
