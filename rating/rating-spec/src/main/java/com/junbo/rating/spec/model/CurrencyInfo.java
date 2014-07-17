/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.spec.model;

import com.junbo.rating.spec.error.AppErrors;

/**
 * The currencies (ISO 4217). Reference: <a href="http://en.wikipedia.org/wiki/ISO_4217">Wikipedia ISO 4217</a>.
 */
public enum CurrencyInfo {
    AED(784, "United Arab Emirates dirham", 2),
    AFN(971, "Afghan afghani", 2),
    ALL(8, "Albanian lek", 2),
    AMD(51, "Armenian dram", 2),
    ANG(532, "Netherlands Antillean guilder", 2),
    AOA(973, "Angolan kwanza", 2),
    ARS(32, "Argentine peso", 2),
    AUD(36, "Australian dollar", 2),
    AWG(533, "Aruban florin", 2),
    AZN(944, "Azerbaijani manat", 2),
    BAM(977, "Bosnia and Herzegovina convertible mark", 2),
    BBD(52, "Barbados dollar", 2),
    BDT(50, "Bangladeshi taka", 2),
    BGN(975, "Bulgarian lev", 2),
    BHD(48, "Bahraini dinar", 3),
    BIF(108, "Burundian franc", 0),
    BMD(60, "Bermudian dollar", 2),
    BND(96, "Brunei dollar", 2),
    BOB(68, "Boliviano", 2),
    BOV(984, "Bolivian Mvdol (funds code)", 2),
    BRL(986, "Brazilian real", 2),
    BSD(44, "Bahamian dollar", 2),
    BTN(64, "Bhutanese ngultrum", 2),
    BWP(72, "Botswana pula", 2),
    BYR(974, "Belarusian ruble", 0),
    BZD(84, "Belize dollar", 2),
    CAD(124, "Canadian dollar", 2),
    CDF(976, "Congolese franc", 2),
    CHE(947, "WIR Euro (complementary currency)", 2),
    CHF(756, "Swiss franc", 2),
    CHW(948, "WIR Franc (complementary currency)", 2),
    CLF(990, "Unidad de Fomento (funds code)", 0),
    CLP(152, "Chilean peso", 0),
    CNY(156, "Chinese yuan", 2),
    COP(170, "Colombian peso", 2),
    COU(970, "Unidad de Valor Real", 2),
    CRC(188, "Costa Rican colon", 2),
    CUC(931, "Cuban convertible peso", 2),
    CUP(192, "Cuban peso", 2),
    CVE(132, "Cape Verde escudo", 0),
    CZK(203, "Czech koruna", 2),
    DJF(262, "Djiboutian franc", 0),
    DKK(208, "Danish krone", 2),
    DOP(214, "Dominican peso", 2),
    DZD(12, "Algerian dinar", 2),
    EGP(818, "Egyptian pound", 2),
    ERN(232, "Eritrean nakfa", 2),
    ETB(230, "Ethiopian birr", 2),
    EUR(978, "Euro", 2),
    FJD(242, "Fiji dollar", 2),
    FKP(238, "Falkland Islands pound", 2),
    GBP(826, "Pound sterling", 2),
    GEL(981, "Georgian lari", 2),
    GHS(936, "Ghanaian cedi", 2),
    GIP(292, "Gibraltar pound", 2),
    GMD(270, "Gambian dalasi", 2),
    GNF(324, "Guinean franc", 0),
    GTQ(320, "Guatemalan quetzal", 2),
    GYD(328, "Guyanese dollar", 2),
    HKD(344, "Hong Kong dollar", 2),
    HNL(340, "Honduran lempira", 2),
    HRK(191, "Croatian kuna", 2),
    HTG(332, "Haitian gourde", 2),
    HUF(348, "Hungarian forint", 2),
    IDR(360, "Indonesian rupiah", 2),
    ILS(376, "Israeli new shekel", 2),
    INR(356, "Indian rupee", 2),
    IQD(368, "Iraqi dinar", 3),
    IRR(364, "Iranian rial", 0),
    ISK(352, "Icelandic króna", 0),
    JMD(388, "Jamaican dollar", 2),
    JOD(400, "Jordanian dinar", 3),
    JPY(392, "Japanese yen", 0),
    KES(404, "Kenyan shilling", 2),
    KGS(417, "Kyrgyzstani som", 2),
    KHR(116, "Cambodian riel", 2),
    KMF(174, "Comoro franc", 0),
    KPW(408, "North Korean won", 0),
    KRW(410, "South Korean won", 0),
    KWD(414, "Kuwaiti dinar", 3),
    KYD(136, "Cayman Islands dollar", 2),
    KZT(398, "Kazakhstani tenge", 2),
    LAK(418, "Lao kip", 0),
    LBP(422, "Lebanese pound", 0),
    LKR(144, "Sri Lankan rupee", 2),
    LRD(430, "Liberian dollar", 2),
    LSL(426, "Lesotho loti", 2),
    LTL(440, "Lithuanian litas", 2),
    LVL(428, "Latvian lats", 2),
    LYD(434, "Libyan dinar", 3),
    MAD(504, "Moroccan dirham", 2),
    MDL(498, "Moldovan leu", 2),
    MGA(969, "Malagasy ariary", 0),
    MKD(807, "Macedonian denar", 0),
    MMK(104, "Myanma kyat", 0),
    MNT(496, "Mongolian tugrik", 2),
    MOP(446, "Macanese pataca", 2),
    MRO(478, "Mauritanian ouguiya", 0),
    MUR(480, "Mauritian rupee", 2),
    MVR(462, "Maldivian rufiyaa", 2),
    MWK(454, "Malawian kwacha", 2),
    MXN(484, "Mexican peso", 2),
    MXV(979, "Mexican Unidad de Inversion (UDI) (funds code)", 2),
    MYR(458, "Malaysian ringgit", 2),
    MZN(943, "Mozambican metical", 2),
    NAD(516, "Namibian dollar", 2),
    NGN(566, "Nigerian naira", 2),
    NIO(558, "Nicaraguan córdoba", 2),
    NOK(578, "Norwegian krone", 2),
    NPR(524, "Nepalese rupee", 2),
    NZD(554, "New Zealand dollar", 2),
    OMR(512, "Omani rial", 3),
    PAB(590, "Panamanian balboa", 2),
    PEN(604, "Peruvian nuevo sol", 2),
    PGK(598, "Papua New Guinean kina", 2),
    PHP(608, "Philippine peso", 2),
    PKR(586, "Pakistani rupee", 2),
    PLN(985, "Polish złoty", 2),
    PYG(600, "Paraguayan guaraní", 0),
    QAR(634, "Qatari riyal", 2),
    RON(946, "Romanian new leu", 2),
    RSD(941, "Serbian dinar", 2),
    RUB(643, "Russian rouble", 2),
    RWF(646, "Rwandan franc", 0),
    SAR(682, "Saudi riyal", 2),
    SBD(90, "Solomon Islands dollar", 2),
    SCR(690, "Seychelles rupee", 2),
    SDG(938, "Sudanese pound", 2),
    SEK(752, "Swedish krona/kronor", 2),
    SGD(702, "Singapore dollar", 2),
    SHP(654, "Saint Helena pound", 2),
    SLL(694, "Sierra Leonean leone", 0),
    SOS(706, "Somali shilling", 2),
    SRD(968, "Surinamese dollar", 2),
    SSP(728, "South Sudanese pound", 2),
    STD(678, "São Tomé and Príncipe dobra", 0),
    SYP(760, "Syrian pound", 2),
    SZL(748, "Swazi lilangeni", 2),
    THB(764, "Thai baht", 2),
    TJS(972, "Tajikistani somoni", 2),
    TMT(934, "Turkmenistani manat", 2),
    TND(788, "Tunisian dinar", 3),
    TOP(776, "Tongan paʻanga", 2),
    TRY(949, "Turkish lira", 2),
    TTD(780, "Trinidad and Tobago dollar", 2),
    TWD(901, "New Taiwan dollar", 2),
    TZS(834, "Tanzanian shilling", 2),
    UAH(980, "Ukrainian hryvnia", 2),
    UGX(800, "Ugandan shilling", 2),
    USD(840, "United States dollar", 2),
    USN(997, "United States dollar (next day) (funds code)", 2),
    USS(998, "United States dollar (same day) (funds code)", 2),
    UYI(940, "Uruguay Peso en Unidades Indexadas (URUIURUI) (funds code)", 0),
    UYU(858, "Uruguayan peso", 2),
    UZS(860, "Uzbekistan som", 2),
    VEF(937, "Venezuelan bolívar fuerte", 2),
    VND(704, "Vietnamese dong", 0),
    VUV(548, "Vanuatu vatu", 0),
    WST(882, "Samoan tala", 2),
    XAF(950, "CFA franc BEAC", 0),
    XAG(961, "Silver (one troy ounce)", 0),
    XAU(959, "Gold (one troy ounce)", 0),
    XBA(955, "European Composite Unit (EURCO) (bond market unit)", 0),
    XBB(956, "European Monetary Unit (E.M.U.-6) (bond market unit)", 0),
    XBC(957, "European Unit of Account 9 (E.U.A.-9) (bond market unit)", 0),
    XBD(958, "European Unit of Account 17 (E.U.A.-17) (bond market unit)", 0),
    XCD(951, "East Caribbean dollar", 2),
    XDR(960, "Special drawing rights  International Monetary Fund", 0),
    XFU(-1, "UIC franc (special settlement currency)", 0),
    XOF(952, "CFA franc BCEAO", 0),
    XPD(964, "Palladium (one troy ounce)", 0),
    XPF(953, "CFP franc", 0),
    XPT(962, "Platinum (one troy ounce)", 0),
    XTS(963, "Code reserved for testing purposes", 0),
    XXX(999, "No currency", 0),
    YER(886, "Yemeni rial", 2),
    ZAR(710, "South African rand", 2),
    ZMK(894, "Zambian kwacha", 2);

    private final String code;
    private final int numeric;
    private final String name;
    private final int digits;

    private CurrencyInfo(int numeric, String name, int digits) {
        this.code = name().toUpperCase();
        this.name = name;
        this.numeric = numeric;
        this.digits = digits;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getNumeric() {
        return numeric;
    }

    public int getDigits() {
        return digits;
    }

    public static CurrencyInfo findByCode(String currencyCode) {
        for (int i = 0; i < values().length; i++) {
            if (values()[i].getCode().equalsIgnoreCase(currencyCode)) {
                return values()[i];
            }
        }

        throw AppErrors.INSTANCE.invalidCurrencyCode(currencyCode).exception();
    }
}
