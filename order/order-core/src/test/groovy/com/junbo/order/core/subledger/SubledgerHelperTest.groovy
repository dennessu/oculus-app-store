package com.junbo.order.core.subledger

import com.junbo.order.core.impl.subledger.SubledgerHelper
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import java.text.SimpleDateFormat

/**
 * Created by fzhang on 4/25/2014.
 */
class SubledgerHelperTest extends SubledgerHelper {

    private SubledgerHelper subledgerHelper = new SubledgerHelper()

    private def dateFormat = new SimpleDateFormat('yyyy-MM-dd', Locale.US)

    @BeforeMethod
    void setUp() {

    }

    @Test
    void testGetSubledgerStartTime() {
        List<Map<String, Object>> dataSet = [
                [input: '2014-02-04', output: '2014-02-04']
        ]
        dataSet.each { Map<String, Object> data ->
            assert subledgerHelper.getSubledgerStartTime(dateFormat.parse(data['input'] as String)) ==
                    dateFormat.parse(data['output'] as String)
        }
    }

    @Test
    void getNextSubledgerStartTime() {
        List<Map<String, Object>> dataSet = [
                [input: '2014-02-02', output: '2014-02-03']
        ]
        dataSet.each { Map<String, Object> data ->
            assert subledgerHelper.getNextSubledgerStartTime(dateFormat.parse(data['input'] as String)) ==
                    dateFormat.parse(data['output'] as String)
        }
    }

}
