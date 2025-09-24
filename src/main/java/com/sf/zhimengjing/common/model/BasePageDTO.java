package com.sf.zhimengjing.common.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;


/**
 * @Title: BasePageDTO
 * @Author: 殇枫
 * @Package: package com.sf.zhimengjing.common.model
 * @Description: 分页查询基础DTO
 */
@Data
@Schema(description = "分页查询基础DTO")
public class BasePageDTO {

    @Schema(description = "当前页码", defaultValue = "1")
    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNum = 1;

    @Schema(description = "每页数量", defaultValue = "10")
    @Min(value = 1, message = "每页数量不能小于1")
    @Max(value = 100, message = "每页数量不能超过100")
    private Integer pageSize = 10;
}