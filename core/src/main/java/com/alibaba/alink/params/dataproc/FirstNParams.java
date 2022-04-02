package com.alibaba.alink.params.dataproc;

import org.apache.flink.ml.api.misc.param.ParamInfo;
import org.apache.flink.ml.api.misc.param.ParamInfoFactory;
import org.apache.flink.ml.api.misc.param.WithParams;

import com.alibaba.alink.common.annotation.DescCn;
import com.alibaba.alink.common.annotation.NameCn;
import com.alibaba.alink.params.validators.MinValidator;

public interface FirstNParams<T> extends
	WithParams <T> {

	@NameCn("采样个数")
	@DescCn("采样个数")
	ParamInfo <Integer> SIZE = ParamInfoFactory
		.createParamInfo("size", Integer.class)
		.setDescription("sampling size")
		.setValidator(new MinValidator <>(1))
		.setRequired()
		.build();

	default Integer getSize() {
		return getParams().get(SIZE);
	}

	default T setSize(Integer value) {
		return set(SIZE, value);
	}
}
