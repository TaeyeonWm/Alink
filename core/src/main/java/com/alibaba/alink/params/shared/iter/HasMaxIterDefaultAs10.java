package com.alibaba.alink.params.shared.iter;

import org.apache.flink.ml.api.misc.param.ParamInfo;
import org.apache.flink.ml.api.misc.param.ParamInfoFactory;
import org.apache.flink.ml.api.misc.param.WithParams;

import com.alibaba.alink.common.annotation.DescCn;
import com.alibaba.alink.common.annotation.NameCn;

public interface HasMaxIterDefaultAs10<T> extends WithParams <T> {

	@NameCn("最大迭代步数")
	@DescCn("最大迭代步数，默认为 10。")
	ParamInfo <Integer> MAX_ITER = ParamInfoFactory
		.createParamInfo("maxIter", Integer.class)
		.setDescription("Maximum iterations, The default value is 10")
		.setHasDefaultValue(10)
		.setAlias(new String[] {"maxIteration", "numIter"})
		.build();

	default Integer getMaxIter() {
		return get(MAX_ITER);
	}

	default T setMaxIter(Integer value) {
		return set(MAX_ITER, value);
	}
}
