#!/usr/bin/python
import sys
import re
import fileinput
import json

class LogProcessor:

    class SampleRequest:
        def __init__(self):
            self.description = ""
            self.method = ""
            self.endpoint = ""
            self.requestUrl = ""
            self.requestHeaders = ""
            self.requestBody = ""
            self.responseCode = ""
            self.responseHeaders = ""
            self.responseBody = ""

    # regex
    __sampleCallStartType1 = r'^\s*com\.junbo\.test\.\S+ - \*\*\*\* EXECUTING (?P<method>\w+)'
    __sampleCallStartType2 = r'^\s*org\.apache\.http\.headers - http-outgoing-\d+ >> '
    __log4jLineStartFilter = r'^\s*\d{1,2}/\d{1,2}/\d{4} \d{1,2}:\d{1,2}:\d{1,2}\.\d{1,3}: \[[^\]]*\]\s*\w+\s*'
    __caseDesc = r'\[Include In Sample\](\[(?P<start>\d+)(\.\.(?P<end>\d+))?\])? Description: (?P<desc>.*)'

    # Case type 1 regex
    __caseType1CaseName = r'^\s*Gradle test > (\w+\.)+?(?P<className>\w+)\.(?P<methodName>\w+)\s+'
    __caseType1RequestBody = r'^\s*com\.junbo\.test\.\S+ - \*\*\*\* Request Body:\s*(?P<requestBody>.*)' 
    __caseType1RequestResponse = r'^\s*com\.ning\.http\.client\.providers\.netty\.NettyAsyncHttpProvider -\s*\n' \
            r'^\s*Request DefaultHttpRequest\([^)]*\)\s*\n' \
            r'^\s*(?P<method>\w+)\s+(?P<url>\S+)\s+HTTP/1.1\s*\n' \
            r'(?P<headers>(^\s*\S[^\n]+$\n)*)' \
            r'^\s*$\n' \
            r'^\s*Response DefaultHttpResponse\([^)]*\)\s*\n' \
            r'^\s*HTTP/1.1\s*(?P<responseCode>\d+\s+\w+)\s*\n' \
            r'(?P<responseHeaders>(^\s*\S[^\n]+$\n)*)' \
            r'^\s*$\n'
    __caseType1ResponseBody = r'^\s*com\.junbo\.test\.\S+ - \*\*\*\* Response body:\s*(?P<responseBody>.*)'

    # Case type 2 regex
    __caseType2CaseName = r'<testcase name="(?P<methodName>[^"]+)" classname="(\w+.)+(?P<className>\w+)+"'
    __caseType2BlockLineOut = r'^\s*org\.apache\.http\.wire - http-outgoing-\d+ >> (?P<line>.*)'
    __caseType2BlockLineIn  = r'^\s*org\.apache\.http\.wire - http-outgoing-\d+ << (?P<line>.*)'
    __caseType2BlockEnd = r'^\s*org\.apache\.http\.headers - http-outgoing-\d+ << '

    __caseType2RequestHeaders = r'^\s*(?P<method>\w+)\s+(?P<url>\S+)\s+HTTP/1.1\s*\n' \
        r'(?P<headers>(^\s*\S[^\n]+$\n)*)' \
        r'^\s*$\n'
    __caseType2ResponseHeaders = r'^\s*HTTP/1.1\s*(?P<responseCode>\d+\s+\w+)\s*\n' \
        r'(?P<responseHeaders>(^\s*\S[^\n]+$\n)*)' \
        r'^\s*$\n' \

    def __init__(self, baseUrl):
        self.__baseUrl = baseUrl
        self.__currentTestCase = ""
        self.__currentBigLine = ""
        self.__requests = []
        self.__currentRequest = None
        self.__globalActions = [
                (self.__caseType1CaseName, self.__setCurrentCase),
                (self.__caseType2CaseName, self.__setCurrentCase),
                (self.__caseDesc, self.__setCaseDesc)]
        self.__enterNotInSampleState(None)

    def __enterNotInSampleState(self, match):
        self.__finishLastSampleRequest()
        self.__actions = [
                (self.__sampleCallStartType1, self.__enterInCaseType1State),
                (self.__sampleCallStartType2, self.__enterInCaseType2State)]

    def __enterInCaseType1State(self, match):
        self.__actions = [
                (self.__sampleCallStartType1, self.__enterInCaseType1State),
                (self.__sampleCallStartType2, self.__enterInCaseType2State),

                (self.__caseType1RequestBody, self.__onCaseType1RequestBody),
                (self.__caseType1RequestResponse, self.__onCaseType1RequestResponse),
                (self.__caseType1ResponseBody, self.__onCaseType1ResponseBody)]

        self.__startCurrentSampleRequest()
        self.__currentRequest.method = match.group('method')

    def __enterInCaseType2State(self, match):
        self.__actions = [
                (self.__sampleCallStartType1, self.__enterInCaseType1State),
                (self.__sampleCallStartType2, self.__enterInCaseType2State),

                (self.__caseType2BlockLineOut,   self.__onCaseType2BlockLineOutStart)]

        self.__startCurrentSampleRequest()

    def __setCurrentCase(self, match):
        self.__currentTestCase = match.group("className") + "." + match.group("methodName")

    def __setCaseDesc(self, match):
        self.__currentTestCase = match.group("desc")
        # TODO: handle [begin..end]

    # state neutral processing of lines from log files
    def process(self, line):
        for regex, action in self.__globalActions:
            m = re.match(regex, line)
            if m is not None:
                action(m)

        lineMatch = re.match(self.__log4jLineStartFilter, line)
        if lineMatch is not None:
            strippedLine = line[len(lineMatch.group()):]
            self.__processBigLine()
            self.__currentBigLine = strippedLine
        else:
            self.__currentBigLine += line 

    def __processBigLine(self):
        for regex, action in self.__actions:
            match = re.match(regex, self.__currentBigLine, re.DOTALL | re.MULTILINE)   # also match newline
            if match is not None:
                action(match)

    # case type 1
    def __onCaseType1RequestBody(self, match):
        self.__currentRequest.requestBody = self.__processBody(match.group('requestBody'))

    def __onCaseType1RequestResponse(self, match):
        self.__currentRequest.method = match.group('method')
        self.__currentRequest.endpoint = self.__processEndpoint(match.group('url'))
        self.__currentRequest.requestUrl = self.__processUrl(match.group('url'))
        self.__currentRequest.requestHeaders = self.__processHeaders(match.group('headers'))
        self.__currentRequest.responseHeaders = self.__processHeaders(match.group('responseHeaders'))
        self.__currentRequest.responseCode = match.group('responseCode')

        # handle redirects
        if re.match(r'^(301)|(302)|(303)|(307)|(308)', self.__currentRequest.responseCode):
            self.__finishLastSampleRequest()
            self.__startCurrentSampleRequest()

    def __onCaseType1ResponseBody(self, match):
        self.__currentRequest.responseBody = self.__processBody(match.group('responseBody'))
        self.__finishLastSampleRequest()
        self.__enterNotInSampleState(None)

    # case type 2
    def __onCaseType2BlockLineOutStart(self, match):
        self.__currentCase2Request = self.__processCaseType2Line(match.group('line'))
        self.__actions = [
                (self.__sampleCallStartType1, self.__enterInCaseType1State),
                (self.__sampleCallStartType2, self.__enterInCaseType2State),

                (self.__caseType2BlockLineOut, self.__onCaseType2BlockLineOutAppend),
                (self.__caseType2BlockLineIn,  self.__onCaseType2BlockLineInStart)]

    def __onCaseType2BlockLineOutAppend(self, match):
        self.__currentCase2Request += self.__processCaseType2Line(match.group('line'))

    def __onCaseType2BlockLineInStart(self, match):
        self.__currentCase2Response = self.__processCaseType2Line(match.group('line'))
        self.__actions = [
                (self.__sampleCallStartType1, self.__enterInCaseType1State),
                (self.__sampleCallStartType2, self.__enterInCaseType2State),

                (self.__caseType2BlockLineIn, self.__onCaseType2BlockLineInAppend),
                (self.__caseType2BlockEnd,    self.__onCaseType2BlockEnd)]

    def __onCaseType2BlockLineInAppend(self, match):
        self.__currentCase2Response += self.__processCaseType2Line(match.group('line'))

    def __onCaseType2BlockEnd(self, match):
        # ignore the input match, we don't need it
        if self.__currentCase2Request is None:
            raise Exception("Request not found in case type 2.")
        if self.__currentCase2Response is None:
            raise Exception("Response not found in case type 2.")

        match = re.match(self.__caseType2RequestHeaders, self.__currentCase2Request, re.DOTALL | re.MULTILINE)
        if match is not None:
            self.__currentRequest.method = match.group('method')
            self.__currentRequest.endpoint = self.__processEndpoint(match.group('url'))
            self.__currentRequest.requestUrl = self.__processUrl(match.group('url'))
            self.__currentRequest.requestHeaders = self.__processHeaders(match.group('headers'))
            self.__currentRequest.requestBody = self.__currentCase2Request[match.end():]
        else:
            raise Exception("Request not matching the regex: " + self.__currentCase2Request)

        match = re.match(self.__caseType2ResponseHeaders, self.__currentCase2Response, re.DOTALL | re.MULTILINE)
        if match is not None:
            self.__currentRequest.responseHeaders = self.__processHeaders(match.group('responseHeaders'))
            self.__currentRequest.responseCode = match.group('responseCode')
            self.__currentRequest.responseBody = self.__currentCase2Response[match.end():]
        else:
            raise Exception("Response not matching the regex: " + self.__currentCase2Response)

        self.__currentCase2Request = None
        self.__currentCase2Response = None
        self.__finishLastSampleRequest();
        self.__enterNotInSampleState(None)

    def __processCaseType2Line(self, line):
        lineRegex = r"\"(?P<line>.*)\""
        m = re.match(lineRegex, line)
        if m is not None:
            line = m.group('line')
            if line.endswith(r"[\r][\n]"):
                line = line.replace(r"[\r][\n]", "\r\n")
            elif line.endswith(r"[\n]"):
                line = line.replace(r"[\n]", "\n")
            return line
        else:
            raise Exception("Case Type 2 line not matching the regex: " + line)

    # helper functions to operate sample requests
    def __startCurrentSampleRequest(self):
        self.__finishLastSampleRequest()
        if self.__currentRequest is None:
            self.__currentRequest = self.SampleRequest()
            self.__currentRequest.description = self.__currentTestCase
        else:
            raise Exception("Start new sample request in invalid status.")

    def __finishLastSampleRequest(self):
        if self.__currentRequest is not None:
            if self.__currentRequest.responseCode != "":
                self.__requests.append(self.__currentRequest)
            self.__currentRequest = None

    # Processing the information we captured
    def __processUrl(self, url):
        urlRegex = r'^http[s]?://[^/]+(?P<path>(/|\?).*)$'
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
                'Host', 'Connection', 'User-Agent', 'Content-Length', 'Date', 'Transfer-Encoding', 'Accept-Encoding'])

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
        try:
            jsonBody = json.loads(body)
            return json.dumps(jsonBody, indent = 2, separators = (',', ': '))
        except ValueError:
            return body

    # print the result as xml
    def dumpByOrder(self):
        self.__finishLastSampleRequest()

        indent = 0

        from xml.sax.saxutils import escape
        def printResponse(fmt, *params):
            print (' ' * indent * 4) + fmt % params

        def printResponseEscape(fmt, *param):
            param = map(lambda x: escape(x), param)
            printResponse(fmt, *param)

        def printResponseCdata(fmt, param):
            if param.strip() != "":
                printResponse(fmt, "<![CDATA[" + param + "]]>")

        printResponse("<samples>")
        indent += 1
        for sample in self.__requests:
            printResponseEscape('<sample description="%s">', sample.description)
            indent += 1
            printResponseEscape('<requestUrl>%s</requestUrl>', sample.requestUrl)
            printResponseCdata('<requestHeaders>%s</requestHeaders>', sample.requestHeaders)
            printResponseCdata('<requestBody>%s</requestBody>', sample.requestBody)
            printResponseEscape('<responseCode>%s</responseCode>', sample.responseCode)
            printResponseCdata('<responseHeaders>%s</responseHeaders>', sample.responseHeaders)
            printResponseCdata('<responseBody>%s</responseBody>', sample.responseBody)
            indent -= 1
            printResponse('</sample>')
        indent -= 1
        printResponse('</samples>')


    # print the result as xml
    def dump(self):
        self.__finishLastSampleRequest()

        # group results
        from collections import defaultdict
        groups = defaultdict(list)
        for request in self.__requests:
            groups[request.endpoint + "." + request.method].append(request)
        indent = 0

        from xml.sax.saxutils import escape
        def printResponse(fmt, *params):
            print (' ' * indent * 4) + fmt % params

        def printResponseEscape(fmt, *param):
            param = map(lambda x: escape(x), param)
            printResponse(fmt, *param)

        def printResponseCdata(fmt, param):
            if param.strip() != "":
                printResponse(fmt, "<![CDATA[" + param + "]]>")

        printResponse("<samples>")
        indent += 1
        sortedKeys = sorted(groups.iterkeys())
        for k in sortedKeys:
            v = groups[k]
            if len(v) == 0:
                continue
            printResponseEscape('<apiSamples method="%s" endpoint="%s">', v[0].method, v[0].endpoint)
            indent += 1
            for sample in v:
                printResponseEscape('<sample description="%s">', sample.description)
                indent += 1
                printResponseEscape('<requestUrl>%s</requestUrl>', sample.requestUrl)
                printResponseCdata('<requestHeaders>%s</requestHeaders>', sample.requestHeaders)
                printResponseCdata('<requestBody>%s</requestBody>', sample.requestBody)
                printResponseEscape('<responseCode>%s</responseCode>', sample.responseCode)
                printResponseCdata('<responseHeaders>%s</responseHeaders>', sample.responseHeaders)
                printResponseCdata('<responseBody>%s</responseBody>', sample.responseBody)
                indent -= 1
                printResponse('</sample>')
            indent -= 1
            printResponse('</apiSamples>')
        indent -= 1
        printResponse('</samples>')

p = LogProcessor("/v1")
for line in fileinput.input():
    p.process(line)
p.dumpByOrder()

