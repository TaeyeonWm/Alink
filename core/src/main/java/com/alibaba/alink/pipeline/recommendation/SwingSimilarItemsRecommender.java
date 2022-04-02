package com.alibaba.alink.pipeline.recommendation;

import org.apache.flink.ml.api.misc.param.Params;

import com.alibaba.alink.common.annotation.NameCn;
import com.alibaba.alink.operator.common.recommendation.RecommType;
import com.alibaba.alink.operator.common.recommendation.SwingRecommKernel;
import com.alibaba.alink.params.recommendation.BaseSimilarItemsRecommParams;

@NameCn("Swing推荐")
public class SwingSimilarItemsRecommender
	extends BaseRecommender <SwingSimilarItemsRecommender>
	implements BaseSimilarItemsRecommParams <SwingSimilarItemsRecommender> {

	public SwingSimilarItemsRecommender() {
		this(null);
	}

	public SwingSimilarItemsRecommender(Params params) {
		super(SwingRecommKernel::new, RecommType.SIMILAR_ITEMS, params);
	}
}
