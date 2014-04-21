/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.spec.def;


import com.junbo.common.util.Identifiable;

import javax.ws.rs.NotSupportedException;

/**
 * The currencies (ISO 4217). Reference: <a href="http://en.wikipedia.org/wiki/ISO_4217">Wikipedia ISO 4217</a>.
 */
public enum Currency implements Identifiable<Integer> {

    AED(784, Digits.DIGITS_2, "United Arab Emirates dirham"),
    AFN(971, Digits.DIGITS_2, "Afghan afghani"),
    ALL(8,   Digits.DIGITS_2, "Albanian lek"),
    AMD(51,  Digits.DIGITS_2, "Armenian dram"),
    ANG(532, Digits.DIGITS_2, "Netherlands Antillean guilder"),
    AOA(973, Digits.DIGITS_2, "Angolan kwanza"),
    ARS(32,  Digits.DIGITS_2, "Argentine peso"),
    AUD(36,  Digits.DIGITS_2, "Australian dollar"),
    AWG(533, Digits.DIGITS_2, "Aruban florin"),
    AZN(944, Digits.DIGITS_2, "Azerbaijani manat"),
    BAM(977, Digits.DIGITS_2, "Bosnia and Herzegovina convertible mark"),
    BBD(52,  Digits.DIGITS_2, "Barbados dollar"),
    BDT(50,  Digits.DIGITS_2, "Bangladeshi taka"),
    BGN(975, Digits.DIGITS_2, "Bulgarian lev"),
    BHD(48,  Digits.DIGITS_3, "Bahraini dinar"),
    BIF(108, Digits.DIGITS_0, "Burundian franc"),
    BMD(60,  Digits.DIGITS_2, "Bermudian dollar"),
    BND(96,  Digits.DIGITS_2, "Brunei dollar"),
    BOB(68,  Digits.DIGITS_2, "Boliviano"),
    BOV(984, Digits.DIGITS_2, "Bolivian Mvdol (funds code)"),
    BRL(986, Digits.DIGITS_2, "Brazilian real"),
    BSD(44,  Digits.DIGITS_2, "Bahamian dollar"),
    BTN(64,  Digits.DIGITS_2, "Bhutanese ngultrum"),
    BWP(72,  Digits.DIGITS_2, "Botswana pula"),
    BYR(974, Digits.DIGITS_0, "Belarusian ruble"),
    BZD(84,  Digits.DIGITS_2, "Belize dollar"),
    CAD(124, Digits.DIGITS_2, "Canadian dollar"),
    CDF(976, Digits.DIGITS_2, "Congolese franc"),
    CHE(947, Digits.DIGITS_2, "WIR Euro (complementary currency)"),
    CHF(756, Digits.DIGITS_2, "Swiss franc"),
    CHW(948, Digits.DIGITS_2, "WIR Franc (complementary currency)"),
    CLF(990, Digits.DIGITS_0, "Unidad de Fomento (funds code)"),
    CLP(152, Digits.DIGITS_0, "Chilean peso"),
    CNY(156, Digits.DIGITS_2, "Chinese yuan"),
    COP(170, Digits.DIGITS_2, "Colombian peso"),
    COU(970, Digits.DIGITS_2, "Unidad de Valor Real"),
    CRC(188, Digits.DIGITS_2, "Costa Rican colon"),
    CUC(931, Digits.DIGITS_2, "Cuban convertible peso"),
    CUP(192, Digits.DIGITS_2, "Cuban peso"),
    CVE(132, Digits.DIGITS_0, "Cape Verde escudo"),
    CZK(203, Digits.DIGITS_2, "Czech koruna"),
    DJF(262, Digits.DIGITS_0, "Djiboutian franc"),
    DKK(208, Digits.DIGITS_2, "Danish krone"),
    DOP(214, Digits.DIGITS_2, "Dominican peso"),
    DZD(12,  Digits.DIGITS_2, "Algerian dinar"),
    EGP(818, Digits.DIGITS_2, "Egyptian pound"),
    ERN(232, Digits.DIGITS_2, "Eritrean nakfa"),
    ETB(230, Digits.DIGITS_2, "Ethiopian birr"),
    EUR(978, Digits.DIGITS_2, "Euro"),
    FJD(242, Digits.DIGITS_2, "Fiji dollar"),
    FKP(238, Digits.DIGITS_2, "Falkland Islands pound"),
    GBP(826, Digits.DIGITS_2, "Pound sterling"),
    GEL(981, Digits.DIGITS_2, "Georgian lari"),
    GHS(936, Digits.DIGITS_2, "Ghanaian cedi"),
    GIP(292, Digits.DIGITS_2, "Gibraltar pound"),
    GMD(270, Digits.DIGITS_2, "Gambian dalasi"),
    GNF(324, Digits.DIGITS_0, "Guinean franc"),
    GTQ(320, Digits.DIGITS_2, "Guatemalan quetzal"),
    GYD(328, Digits.DIGITS_2, "Guyanese dollar"),
    HKD(344, Digits.DIGITS_2, "Hong Kong dollar"),
    HNL(340, Digits.DIGITS_2, "Honduran lempira"),
    HRK(191, Digits.DIGITS_2, "Croatian kuna"),
    HTG(332, Digits.DIGITS_2, "Haitian gourde"),
    HUF(348, Digits.DIGITS_2, "Hungarian forint"),
    IDR(360, Digits.DIGITS_2, "Indonesian rupiah"),
    ILS(376, Digits.DIGITS_2, "Israeli new shekel"),
    INR(356, Digits.DIGITS_2, "Indian rupee"),
    IQD(368, Digits.DIGITS_3, "Iraqi dinar"),
    IRR(364, Digits.DIGITS_0, "Iranian rial"),
    ISK(352, Digits.DIGITS_0, "Icelandic króna"),
    JMD(388, Digits.DIGITS_2, "Jamaican dollar"),
    JOD(400, Digits.DIGITS_3, "Jordanian dinar"),
    JPY(392, Digits.DIGITS_0, "Japanese yen"),
    KES(404, Digits.DIGITS_2, "Kenyan shilling"),
    KGS(417, Digits.DIGITS_2, "Kyrgyzstani som"),
    KHR(116, Digits.DIGITS_2, "Cambodian riel"),
    KMF(174, Digits.DIGITS_0, "Comoro franc"),
    KPW(408, Digits.DIGITS_0, "North Korean won"),
    KRW(410, Digits.DIGITS_0, "South Korean won"),
    KWD(414, Digits.DIGITS_3, "Kuwaiti dinar"),
    KYD(136, Digits.DIGITS_2, "Cayman Islands dollar"),
    KZT(398, Digits.DIGITS_2, "Kazakhstani tenge"),
    LAK(418, Digits.DIGITS_0, "Lao kip"),
    LBP(422, Digits.DIGITS_0, "Lebanese pound"),
    LKR(144, Digits.DIGITS_2, "Sri Lankan rupee"),
    LRD(430, Digits.DIGITS_2, "Liberian dollar"),
    LSL(426, Digits.DIGITS_2, "Lesotho loti"),
    LTL(440, Digits.DIGITS_2, "Lithuanian litas"),
    LVL(428, Digits.DIGITS_2, "Latvian lats"),
    LYD(434, Digits.DIGITS_3, "Libyan dinar"),
    MAD(504, Digits.DIGITS_2, "Moroccan dirham"),
    MDL(498, Digits.DIGITS_2, "Moldovan leu"),
    MGA(969, Digits.DIGITS_07, "Malagasy ariary"),
    MKD(807, Digits.DIGITS_0, "Macedonian denar"),
    MMK(104, Digits.DIGITS_0, "Myanma kyat"),
    MNT(496, Digits.DIGITS_2, "Mongolian tugrik"),
    MOP(446, Digits.DIGITS_2, "Macanese pataca"),
    MRO(478, Digits.DIGITS_07, "Mauritanian ouguiya"),
    MUR(480, Digits.DIGITS_2, "Mauritian rupee"),
    MVR(462, Digits.DIGITS_2, "Maldivian rufiyaa"),
    MWK(454, Digits.DIGITS_2, "Malawian kwacha"),
    MXN(484, Digits.DIGITS_2, "Mexican peso"),
    MXV(979, Digits.DIGITS_2, "Mexican Unidad de Inversion (UDI) (funds code)"),
    MYR(458, Digits.DIGITS_2, "Malaysian ringgit"),
    MZN(943, Digits.DIGITS_2, "Mozambican metical"),
    NAD(516, Digits.DIGITS_2, "Namibian dollar"),
    NGN(566, Digits.DIGITS_2, "Nigerian naira"),
    NIO(558, Digits.DIGITS_2, "Nicaraguan córdoba"),
    NOK(578, Digits.DIGITS_2, "Norwegian krone"),
    NPR(524, Digits.DIGITS_2, "Nepalese rupee"),
    NZD(554, Digits.DIGITS_2, "New Zealand dollar"),
    OMR(512, Digits.DIGITS_3, "Omani rial"),
    PAB(590, Digits.DIGITS_2, "Panamanian balboa"),
    PEN(604, Digits.DIGITS_2, "Peruvian nuevo sol"),
    PGK(598, Digits.DIGITS_2, "Papua New Guinean kina"),
    PHP(608, Digits.DIGITS_2, "Philippine peso"),
    PKR(586, Digits.DIGITS_2, "Pakistani rupee"),
    PLN(985, Digits.DIGITS_2, "Polish złoty"),
    PYG(600, Digits.DIGITS_0, "Paraguayan guaraní"),
    QAR(634, Digits.DIGITS_2, "Qatari riyal"),
    RON(946, Digits.DIGITS_2, "Romanian new leu"),
    RSD(941, Digits.DIGITS_2, "Serbian dinar"),
    RUB(643, Digits.DIGITS_2, "Russian rouble"),
    RWF(646, Digits.DIGITS_0, "Rwandan franc"),
    SAR(682, Digits.DIGITS_2, "Saudi riyal"),
    SBD(90,  Digits.DIGITS_2, "Solomon Islands dollar"),
    SCR(690, Digits.DIGITS_2, "Seychelles rupee"),
    SDG(938, Digits.DIGITS_2, "Sudanese pound"),
    SEK(752, Digits.DIGITS_2, "Swedish krona/kronor"),
    SGD(702, Digits.DIGITS_2, "Singapore dollar"),
    SHP(654, Digits.DIGITS_2, "Saint Helena pound"),
    SLL(694, Digits.DIGITS_0, "Sierra Leonean leone"),
    SOS(706, Digits.DIGITS_2, "Somali shilling"),
    SRD(968, Digits.DIGITS_2, "Surinamese dollar"),
    SSP(728, Digits.DIGITS_2, "South Sudanese pound"),
    STD(678, Digits.DIGITS_0, "São Tomé and Príncipe dobra"),
    SYP(760, Digits.DIGITS_2, "Syrian pound"),
    SZL(748, Digits.DIGITS_2, "Swazi lilangeni"),
    THB(764, Digits.DIGITS_2, "Thai baht"),
    TJS(972, Digits.DIGITS_2, "Tajikistani somoni"),
    TMT(934, Digits.DIGITS_2, "Turkmenistani manat"),
    TND(788, Digits.DIGITS_3, "Tunisian dinar"),
    TOP(776, Digits.DIGITS_2, "Tongan paʻanga"),
    TRY(949, Digits.DIGITS_2, "Turkish lira"),
    TTD(780, Digits.DIGITS_2, "Trinidad and Tobago dollar"),
    TWD(901, Digits.DIGITS_2, "New Taiwan dollar"),
    TZS(834, Digits.DIGITS_2, "Tanzanian shilling"),
    UAH(980, Digits.DIGITS_2, "Ukrainian hryvnia"),
    UGX(800, Digits.DIGITS_2, "Ugandan shilling"),
    USD(840, Digits.DIGITS_2, "United States dollar"),
    USN(997, Digits.DIGITS_2, "United States dollar (next day) (funds code)"),
    USS(998, Digits.DIGITS_2, "United States dollar (same day) (funds code)"),
    UYI(940, Digits.DIGITS_0, "Uruguay Peso en Unidades Indexadas (URUIURUI) (funds code)"),
    UYU(858, Digits.DIGITS_2, "Uruguayan peso"),
    UZS(860, Digits.DIGITS_2, "Uzbekistan som"),
    VEF(937, Digits.DIGITS_2, "Venezuelan bolívar fuerte"),
    VND(704, Digits.DIGITS_0, "Vietnamese dong"),
    VUV(548, Digits.DIGITS_0, "Vanuatu vatu"),
    WST(882, Digits.DIGITS_2, "Samoan tala"),
    XAF(950, Digits.DIGITS_0, "CFA franc BEAC"),
    XAG(961, Digits.DIGITS_NO, "Silver (one troy ounce)"),
    XAU(959, Digits.DIGITS_NO, "Gold (one troy ounce)"),
    XBA(955, Digits.DIGITS_NO, "European Composite Unit (EURCO) (bond market unit)"),
    XBB(956, Digits.DIGITS_NO, "European Monetary Unit (E.M.U.-6) (bond market unit)"),
    XBC(957, Digits.DIGITS_NO, "European Unit of Account 9 (E.U.A.-9) (bond market unit)"),
    XBD(958, Digits.DIGITS_NO, "European Unit of Account 17 (E.U.A.-17) (bond market unit)"),
    XCD(951, Digits.DIGITS_2, "East Caribbean dollar"),
    XDR(960, Digits.DIGITS_NO, "Special drawing rights  International Monetary Fund"),
    XFU(-1,  Digits.DIGITS_NO, "UIC franc (special settlement currency)"),
    XOF(952, Digits.DIGITS_0, "CFA franc BCEAO"),
    XPD(964, Digits.DIGITS_NO, "Palladium (one troy ounce)"),
    XPF(953, Digits.DIGITS_0, "CFP franc"),
    XPT(962, Digits.DIGITS_NO, "Platinum (one troy ounce)"),
    XTS(963, Digits.DIGITS_NO, "Code reserved for testing purposes"),
    XXX(999, Digits.DIGITS_NO, "No currency"),
    YER(886, Digits.DIGITS_2, "Yemeni rial"),
    ZAR(710, Digits.DIGITS_2, "South African rand"),
    ZMK(894, Digits.DIGITS_2, "Zambian kwacha");


