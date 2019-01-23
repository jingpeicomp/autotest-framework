package com.qianmi.autotest.base.common;

import lombok.*;

/**
 * 异常基类
 * Created by liuzhaoming on 16/9/26.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AutotestException extends RuntimeException {
    /**
     * 异常码
     */
    private int code;

    /**
     * 异常信息
     */
    @NonNull
    private String message;

}
