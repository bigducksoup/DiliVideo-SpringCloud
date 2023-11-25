package com.ducksoup.dilivideocontent.enums;

public enum MISSION_CODE {


    CREATED(1),
    HANDLING(2),
    FINISHED(3),
    FAILED(4);



    public final Integer code;

    MISSION_CODE(Integer code){
        this.code = code;
    }

}
