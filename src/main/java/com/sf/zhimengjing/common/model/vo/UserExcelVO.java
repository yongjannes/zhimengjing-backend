package com.sf.zhimengjing.common.model.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Title: UserExcelVO
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.model.vo
 * @description: 用户 Excel 导出视图对象（VO）
 */
@Data
public class UserExcelVO {

    @ExcelProperty("用户名")
    private String username;

    @ExcelProperty("昵称")
    private String nickname;

    @ExcelProperty("邮箱")
    private String email;

    @ExcelProperty("手机号")
    private String phone;

    @ExcelProperty("性别")
    private String gender;

    @ExcelProperty("用户等级")
    private String userLevel;

    @ExcelProperty("状态")
    private String status;

    @ExcelProperty("账户余额")
    private BigDecimal balance;

    @ExcelProperty("可用积分")
    private Integer availablePoints;

    @ExcelProperty("注册来源")
    private String registerSource;

    @ExcelProperty("注册时间")
    private LocalDateTime createTime;
}
