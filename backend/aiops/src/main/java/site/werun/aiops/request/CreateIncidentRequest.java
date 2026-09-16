package site.werun.aiops.request;

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
    private String serviceName;

    /**
     * 环境.
     */
    private String environment;

    /**
     * 告警标题.
     */
    private String title;

    /**
     * 原始告警内容.
     */
    private String rawAlert;
}
