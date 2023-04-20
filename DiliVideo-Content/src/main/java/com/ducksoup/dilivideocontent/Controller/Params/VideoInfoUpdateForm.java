package com.ducksoup.dilivideocontent.Controller.Params;

import lombok.Data;


/**
 *     "title": "",
 *     "partitionId": "",
 *     "iforiginal": false,
 *     "description": "",
 */

@Data
public class VideoInfoUpdateForm {
    private String id;

    private String title;

    private String partitionId;

    private boolean iforiginal;

    private String description;

}
