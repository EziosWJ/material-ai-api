package cn.ezios.baseapi.modules.ai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PythonAiErrorResponse {

    private String error;

    private String detail;
}
