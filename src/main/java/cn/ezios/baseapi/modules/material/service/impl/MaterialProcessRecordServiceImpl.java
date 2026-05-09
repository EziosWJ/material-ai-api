package cn.ezios.baseapi.modules.material.service.impl;

import cn.ezios.baseapi.common.enums.ResponseCode;
import cn.ezios.baseapi.common.exception.BusinessException;
import cn.ezios.baseapi.common.model.PageResult;
import cn.ezios.baseapi.modules.material.dto.MaterialProcessRecordPageQuery;
import cn.ezios.baseapi.modules.material.entity.BizMaterialProcessRecord;
import cn.ezios.baseapi.modules.material.mapper.BizMaterialProcessRecordMapper;
import cn.ezios.baseapi.modules.material.service.MaterialProcessRecordService;
import cn.ezios.baseapi.modules.material.vo.MaterialProcessRecordVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class MaterialProcessRecordServiceImpl implements MaterialProcessRecordService {

    private final BizMaterialProcessRecordMapper processRecordMapper;

    public MaterialProcessRecordServiceImpl(BizMaterialProcessRecordMapper processRecordMapper) {
        this.processRecordMapper = processRecordMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MaterialProcessRecordVO create(BizMaterialProcessRecord record) {
        processRecordMapper.insert(record);
        return toVO(record);
    }

    @Override
    public PageResult<MaterialProcessRecordVO> page(MaterialProcessRecordPageQuery query) {
        Page<BizMaterialProcessRecord> page = processRecordMapper.selectPage(Page.of(query.getPage(), query.getPageSize()),
                new LambdaQueryWrapper<BizMaterialProcessRecord>()
                        .eq(query.getMaterialId() != null, BizMaterialProcessRecord::getMaterialId, query.getMaterialId())
                        .eq(query.getUserId() != null, BizMaterialProcessRecord::getUserId, query.getUserId())
                        .eq(query.getFileId() != null, BizMaterialProcessRecord::getFileId, query.getFileId())
                        .eq(StringUtils.hasText(query.getProcessType()), BizMaterialProcessRecord::getProcessType, query.getProcessType())
                        .eq(StringUtils.hasText(query.getStatus()), BizMaterialProcessRecord::getStatus, query.getStatus())
                        .orderByDesc(BizMaterialProcessRecord::getCreateTime)
                        .orderByDesc(BizMaterialProcessRecord::getId));
        return new PageResult<>(page.getRecords().stream().map(this::toVO).toList(),
                page.getTotal(), query.getPage(), query.getPageSize());
    }

    @Override
    public MaterialProcessRecordVO getDetail(Long id) {
        return toVO(requireProcessRecord(id));
    }

    private BizMaterialProcessRecord requireProcessRecord(Long id) {
        BizMaterialProcessRecord record = processRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(ResponseCode.NOT_FOUND);
        }
        return record;
    }

    private MaterialProcessRecordVO toVO(BizMaterialProcessRecord record) {
        MaterialProcessRecordVO vo = new MaterialProcessRecordVO();
        BeanUtils.copyProperties(record, vo);
        return vo;
    }
}
