package com.ducksoup.dilivideotext.controller.params;


import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CommentDeleteParams {

    @NotNull
    private String commentId;

}
