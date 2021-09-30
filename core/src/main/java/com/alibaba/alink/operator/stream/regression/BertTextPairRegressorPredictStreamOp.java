package com.alibaba.alink.operator.stream.regression;

import org.apache.flink.ml.api.misc.param.Params;

import com.alibaba.alink.operator.batch.BatchOperator;

public class BertTextPairRegressorPredictStreamOp extends
	TFTableModelRegressorPredictStreamOp <BertTextPairRegressorPredictStreamOp> {

	public BertTextPairRegressorPredictStreamOp(BatchOperator <?> model) {
		this(model, new Params());
	}

	public BertTextPairRegressorPredictStreamOp(BatchOperator <?> model, Params params) {
		super(model, params);
	}
}
