package com.ducksoup.dilivideomain.controller.params;


import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CommentDeleteParams {

    @NotNull
    private String commentId;

}
