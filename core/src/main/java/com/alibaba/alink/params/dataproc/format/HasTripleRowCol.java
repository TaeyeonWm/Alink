package com.alibaba.alink.params.dataproc.format;

import org.apache.flink.ml.api.misc.param.ParamInfo;
import org.apache.flink.ml.api.misc.param.ParamInfoFactory;
import org.apache.flink.ml.api.misc.param.WithParams;

import com.alibaba.alink.common.annotation.DescCn;
import com.alibaba.alink.common.annotation.NameCn;

/**
 * An interface for classes with a parameter specifying the name of the table column.
 */
public interface HasTripleRowCol<T> extends WithParams <T> {

	@NameCn("三元组结构中行信息的列名")
	@DescCn("三元组结构中行信息的列名")
	ParamInfo <String> TRIPLE_ROW_COL = ParamInfoFactory
		.createParamInfo("tripleRowCol", String.class)
		.setDescription("Name of the triple row column")
		.setHasDefaultValue(null)
		.build();

	default String getTripleRowCol() {
		return get(TRIPLE_ROW_COL);
	}

	default T setTripleRowCol(String colName) {
		return set(TRIPLE_ROW_COL, colName);
	}
}
