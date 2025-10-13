package com.sf.zhimengjing.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sf.zhimengjing.common.annotation.Log;
import com.sf.zhimengjing.common.model.dto.CommunityPostAuditDTO;
import com.sf.zhimengjing.common.model.dto.CommunityPostQueryDTO;
import com.sf.zhimengjing.common.model.vo.CommunityPostListVO;
import com.sf.zhimengjing.common.model.vo.CommunityPostStatisticsVO;
import com.sf.zhimengjing.common.result.Result;
import com.sf.zhimengjing.service.admin.CommunityPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Title: CommunityPostController
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.controller.admin
 * @Description: 社区帖子管理控制器
 */
@RestController
@RequestMapping("/admin/community/posts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('community:post')")
@Tag(name = "社区帖子管理", description = "社区帖子管理相关接口")
public class CommunityPostController {

    private final CommunityPostService communityPostService;

    @GetMapping("/list")
    @Operation(summary = "分页查询帖子列表")
    @Log(module = "社区帖子管理", operation = "查询帖子列表")
    public Result<IPage<CommunityPostListVO>> getPostList(CommunityPostQueryDTO queryDTO) {
        return Result.success(communityPostService.getPostListPage(queryDTO));
    }

    @GetMapping("/statistics")
    @Operation(summary = "查询帖子统计信息")
    @Log(module = "社区帖子管理", operation = "获取统计信息")
    public Result<CommunityPostStatisticsVO> getStatistics() {
        return Result.success(communityPostService.getPostStatistics());
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询帖子详情")
    @Log(module = "社区帖子管理", operation = "查询帖子详情")
    public Result<CommunityPostListVO> getPostDetail(@PathVariable Long id) {
        return Result.success(communityPostService.getPostDetail(id));
    }

    @PostMapping("/audit")
    @Operation(summary = "审核帖子")
    @Log(module = "社区帖子管理", operation = "审核帖子")
    public Result<String> auditPosts(@Validated @RequestBody CommunityPostAuditDTO auditDTO) {
        communityPostService.auditPosts(auditDTO);
        return Result.success("审核成功");
    }

    @DeleteMapping("/{ids}")
    @Operation(summary = "批量删除帖子")
    @Log(module = "社区帖子管理", operation = "删除帖子")
    public Result<String> deletePosts(@PathVariable List<Long> ids) {
        communityPostService.deletePosts(ids);
        return Result.success("删除成功");
    }

    @PutMapping("/toggle-top/{id}")
    @Operation(summary = "切换置顶状态")
    @Log(module = "社区帖子管理", operation = "切换置顶状态")
    public Result<String> toggleTop(@PathVariable Long id) {
        communityPostService.toggleTop(id);
        return Result.success("操作成功");
    }

    @PutMapping("/toggle-hot/{id}")
    @Operation(summary = "切换热门状态")
    @Log(module = "社区帖子管理", operation = "切换热门状态")
    public Result<String> toggleHot(@PathVariable Long id) {
        communityPostService.toggleHot(id);
        return Result.success("操作成功");
    }
}