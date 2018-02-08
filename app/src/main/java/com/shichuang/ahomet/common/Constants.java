package com.shichuang.ahomet.common;

/**
 * Created by Administrator on 2018/1/10.
 */

public interface Constants {
    String MAIN_ENGINE = "https://www.ahomet.com";
    //String MAIN_ENGINE = "http://139.196.231.53:8080";

    String ALIPAY_APP_ID = "2016011401092710";
    String ALIPAY_PID = "2088301826965745";
    String ALIPAY_SELLER = "ahomet_cttvnet@sina.com";
    String ALIPAY_NOTIFY_URL = MAIN_ENGINE + "/api/payment/alipaypc/notify_url_mobile";
    String ALIPAY_RSA_PRIVATE = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCaTElH+77AqG/+OtBVEvgX6amS5QgeHNKh/O75BbFQxf2MiUiJjdVTVB9feFvobPOz8WgO4Ul8z0egF+lJiae2+Gkpp8z93y8gxMKo6f8KgG9DY9X9b5qYsyIu5PRg+ZylK87D9H2P3bfnEqtpin/5XMIFPbxw7FK1KvN/SKhWIvIVyrQg74Of5hll/PzaYNzyLB7+lcihTyXMDIHV/BF43OOZWUypLcs2jRBrbTB6hr/f3GRRzEw9M3qCV0qQDBpXk2JP0SaQ/njmqy559taeSJgBcS3f5UoJn1qj8FukmhlwadLqKe0uJtlS9v9/rDyc01hs1ujweG/R+yRTYFRbAgMBAAECggEAJzDVNClnK/wcZhB+Nf2ZcklxPtRZmXxsq6RY9ntRrFh9rCJ6gCW8V2v4hAFIJuL1i3D3oz/psH/di3ZHVNxX8wtpeqEaWilECSiw4MJ+eyZgHFfRVIvyUnK9bffNcow0E2frHq6M7eu8+D4vt6DsHGRJC5fAs1XzM/zy2BOWXjoea3nJDZya0DjmeJhu77yFoUgXLlazuswa2C88m00G+a1hiCWW3pOS3XTd0wpDBefZYwbo9oImCxNeAlpFruzoDjW95oIyhdsjEmo0PkdjePDvhVvDvteJDNMESpiL6j/taj4n4YlctVkk0uJ61mfY8N2f6JGz6MPGmzf+Fku10QKBgQD6FR9eYq7abGM87haBT/a/b2fHyTOWPdIfX2RCRXwasvygcupy7lzdgCLBFNVZkm/y17A5Mj3B8Tt7bgZkk5CHjFuYigGK6+TKACKw2VnPFA07A8VZ++puCl0vPtnYJcbjAPTmMgZTEdh/L1BNzbGtlfEQ2NpGfVjaiRQ7/2IKxQKBgQCd8vKviRO3fbRHZDOCFu0P+vF8itTctEMFUA0H0osQOtgAIqnMWO/ZXZcDNppz9IB/K2qzLOtj++oTuO3jKznb44UcyPpA8DeI+41XJDuvCzHKVOGrEK68EIskNBPMkINuYb7F6EGmIEaw0LUIHKsPF8Ls1q0tzsG4DpNJbVdUnwKBgQCVDInJo187Z2YuJmjbYWFa73cXJ9/LJ8VYVW5X4Tn6X7sZleQN4+sNSRVtppER9akrP0oUjNEqCjC9e/Hrd9eoMgtVess4lSejDTmf1aslNHo+Nh0laHNsaljnk7oajUlgem171UzXrccu5nAiLvo6zycCN3zAjmyqsxCQQcGZUQKBgAYOd9HVTsU9c7k9FWCKooJ3W+Urqafg3aUJgcZoA18W8otmyN4GFwRdu1mrMVb67CZ0Mr42HbySv1IMSErqsGhHHCaTak0tKOP+yVerEwLOShV5FcIqfJjHhZ5YxN2WOQP1iYwrMME/AZbsGdT6zHBit92/CZr1D6aQIIOzRqL/AoGAbS0HADYN5Adnz1z6UYhL0Z3vF3dKZrjR7wWho4gYdYWNOTdLq0Lkjh+tPycFgBHXIOmypZTJBTW4KmK6sbqJukOY77EGKqRNje7EwwlFBW4IBzLmmWQf6J78fzK3deae73esneHm5Nc5+e573UpB76tWDymt1bNPM8XbBFtbT6c=";

    String WX_TOTAL_ORDER = "";
    String WX_APP_ID = "";
    String WX_PRIVATE_KEY = "";


    String loginUrl = MAIN_ENGINE + "/mobile/login_move";
    String oauthLoginUrl = MAIN_ENGINE + "/ahomet/oauthlogin/app/handler";
    String payResultUrl = MAIN_ENGINE + "/ahomet/personal/mobile/personel_hotel_order";
    String stepLoginUrl = MAIN_ENGINE + "/api/mobile/one_step_login";
    String appShareUrl = MAIN_ENGINE + "/mobile/app/getShareDown";
    String appUpdateUrl = MAIN_ENGINE + "/download/update/android_d_ph";
    String wxMakeOrderUrl = MAIN_ENGINE + "/api/payment/weixinpay/app/makePreOrder";

    String feedbackUrl = MAIN_ENGINE + "/mobile/singlePage/mobile_feedback";
    String functionIntroductionUrl = MAIN_ENGINE + "/mobile/singlePage/mobile_feature_list";
    String copyrightUrl = MAIN_ENGINE + "/mobile/singlePage/mobile_legal_notices";
    String privacyPolicyUrl = MAIN_ENGINE + "/mobile/singlePage/mobile_privacy_policy";
    String userAgreementUrl = MAIN_ENGINE + "/mobile/singlePage/mobile_user_treaty";
}
