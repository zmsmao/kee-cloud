package com.kee.model.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kee.model.test.domain.Test;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description : Object
 * @author zms
 */
@Mapper
public interface TestMapper extends BaseMapper<Test> {
}
