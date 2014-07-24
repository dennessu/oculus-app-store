package com.junbo.billing.clientproxy.impl.sabrix

import groovy.transform.CompileStatic

/**
 * Warehouse Entity.
 */
@CompileStatic
public enum Warehouse {
    WAREHOUSE_US('US', 'RU,BS,BM,CA,PR,US,BB', false),
    WAREHOUSE_AU('AU', 'AU,PF,NC,NZ,PG', false),
    WAREHOUSE_SZ('CN', 'CN', false),
    WAREHOUSE_HK('HK', 'AR,BD,BO,BR,KH,CL,CO,CR,EC,SV,GT,HN,HK,IN,ID,IQ,JP,MO,MY,MX,MN,PK,PA,PY,PE,PH,SG,LK,TW,TH,UG,UY,VE,VN,DO', false),
    WAREHOUSE_UK('UK', 'AL,AD,AM,AT,AZ,BH,BY,BE,BA,BN,BG,CM,IC,HR,CY,CZ,DK,EG,EE,FO,FI,FR,GF,GE,DE,GI,GR,GL,GP,GG,HU,IS,IE,IL,IT,JE,KZ,KE,KW,KG,LV,LB,LI,LT,LU,MK,MT,MQ,MU,MD,MC,ME,MA,NL,NG,NO,OM,PL,PT,QA,RE,RO,SA,CS,SK,SI,ZA,ES,SE,CH,TN,TR,AE,GB,UZ', true)

    String location
    String shipCountryList
    Boolean isEUCountry

    Warehouse(String location, String shipCountryList, Boolean isEUCountry) {
        this.location = location
        this.shipCountryList = shipCountryList
        this.isEUCountry = isEUCountry
    }
}