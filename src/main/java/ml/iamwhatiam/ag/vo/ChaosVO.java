package ml.iamwhatiam.ag.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Map;

/**
 * 混乱的HTTP入参
 * @author iMinusMinus
 * @date 2019-08-30
 */
@Getter
@Setter
@ToString
public class ChaosVO implements Serializable {

    private static final long serialVersionUID = -4124786294321638070L;

    /**
     * HTTP header
     */
    private Map<String, Object> headers;

    /**
     * HTTP path
     */
    private String[] paths;

    /**
     * HTTP query string
     */
    private Map<String, Object> queryString;

    /**
     * HTTP body
     */
    private String body;
}
