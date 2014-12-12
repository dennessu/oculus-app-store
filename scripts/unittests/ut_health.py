#!/usr/bin/python
import silkcloudut as ut
from silkcloudut import *

class HealthTests(ut.TestBase):
    def testHealth(self):
        ut.cookies.clear()
        resp = curlJson('POST', ut.test_health_uri, '/health/check')
        assert resp["result"] == 'PASS' or resp["result"] == 'WARN'

if __name__ == '__main__':
    silkcloud_utmain()
