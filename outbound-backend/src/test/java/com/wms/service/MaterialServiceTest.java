package com.wms.service;

import com.wms.dto.MaterialDTO;
import com.wms.entity.Material;
import com.wms.exception.BusinessException;
import com.wms.mapper.MaterialMapper;
import com.wms.service.impl.MaterialServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 物料服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("物料服务测试")
public class MaterialServiceTest {

    @Mock
    private MaterialMapper materialMapper;

    @InjectMocks
    private MaterialServiceImpl materialService;

    private Material testMaterial;
    private MaterialDTO testMaterialDTO;

    @BeforeEach
    void setUp() {
        testMaterial = new Material();
        testMaterial.setId(1L);
        testMaterial.setMaterialCode("MAT001");
        testMaterial.setMaterialName("测试物料");
        testMaterial.setUnit("个");
        testMaterial.setStatus(1);

        testMaterialDTO = new MaterialDTO();
        testMaterialDTO.setMaterialCode("MAT001");
        testMaterialDTO.setMaterialName("测试物料");
        testMaterialDTO.setUnit("个");
    }

    @Test
    @DisplayName("根据ID查询物料-成功")
    void testGetById_Success() {
        // 模拟
        when(materialMapper.selectById(1L)).thenReturn(testMaterial);

        // 执行
        Material result = materialService.getById(1L);

        // 验证
        assertNotNull(result);
        assertEquals("MAT001", result.getMaterialCode());
        assertEquals("测试物料", result.getMaterialName());
        verify(materialMapper, times(1)).selectById(1L);
    }

    @Test
    @DisplayName("根据ID查询物料-不存在")
    void testGetById_NotFound() {
        // 模拟
        when(materialMapper.selectById(999L)).thenReturn(null);

        // 执行 & 验证
        assertThrows(BusinessException.class, () -> {
            materialService.getById(999L);
        });
    }

    @Test
    @DisplayName("新增物料-成功")
    void testAddMaterial_Success() {
        // 模拟
        when(materialMapper.insert(any(Material.class))).thenReturn(1);

        // 执行
        boolean result = materialService.save(testMaterial);

        // 验证
        assertTrue(result);
        verify(materialMapper, times(1)).insert(any(Material.class));
    }

    @Test
    @DisplayName("新增物料-编码重复")
    void testAddMaterial_DuplicateCode() {
        // 模拟编码已存在
        when(materialMapper.selectCount(any())).thenReturn(1);

        // 执行 & 验证
        assertThrows(BusinessException.class, () -> {
            materialService.addMaterial(testMaterialDTO);
        });
    }

    @Test
    @DisplayName("更新物料-成功")
    void testUpdateMaterial_Success() {
        // 模拟
        when(materialMapper.selectById(1L)).thenReturn(testMaterial);
        when(materialMapper.updateById(any(Material.class))).thenReturn(1);

        // 执行
        boolean result = materialService.updateById(testMaterial);

        // 验证
        assertTrue(result);
        verify(materialMapper, times(1)).updateById(any(Material.class));
    }

    @Test
    @DisplayName("删除物料-成功")
    void testDeleteMaterial_Success() {
        // 模拟
        when(materialMapper.selectById(1L)).thenReturn(testMaterial);
        when(materialMapper.updateById(any(Material.class))).thenReturn(1);

        // 执行
        boolean result = materialService.removeById(1L);

        // 验证
        assertTrue(result);
        verify(materialMapper, times(1)).updateById(any(Material.class));
    }

    @Test
    @DisplayName("查询所有物料")
    void testListAllMaterials() {
        // 模拟
        List<Material> materials = Arrays.asList(testMaterial);
        when(materialMapper.selectList(any())).thenReturn(materials);

        // 执行
        List<Material> result = materialService.list();

        // 验证
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("MAT001", result.get(0).getMaterialCode());
    }
}
