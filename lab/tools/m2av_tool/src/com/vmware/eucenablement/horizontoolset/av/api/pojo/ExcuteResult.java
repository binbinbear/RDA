package com.vmware.eucenablement.horizontoolset.av.api.pojo;

public class ExcuteResult {
    public final static int RES_SUCCESS = 0;
    public final static int RES_GENERAL_FAILURE = 1;
    public int resultFlag;
    public String message;

    public ExcuteResult() {
        resultFlag = RES_GENERAL_FAILURE;
        message = null;
    }

    public ExcuteResult(int resultInput, String msgInput) {
        this.resultFlag = resultInput;
        this.message = msgInput;
    }
}
