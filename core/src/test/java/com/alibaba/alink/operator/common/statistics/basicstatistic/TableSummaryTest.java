package com.alibaba.alink.operator.common.statistics.basicstatistic;

import org.apache.flink.types.Row;

import com.alibaba.alink.testutil.AlinkTestBase;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for TableSummary.
 */

public class TableSummaryTest extends AlinkTestBase {

	@Test
	public void test() {
		TableSummary srt = testVisit();
		System.out.println(srt.toString());

		Assert.assertEquals(srt.colNames.length, 5);
		Assert.assertEquals(srt.count(), 4);
		Assert.assertEquals(srt.numMissingValue("f_double"), 1, 10e-4);
		Assert.assertEquals(srt.numValidValue("f_double"), 3, 10e-4);
		Assert.assertEquals(srt.max("f_double"), 2.0, 10e-4);
		Assert.assertEquals(srt.min("f_int"), 0.0, 10e-4);
		Assert.assertEquals(srt.mean("f_double"), 0.3333333333333333, 10e-4);
		Assert.assertEquals(srt.variance("f_double"), 8.333333333333334, 10e-4);
		Assert.assertEquals(srt.standardDeviation("f_double"), 2.886751345948129, 10e-4);
		Assert.assertEquals(srt.normL1("f_double"), 7.0, 10e-4);
		Assert.assertEquals(srt.normL2("f_double"), 4.123105625617661, 10e-4);

	}

	private TableSummary testVisit() {
		Row[] data =
			new Row[] {
				Row.of("a", 1L, 1, 2.0, true),
				Row.of(null, 2L, 2, -3.0, true),
				Row.of("c", null, null, 2.0, false),
				Row.of("a", 0L, 0, null, null),
			};

		int[] numberIdxs = new int[] {1, 2, 3};
		String[] selectedColNames = new String[] {"f_string", "f_long", "f_int", "f_double", "f_boolean"};
		TableSummarizer summarizer = new TableSummarizer(selectedColNames, numberIdxs, false);
		for (Row aData : data) {
			summarizer.visit(aData);
		}

		return summarizer.toSummary();
	}
}