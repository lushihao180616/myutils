package com.lushihao.myutils.response;

import com.lushihao.myutils.json.LSHJsonUtils;
import com.lushihao.myutils.response.vo.LSHResponse;

public final class LSHResponseUtils {

    /**
     * 返回数据
     *
     * @param response
     * @return
     */
    public static String getResponse(LSHResponse response) {
        return LSHJsonUtils.bean2Json(response);
    }

}