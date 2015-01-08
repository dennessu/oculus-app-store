package com.junbo.data.model
/**
 * Created by liangfu on 8/28/2014.
 */
class TosData {
    String title
    String content
    String type
    String version
    Double minorversion
    List<String> countries
    List<String> coveredLocales
    Map<String, TosLocalePropertyData> locales
}