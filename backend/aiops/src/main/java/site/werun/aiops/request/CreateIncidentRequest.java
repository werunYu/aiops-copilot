package site.werun.aiops.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * @author werun
 * @version 1.0
 * @date 2026/09/16 13:39
 * @description
 **/
@Data
public class CreateIncidentRequest implements Serializable {

    /**
     * 服务名称.
     */
    @NotBlank(message = "服务名称不能为空")
    @Size(max = 100, message = "服务名称长度不能超过100个字符")
    private String serviceName;

    /**
     * 环境.
     */
    @NotBlank(message = "环境不能为空")
    @Size(max = 50, message = "环境长度不能超过50个字符")
    private String environment;

    /**
     * 告警标题.
     */
    @NotBlank(message = "告警标题不能为空")
    @Size(max = 255, message = "告警标题长度不能超过255个字符")
    private String title;

    /**
     * 原始告警内容.
     */
    @NotBlank(message = "告警内容不能为空")
    @Size(max = 10000, message = "告警内容长度不能超过10000个字符")
    private String rawAlert;
}
