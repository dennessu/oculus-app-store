#!/bin/env python
import sys
import re
import fileinput

class LogProcessor:

    class SampleRequest:
        def __init__(self):
            self.description = ""
            self.method = ""
            self.endpoint = ""
            self.requestUrl = ""
            self.requestHeaders = ""
            self.requestBody = ""
            self.responseHeaders = ""
            self.responseStatus = ""
            self.responseBody = ""

    def __init__(self, baseUrl):
        self.__baseUrl = baseUrl
        self.__currentTestCase = ""
        self.__currentBigLine = ""
        self.__requests = []
        self.__currentRequest = None

    def process(self, line):
        gradleCaseStartRegex = r'^\s*Gradle test > (\w+\.)+?(?P<className>\w+)\.(?P<methodName>\w+\s+)'
        m = re.match(gradleCaseStartRegex, line)
        if m is not None:
            self.__currentTestCase = m.group("className") + "." + m.group("methodName")

        lineStartRegex = r'^\s*\d{1,2}/\d{1,2}/\d{4} \d{1,2}:\d{1,2}:\d{1,2}\.\d{1,3}: \[[^\]]*\]\s*\w+\s*'
        lineMatch = re.match(lineStartRegex, line)
        if lineMatch is not None:
            strippedLine = line[len(lineMatch.group()):]
            self.__processBigLine()
            self.__currentBigLine = strippedLine
        else:
            self.__currentBigLine += line 

    def __processBigLine(self):
        sampleCallStart = r'^\s*com\.junbo\.test\.\S+ - \*\*\*\* EXECUTING (?P<method>\w+)'
        sampleCallRequestBody = r'^\s*com\.junbo\.test\.\S+ - \*\*\*\* Request Body:\s*(?P<requestBody>.*)'
        sampleCallRequestResponse = r'^\s*com\.ning\.http\.client\.providers\.netty\.NettyAsyncHttpProvider -\s*\n' \
            r'^\s*Request DefaultHttpRequest\([^)]*\)\s*\n' \
            r'^\s*(?P<method>\w+)\s+(?P<url>\S+)\s+HTTP/1.1\s*\n' \
            r'(?P<headers>(^\s*\S[^\n]+$\n)*)' \
            r'^\s*$\n' \
            r'^\s*Response DefaultHttpResponse\([^)]*\)\s*\n' \
            r'^\s*HTTP/1.1\s*(?P<responseCode>\d+\s+\w+)\s*\n' \
            r'(?P<responseHeaders>(^\s*\S[^\n]+$\n)*)' \
            r'^\s*$\n'
        sampleCallResponseBody = r'^\s*com\.junbo\.test\.\S+ - \*\*\*\* Response body:\s*(?P<responseBody>.*)'
        actions = [
                (sampleCallStart, self.__processSampleCallStart),
                (sampleCallRequestBody, self.__processSampleCallRequestBody),
                (sampleCallRequestResponse, self.__processSampleCallRequestResponse),
                (sampleCallResponseBody, self.__processSampleCallResponseBody)]
        for regex, action in actions:
            match = re.match(regex, self.__currentBigLine, re.DOTALL|re.MULTILINE)   # also match newline
            if match is not None:
                action(match)

    def __processSampleCallStart(self, match):
        self.__startCurrentSampleRequest()
        self.__currentRequest.method = match.group('method')

    def __processSampleCallRequestBody(self, match):
        self.__currentRequest.requestBody = self.__processBody(match.group('requestBody'))

    def __processSampleCallRequestResponse(self, match):
        self.__currentRequest.method = match.group('method')
        self.__currentRequest.endpoint = self.__processEndpoint(match.group('url'))
        self.__currentRequest.requestUrl = self.__processUrl(match.group('url'))
        self.__currentRequest.requestHeaders = self.__processHeaders(match.group('headers'))
        self.__currentRequest.responseHeaders = self.__processHeaders(match.group('responseHeaders'))
        self.__currentRequest.responseCode = match.group('responseCode')
        if self.__currentRequest.responseCode == "302 Found":
            self.__finishLastSampleRequest()
            self.__startCurrentSampleRequest()

    def __processSampleCallResponseBody(self, match):
        self.__currentRequest.responseBody = self.__processBody(match.group('responseBody'))
        self.__finishLastSampleRequest()

    def __startCurrentSampleRequest(self):
        if self.__currentRequest is None:
            self.__currentRequest = self.SampleRequest()
            self.__currentRequest.description = self.__currentTestCase
        else:
            raise Exception("Start new sample request in invalid status.")

    def __finishLastSampleRequest(self):
        if self.__currentRequest is not None:
            self.__requests.append(self.__currentRequest)
            self.__currentRequest = None

    def __processUrl(self, url):
        urlRegex = r'^http[s]?://[^/]+(?P<path>/.*)$'
        m = re.match(urlRegex, url)
        if m is not None:
            url = m.group('path')
        return "$baseUrl" + url.replace(self.__baseUrl, "")

    def __processEndpoint(self, url):
        # Process the endpoint using the following rules:
        # 1. Strip all query parameters and segmentations
        # 2. Replace all IDs (([0-9a-fA-F]{12})|([0-9]{4}\-[0-9]{4}\-[0-9]{4}))
        # 3. Strip the trail /
        url = self.__processUrl(url)
        url = url.replace("$baseUrl", "")
        url = re.match(r'[^#]*', url).group(0)
        url = re.match(r'[^?]*', url).group(0)
        url = re.sub(r'(([0-9a-fA-F]{12})|([0-9]{4}\-[0-9]{4}\-[0-9]{4}))', r'{id}', url)
        url = url.rstrip('/')
        return url

    def __processHeaders(self, headers):
        headerRegex = r'^\s*(\S[^:]*): (.*)$'
        headerResponse = ""
        for headerLine in headers.split("\n"):
            m = re.match(headerRegex, headerLine)
            if m is None:
                if not re.match('^\s*$', headerLine):
                    raise Exception('Failed to parse \'' + headerLine + '\'')
                continue
            ignoreList = set([
                'Host', 'Connection', 'User-Agent', 'Content-Length', 'Date', 'Transfer-Encoding'])

            if m.group(1) in ignoreList:
                continue

            headerName = m.group(1)
            headerValue = m.group(2)
            if headerName == 'Accept' and headerValue == '*/*':
                headerValue = 'application/json'
            if headerName == 'Location':
                headerValue = self.__processUrl(headerValue)
            headerResponse += headerName + ": " + headerValue + "\n"
        return headerResponse.rstrip()

    def __processBody(self, body):
        if body.strip() == "null":
            return ""
        return body

    def dump(self):
        self.__finishLastSampleRequest()

        # group results
        from collections import defaultdict
        groups = defaultdict(list)
        for request in self.__requests:
            groups[request.endpoint + "." + request.method].append(request)
        indent = 0

        def printResponse(fmt, params = ()):
            print (' ' * indent * 4) + fmt % params

        def printResponseOpt(fmt, param):
            if param.strip() != "":
                printResponse(fmt, (param))

        printResponse("<samples>")
        indent += 1
        sortedKeys = sorted(groups.iterkeys())
        for k in sortedKeys:
            v = groups[k]
            if len(v) == 0:
                continue
            printResponse('<apiSamples method="%s" endpoint="%s">', (v[0].method, v[0].endpoint))
            indent += 1
            for sample in v:
                printResponse('<sample description="%s">', (sample.description))
                indent += 1
                printResponse('<requestUrl>%s</requestUrl>', (sample.requestUrl))
                printResponseOpt('<requestHeaders><![CDATA[%s]]></requestHeaders>', sample.requestHeaders)
                printResponseOpt('<requestBody><![CDATA[%s]]></requestBody>', sample.requestBody)
                printResponse('<responseCode>%s</responseCode>', (sample.responseCode))
                printResponseOpt('<responseHeaders><![CDATA[%s]]></responseHeaders>', sample.responseHeaders)
                printResponseOpt('<responseBody><![CDATA[%s]]></responseBody>', sample.responseBody)
                indent -= 1
                printResponse('</sample>')
            indent -= 1
            printResponse('</apiSamples>')
        indent -= 1
        printResponse('</samples>')

p = LogProcessor("/rest")
for line in fileinput.input():
    p.process(line)
p.dump()

