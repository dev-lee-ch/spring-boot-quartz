package kr.co.ensecure.scheduler.base.api;

import java.util.Map;
import kr.co.ensecure.scheduler.base.util.MsgUtil;
import kr.co.ensecure.scheduler.base.util.web.HttpUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {
    
    private String url;
    private String code;
    private String msg;
    private Object result;
    private Object data;
    
    public static ApiResponse createApiResponse(Map<String, Object> resultMap) {
        String url = HttpUtil.getRequest().getRequestURL().toString();
        String code = "200";
        String msg = "";
        Object result = null;
        Object data = null;
        
        if (resultMap.get("code") != null) {
            code = (String) resultMap.get("code");
        }
        if (resultMap.get("result") != null) {
            result = resultMap.get("result");
        }
        if (resultMap.get("msg") != null && ((String) resultMap.get("msg")).length() > 0) {
            msg = (String) resultMap.get("msg");
        } else {
            if (resultMap.get("codeParam") != null) {
                // codeParam => type String[]
                Object[] objArr = (Object[]) resultMap.get("codeParam");
                msg = MsgUtil.getMessage(code, objArr);
            } else {
                msg = MsgUtil.getMessage(code);
            }
        }
        
        resultMap.remove("url");
        resultMap.remove("code");
        resultMap.remove("codeParam");
        resultMap.remove("msg");
        resultMap.remove("result");
        data = resultMap;
        
        return new ApiResponse(url, code, msg, result, data);        
    }
}
