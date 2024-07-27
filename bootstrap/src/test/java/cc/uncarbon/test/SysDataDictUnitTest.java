package cc.uncarbon.test;

import cc.uncarbon.framework.core.context.UserContext;
import cc.uncarbon.framework.core.context.UserContextHolder;
import cc.uncarbon.framework.core.enums.EnabledStatusEnum;
import cc.uncarbon.framework.core.page.PageParam;
import cc.uncarbon.framework.core.page.PageResult;
import cc.uncarbon.module.HelioBootApplication;
import cc.uncarbon.module.sys.model.request.AdminSysDataDictClassifiedInsertOrUpdateDTO;
import cc.uncarbon.module.sys.model.request.AdminSysDataDictClassifiedListDTO;
import cc.uncarbon.module.sys.model.request.AdminSysDataDictItemInsertOrUpdateDTO;
import cc.uncarbon.module.sys.model.request.AdminSysDataDictItemListDTO;
import cc.uncarbon.module.sys.model.response.SysDataDictClassifiedBO;
import cc.uncarbon.module.sys.model.response.SysDataDictItemBO;
import cc.uncarbon.module.sys.service.SysDataDictService;
import cn.hutool.core.collection.CollUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

// 因为部分单元测试方法，需要依赖前置单元测试的结果，指定为只创建一个测试类实例
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
// 控制单元测试方法执行顺序
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//                      👇 这里需要改成相应启动类 👇
@SpringBootTest(classes = HelioBootApplication.class)
@Slf4j
class SysDataDictUnitTest {

    @Resource
    private SysDataDictService sysDataDictService;

    Long classifiedId;
    List<SysDataDictItemBO> items;

    /**
     * 用于比较的数据字典项条数
     */
    int compareAllItemSize = EnabledStatusEnum.class.getEnumConstants().length;
    int compareEnabledItemSize = 0;


    /**
     * 单元测试-初始化
     */
    @BeforeAll
    public static void init() {
        // 可以在这里进行初始化操作, 如设置用户上下文等
        UserContextHolder.setUserContext(
                UserContext.builder()
                        .userId(1L)
                        .userName("admin")
                        .build()
        );

        // 指定租户
        /*TenantContextHolder.setTenantContext(
                new TenantContext()
                        .setTenantId()
                        .setTenantName()
        );*/
    }

    /**
     * 单元测试-后台新增数据字典分类
     */
    @Order(0)
    @Test
    void testAdminInsertClassified() {
        Long entityId = sysDataDictService.adminInsertClassified(
                AdminSysDataDictClassifiedInsertOrUpdateDTO.builder()
                        // 分类编码
                        .code("enabled-status1")
                        // 分类名称
                        .name("启用状态1")
                        // 状态
                        .status(EnabledStatusEnum.DISABLED)
                        // 分类描述
                        .description(null)
                        .build()
        );

        log.info("\n\n\n新增成功 >> 新主键ID={}", entityId);
        classifiedId = entityId;
    }

    /**
     * 单元测试-后台更新数据字典分类
     */
    @Order(100)
    @Test
    void testAdminUpdateClassified() {
        AdminSysDataDictClassifiedInsertOrUpdateDTO dto = AdminSysDataDictClassifiedInsertOrUpdateDTO.builder()
                // 主键ID
                .id(classifiedId)
                // 分类编码
                .code("enabled-status")
                // 分类名称
                .name("启用状态")
                // 状态
                .status(EnabledStatusEnum.ENABLED)
                // 分类描述
                .description("这下有描述了")
                .build();
        sysDataDictService.adminUpdateClassified(dto);

        log.info("\n\n\n更新完成 >> dto={}", dto);
    }

    /**
     * 单元测试-分页列表数据字典分类
     */
    @Order(200)
    @Test
    void testAdminListClassified() {
        PageResult<SysDataDictClassifiedBO> pageResult = sysDataDictService.adminListClassified(
                new PageParam(1, 10),
                AdminSysDataDictClassifiedListDTO.builder()

                        .build()
        );

        log.info("\n\n\n分页列表成功 >> 结果={}", pageResult);
        Assertions.assertTrue(CollUtil.isNotEmpty(pageResult.getRecords()));
    }

