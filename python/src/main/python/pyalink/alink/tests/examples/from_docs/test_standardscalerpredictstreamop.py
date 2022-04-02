import unittest
from pyalink.alink import *
import numpy as np
import pandas as pd
class TestStandardScalerPredictStreamOp(unittest.TestCase):
    def test_standardscalerpredictstreamop(self):

        df = pd.DataFrame([
                    ["a", 10.0, 100],
                    ["b", -2.5, 9],
                    ["c", 100.2, 1],
                    ["d", -99.9, 100],
                    ["a", 1.4, 1],
                    ["b", -2.2, 9],
                    ["c", 100.9, 1]
        ])
                     
        colnames = ["col1", "col2", "col3"]
        selectedColNames = ["col2", "col3"]
        
        inOp = BatchOperator.fromDataframe(df, schemaStr='col1 string, col2 double, col3 long')
           
        # train
        trainOp = StandardScalerTrainBatchOp()\
                   .setSelectedCols(selectedColNames)
        
        trainOp.linkFrom(inOp)
        
        # batch predict
        predictOp = StandardScalerPredictBatchOp()
        predictOp.linkFrom(trainOp, inOp).print()
        
        # stream predict
        sinOp = StreamOperator.fromDataframe(df, schemaStr='col1 string, col2 double, col3 long')
        
        predictStreamOp = StandardScalerPredictStreamOp(trainOp)
        predictStreamOp.linkFrom(sinOp).print()
        
        StreamOperator.execute()
        pass