    private final String code;
    private final String name;
    private final int numeric;
    private final Digits digits;

    private enum Digits { DIGITS_0, DIGITS_2, DIGITS_3, DIGITS_07, DIGITS_NO };

    private Currency(int numeric, Digits digits, String name) {
        this.code = name().toUpperCase();
        this.name = name;
        this.numeric = numeric;
        this.digits = digits;
    }

    /**
     * Returns the currency code.
     *
     * @return  The code, e.g. "USD", "EUR", etc.
     */
    public String getCode() {
        return code;
    }

    /**
     * Implement Identifiable.
     * @return ISOCodeNumeric.
     */
    @Override
    public Integer getId(){
        return numeric;
    }

    @Override
    public void setId(Integer id) {
        throw new NotSupportedException("enum Currency not settable");
    }

    /**
     * Returns the currency name in English.
     *
     * @return  The English currency name, e.g. "United States dollar".
     */
    public String getName() {
        return name;
    }

    /**
     * Formats and outputs the amount for the given currency.
     *
     * @param amount  The amount.
     * @return  The formatted amount, e.g. "USD 10.38".
     */
    public String format(long amount) {
        String formatted;
        switch (digits) {
            case DIGITS_0:{
                // e.g. "10"
                formatted = String.valueOf(amount);
                break;
            }
            case DIGITS_2:{
                // e.g. "10.99"
                String a = String.valueOf(amount / 100);
                String b = String.valueOf(amount % 100);
                while (b.length() < 2) {
                    b = "0" + b;
                }
                formatted = a + "." + b;
                break;
            }
            case DIGITS_3:{
                // e.g. "10.999"
                String a = String.valueOf(amount / 1000);
                String b = String.valueOf(amount % 1000);
                while (b.length() < 3) {
                    b = "0" + b;
                }
                formatted = a + "." + b;
                break;
            }
            case DIGITS_07:{
                formatted = String.valueOf(amount);
                break;
            }
            case DIGITS_NO:{
                formatted = String.valueOf(amount);
                break;
            }
            default:
                formatted = String.valueOf(amount);
        }
        return getCode() + " " + formatted;
    }

    /**
     * Returns the currency for the given code.
     *
     * @param code  The code, e.g. "USD", "EUR", etc.
     * @return  The corresponding currency or null if it doesn't exist.
     */
    public static Currency find(String code) {
        for (int i = 0; i < values().length; i++) {
            if (values()[i].getCode().equals(code)) {
                return values()[i];
            }
        }

        // not found
        return null;
    }

    public String getISOCodeAlpha() {
        return name();
    }

    public int getISOCodeNumeric() {
        return numeric;
    }

    @Override
    public String toString() {
        return code;
    }
}
