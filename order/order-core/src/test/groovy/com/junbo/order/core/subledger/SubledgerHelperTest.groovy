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
                [startTime: '2013-03-01', durationInMonth: 3, input: '2014-02-04', output: '2013-12-01'],
                [startTime: '2013-03-01', durationInMonth: 3, input: '2013-03-01', output: '2013-03-01'],
                [startTime: '2013-03-01', durationInMonth: 3, input: '2013-01-04', output: '2012-12-01'],
                [startTime: '2013-03-04', durationInMonth: 3, input: '2013-03-02', output: '2012-12-04']
        ]
        dataSet.each { Map<String, Object> data ->
            subledgerHelper.startTime = data['startTime']
            subledgerHelper.durationInMonth = data['durationInMonth'] as int
            assert subledgerHelper.getSubledgerStartTime(dateFormat.parse(data['input'] as String)) ==
                    dateFormat.parse(data['output'] as String)
        }
    }

    @Test
    void getNextSubledgerStartTime() {
        List<Map<String, Object>> dataSet = [
                [startTime: '2013-03-01', durationInMonth: 3, input: '2014-02-04', output: '2014-03-01']
        ]
        dataSet.each { Map<String, Object> data ->
            subledgerHelper.startTime = data['startTime']
            subledgerHelper.durationInMonth = data['durationInMonth'] as int
            assert subledgerHelper.getNextSubledgerStartTime(dateFormat.parse(data['input'] as String)) ==
                    dateFormat.parse(data['output'] as String)
        }
    }

}
