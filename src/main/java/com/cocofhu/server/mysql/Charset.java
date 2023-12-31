package com.cocofhu.server.mysql;

import java.util.HashMap;

public abstract class Charset {
    public static final int UTF_8 = 255;
    public static final HashMap<Integer,String> UTF8_COLLATION_NAME;
    static {
        UTF8_COLLATION_NAME = new HashMap<>();
        UTF8_COLLATION_NAME.put(33, "utf8mb3_general_ci");
        UTF8_COLLATION_NAME.put(45, "utf8mb4_general_ci");
        UTF8_COLLATION_NAME.put(46, "utf8mb4_bin");
        UTF8_COLLATION_NAME.put(76, "utf8mb3_tolower_ci");
        UTF8_COLLATION_NAME.put(83, "utf8mb3_bin");
        UTF8_COLLATION_NAME.put(192, "utf8mb3_unicode_ci");
        UTF8_COLLATION_NAME.put(193, "utf8mb3_icelandic_ci");
        UTF8_COLLATION_NAME.put(194, "utf8mb3_latvian_ci");
        UTF8_COLLATION_NAME.put(195, "utf8mb3_romanian_ci");
        UTF8_COLLATION_NAME.put(196, "utf8mb3_slovenian_ci");
        UTF8_COLLATION_NAME.put(197, "utf8mb3_polish_ci");
        UTF8_COLLATION_NAME.put(198, "utf8mb3_estonian_ci");
        UTF8_COLLATION_NAME.put(199, "utf8mb3_spanish_ci");
        UTF8_COLLATION_NAME.put(200, "utf8mb3_swedish_ci");
        UTF8_COLLATION_NAME.put(201, "utf8mb3_turkish_ci");
        UTF8_COLLATION_NAME.put(202, "utf8mb3_czech_ci");
        UTF8_COLLATION_NAME.put(203, "utf8mb3_danish_ci");
        UTF8_COLLATION_NAME.put(204, "utf8mb3_lithuanian_ci");
        UTF8_COLLATION_NAME.put(205, "utf8mb3_slovak_ci");
        UTF8_COLLATION_NAME.put(206, "utf8mb3_spanish2_ci");
        UTF8_COLLATION_NAME.put(207, "utf8mb3_roman_ci");
        UTF8_COLLATION_NAME.put(208, "utf8mb3_persian_ci");
        UTF8_COLLATION_NAME.put(209, "utf8mb3_esperanto_ci");
        UTF8_COLLATION_NAME.put(210, "utf8mb3_hungarian_ci");
        UTF8_COLLATION_NAME.put(211, "utf8mb3_sinhala_ci");
        UTF8_COLLATION_NAME.put(212, "utf8mb3_german2_ci");
        UTF8_COLLATION_NAME.put(213, "utf8mb3_croatian_ci");
        UTF8_COLLATION_NAME.put(214, "utf8mb3_unicode_520_ci");
        UTF8_COLLATION_NAME.put(215, "utf8mb3_vietnamese_ci");
        UTF8_COLLATION_NAME.put(223, "utf8mb3_general_mysql500_ci");
        UTF8_COLLATION_NAME.put(224, "utf8mb4_unicode_ci");
        UTF8_COLLATION_NAME.put(225, "utf8mb4_icelandic_ci");
        UTF8_COLLATION_NAME.put(226, "utf8mb4_latvian_ci");
        UTF8_COLLATION_NAME.put(227, "utf8mb4_romanian_ci");
        UTF8_COLLATION_NAME.put(228, "utf8mb4_slovenian_ci");
        UTF8_COLLATION_NAME.put(229, "utf8mb4_polish_ci");
        UTF8_COLLATION_NAME.put(230, "utf8mb4_estonian_ci");
        UTF8_COLLATION_NAME.put(231, "utf8mb4_spanish_ci");
        UTF8_COLLATION_NAME.put(232, "utf8mb4_swedish_ci");
        UTF8_COLLATION_NAME.put(233, "utf8mb4_turkish_ci");
        UTF8_COLLATION_NAME.put(234, "utf8mb4_czech_ci");
        UTF8_COLLATION_NAME.put(235, "utf8mb4_danish_ci");
        UTF8_COLLATION_NAME.put(236, "utf8mb4_lithuanian_ci");
        UTF8_COLLATION_NAME.put(237, "utf8mb4_slovak_ci");
        UTF8_COLLATION_NAME.put(238, "utf8mb4_spanish2_ci");
        UTF8_COLLATION_NAME.put(239, "utf8mb4_roman_ci");
        UTF8_COLLATION_NAME.put(240, "utf8mb4_persian_ci");
        UTF8_COLLATION_NAME.put(241, "utf8mb4_esperanto_ci");
        UTF8_COLLATION_NAME.put(242, "utf8mb4_hungarian_ci");
        UTF8_COLLATION_NAME.put(243, "utf8mb4_sinhala_ci");
        UTF8_COLLATION_NAME.put(244, "utf8mb4_german2_ci");
        UTF8_COLLATION_NAME.put(245, "utf8mb4_croatian_ci");
        UTF8_COLLATION_NAME.put(246, "utf8mb4_unicode_520_ci");
        UTF8_COLLATION_NAME.put(247, "utf8mb4_vietnamese_ci");
        UTF8_COLLATION_NAME.put(255, "utf8mb4_0900_ai_ci");
        UTF8_COLLATION_NAME.put(256, "utf8mb4_de_pb_0900_ai_ci");
        UTF8_COLLATION_NAME.put(257, "utf8mb4_is_0900_ai_ci");
        UTF8_COLLATION_NAME.put(258, "utf8mb4_lv_0900_ai_ci");
        UTF8_COLLATION_NAME.put(259, "utf8mb4_ro_0900_ai_ci");
        UTF8_COLLATION_NAME.put(260, "utf8mb4_sl_0900_ai_ci");
        UTF8_COLLATION_NAME.put(261, "utf8mb4_pl_0900_ai_ci");
        UTF8_COLLATION_NAME.put(262, "utf8mb4_et_0900_ai_ci");
        UTF8_COLLATION_NAME.put(263, "utf8mb4_es_0900_ai_ci");
        UTF8_COLLATION_NAME.put(264, "utf8mb4_sv_0900_ai_ci");
        UTF8_COLLATION_NAME.put(265, "utf8mb4_tr_0900_ai_ci");
        UTF8_COLLATION_NAME.put(266, "utf8mb4_cs_0900_ai_ci");
        UTF8_COLLATION_NAME.put(267, "utf8mb4_da_0900_ai_ci");
        UTF8_COLLATION_NAME.put(268, "utf8mb4_lt_0900_ai_ci");
        UTF8_COLLATION_NAME.put(269, "utf8mb4_sk_0900_ai_ci");
        UTF8_COLLATION_NAME.put(270, "utf8mb4_es_trad_0900_ai_ci");
        UTF8_COLLATION_NAME.put(271, "utf8mb4_la_0900_ai_ci");
        UTF8_COLLATION_NAME.put(273, "utf8mb4_eo_0900_ai_ci");
        UTF8_COLLATION_NAME.put(274, "utf8mb4_hu_0900_ai_ci");
        UTF8_COLLATION_NAME.put(275, "utf8mb4_hr_0900_ai_ci");
        UTF8_COLLATION_NAME.put(277, "utf8mb4_vi_0900_ai_ci");
        UTF8_COLLATION_NAME.put(278, "utf8mb4_0900_as_cs");
        UTF8_COLLATION_NAME.put(279, "utf8mb4_de_pb_0900_as_cs");
        UTF8_COLLATION_NAME.put(280, "utf8mb4_is_0900_as_cs");
        UTF8_COLLATION_NAME.put(281, "utf8mb4_lv_0900_as_cs");
        UTF8_COLLATION_NAME.put(282, "utf8mb4_ro_0900_as_cs");
        UTF8_COLLATION_NAME.put(283, "utf8mb4_sl_0900_as_cs");
        UTF8_COLLATION_NAME.put(284, "utf8mb4_pl_0900_as_cs");
        UTF8_COLLATION_NAME.put(285, "utf8mb4_et_0900_as_cs");
        UTF8_COLLATION_NAME.put(286, "utf8mb4_es_0900_as_cs");
        UTF8_COLLATION_NAME.put(287, "utf8mb4_sv_0900_as_cs");
        UTF8_COLLATION_NAME.put(288, "utf8mb4_tr_0900_as_cs");
        UTF8_COLLATION_NAME.put(289, "utf8mb4_cs_0900_as_cs");
        UTF8_COLLATION_NAME.put(290, "utf8mb4_da_0900_as_cs");
        UTF8_COLLATION_NAME.put(291, "utf8mb4_lt_0900_as_cs");
        UTF8_COLLATION_NAME.put(292, "utf8mb4_sk_0900_as_cs");
        UTF8_COLLATION_NAME.put(293, "utf8mb4_es_trad_0900_as_cs");
        UTF8_COLLATION_NAME.put(294, "utf8mb4_la_0900_as_cs");
        UTF8_COLLATION_NAME.put(296, "utf8mb4_eo_0900_as_cs");
        UTF8_COLLATION_NAME.put(297, "utf8mb4_hu_0900_as_cs");
        UTF8_COLLATION_NAME.put(298, "utf8mb4_hr_0900_as_cs");
        UTF8_COLLATION_NAME.put(300, "utf8mb4_vi_0900_as_cs");
        UTF8_COLLATION_NAME.put(303, "utf8mb4_ja_0900_as_cs");
        UTF8_COLLATION_NAME.put(304, "utf8mb4_ja_0900_as_cs_ks");
        UTF8_COLLATION_NAME.put(305, "utf8mb4_0900_as_ci");
        UTF8_COLLATION_NAME.put(306, "utf8mb4_ru_0900_ai_ci");
        UTF8_COLLATION_NAME.put(307, "utf8mb4_ru_0900_as_cs");
        UTF8_COLLATION_NAME.put(308, "utf8mb4_zh_0900_as_cs");
        UTF8_COLLATION_NAME.put(309, "utf8mb4_0900_bin");
        UTF8_COLLATION_NAME.put(310, "utf8mb4_nb_0900_ai_ci");
        UTF8_COLLATION_NAME.put(311, "utf8mb4_nb_0900_as_cs");
        UTF8_COLLATION_NAME.put(312, "utf8mb4_nn_0900_ai_ci");
        UTF8_COLLATION_NAME.put(313, "utf8mb4_nn_0900_as_cs");
        UTF8_COLLATION_NAME.put(314, "utf8mb4_sr_latn_0900_ai_ci");
        UTF8_COLLATION_NAME.put(315, "utf8mb4_sr_latn_0900_as_cs");
        UTF8_COLLATION_NAME.put(316, "utf8mb4_bs_0900_ai_ci");
        UTF8_COLLATION_NAME.put(317, "utf8mb4_bs_0900_as_cs");
        UTF8_COLLATION_NAME.put(318, "utf8mb4_bg_0900_ai_ci");
        UTF8_COLLATION_NAME.put(319, "utf8mb4_bg_0900_as_cs");
        UTF8_COLLATION_NAME.put(320, "utf8mb4_gl_0900_ai_ci");
        UTF8_COLLATION_NAME.put(321, "utf8mb4_gl_0900_as_cs");
        UTF8_COLLATION_NAME.put(322, "utf8mb4_mn_cyrl_0900_ai_ci");
        UTF8_COLLATION_NAME.put(323, "utf8mb4_mn_cyrl_0900_as_cs");
    }
}
