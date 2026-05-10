function capDiv(value, cap) {
    return Math.min(value / cap, 1);
}

function normalizeFeatures(features) {

    return {

        char_entropy: capDiv(features.char_entropy, 5.5),
        token_entropy: capDiv(features.token_entropy, 5.5),

        url_length: capDiv(features.url_length, 200),
        token_count: capDiv(features.token_count, 20),

        hyphenated_domain: features.hyphenated_domain,
        uses_ip_address: features.uses_ip_address,
        uses_shortener: features.uses_shortener,

        password_field_present: features.password_field_present,
        email_field_present: features.email_field_present,
        external_form_action: features.external_form_action,

        iframe_count: capDiv(features.iframe_count, 5),
        redirect_indicator: capDiv(features.redirect_indicator, 3),

        possible_js_obfuscation: features.possible_js_obfuscation
    };
}