    /**
     * 单元测试-后台新增数据字典项
     */
    @Order(300)
    @Test
    void testAdminInsert() {
        for (EnabledStatusEnum enumConstant : EnabledStatusEnum.class.getEnumConstants()) {
            Long entityId = sysDataDictService.adminInsertItem(
                    AdminSysDataDictItemInsertOrUpdateDTO.builder()
                            // 所属分类ID
                            .classifiedId(classifiedId)
                            // 字典项编码
                            .code(enumConstant.name().toLowerCase(Locale.ROOT))
                            // 字典项标签
                            .label(enumConstant.getLabel())
                            // 字典项值
                            .value(enumConstant.getValue().toString())
                            // 状态
                            .status(EnabledStatusEnum.DISABLED)
                            // 排序
                            .sort(enumConstant.ordinal())
                            // 描述
                            .description(enumConstant.name())
                            .build()
            );

            log.info("\n\n\n新增成功 >> 新主键ID={}", entityId);
        }
    }

    /**
     * 单元测试-分页列表数据字典项
     */
    @Order(400)
    @Test
    void testAdminListItem() {
        PageResult<SysDataDictItemBO> pageResult = sysDataDictService.adminListItem(
                new PageParam(1, 10),
                AdminSysDataDictItemListDTO.builder()
                        .classifiedId(classifiedId)
                        .build()
        );

        log.info("\n\n\n分页列表成功 >> 结果={}", pageResult);
        // 数据条数与枚举条数一致
        Assertions.assertEquals(compareAllItemSize, CollUtil.size(pageResult.getRecords()));
        Assertions.assertEquals(compareEnabledItemSize, sysDataDictService.listEnabledItemsByClassifiedCode("enabled-status").size());
        items = pageResult.getRecords();
    }

    /**
     * 单元测试-后台更新数据字典项
     */
    @Order(500)
    @Test
    void testAdminUpdateItem() {
        for (SysDataDictItemBO item : items) {
            AdminSysDataDictItemInsertOrUpdateDTO dto = AdminSysDataDictItemInsertOrUpdateDTO.builder()
                    // 主键ID
                    .id(item.getId())
                    // 所属分类ID
                    .classifiedId(classifiedId)
                    // 字典项编码
                    .code(item.getCode())
                    // 字典项标签
                    .label(item.getLabel())
                    // 字典项值
                    .value(item.getValue())
                    // 状态
                    .status(EnabledStatusEnum.ENABLED)
                    // 排序
                    .sort(item.getSort())
                    // 描述
                    .description(item.getDescription())
                    .build();
            sysDataDictService.adminUpdateItem(dto);

            log.info("\n\n\n更新完成 >> dto={}", dto);
        }

        // 再次查询列表，比较剩余数据字典项数量
        compareEnabledItemSize = EnabledStatusEnum.class.getEnumConstants().length;
        testAdminListItem();
    }

    /**
     * 单元测试-后台删除 classifiedId 下所有数据字典项
     */
    @Order(600)
    @Test
    void testAdminDeleteItem() {
        List<Long> ids = items.stream().map(SysDataDictItemBO::getId).toList();
        sysDataDictService.adminDeleteItem(ids, classifiedId);
        log.info("\n\n\n删除完成 >> ids={}", ids);

        // 再次查询列表，比较剩余数据字典项数量
        compareAllItemSize = 0;
        compareEnabledItemSize = 0;
        testAdminListItem();
    }

    /**
     * 单元测试-后台删除数据字典分类
     */
    @Order(700)
    @Test
    void testAdminDelete() {
        // 主键ID列表
        List<Long> ids = Collections.singletonList(classifiedId);
        sysDataDictService.adminDeleteClassified(ids);
        log.info("\n\n\n删除完成 >> ids={}", ids);
    }
}
