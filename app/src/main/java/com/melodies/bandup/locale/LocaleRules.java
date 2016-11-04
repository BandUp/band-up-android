package com.melodies.bandup.locale;

/**
 * Created by Bergthor on 4.11.2016.
 */

/**
 * Here go all grammar rules for specific languages.
 * To create a new locale, create a new class with a name in the following format:
 * LocaleRules_'language'_'country' e.g. LocaleRules_is_IS. but the country is optional.
 */
public interface LocaleRules {
    Boolean ageIsPlural(int age);
}
