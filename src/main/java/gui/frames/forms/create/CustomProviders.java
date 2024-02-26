package gui.frames.forms.create;

import java.util.ArrayList;
import java.util.List;

import net.datafaker.Faker;
import net.datafaker.providers.base.AbstractProvider;
import net.datafaker.providers.base.BaseProviders;

public class CustomProviders {

    private CustomProviders() {

    }

    public static class DBestFaker extends Faker {

        public Char character() {
            return getProvider(Char.class, Char::new, this);
        }
    }

    public static class Char extends AbstractProvider<BaseProviders> {

        private static final String[] UPPERCASE_LETTERS = new String[]{
            "A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P",
            "Q", "R", "S", "T", "U", "V", "W", "X",
            "Y", "Z"
        };

        private static final String[] LOWERCASE_LETTERS = new String[]{
            "a", "b", "c", "d", "e", "f", "g", "h",
            "i", "j", "k", "l", "m", "n", "o", "p",
            "q", "r", "s", "t", "u", "v", "w", "x",
            "y", "z"
        };

        private static final String[] SPECIAL_CHARACTERS = new String[]{
            "!", "@", "#", "$", "%", "&", "*",
            "+", "-", "/", ":", ";", "<", "=",
            ">", "?", "@", "^", "`", "{", "|",
            "}", "~"
        };

        private static final String[] NUMBERS = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

        public Char(BaseProviders faker) {
            super(faker);
        }

        public String uppercaseLetter() {
            return UPPERCASE_LETTERS[this.faker.random().nextInt(UPPERCASE_LETTERS.length)];
        }

        public String lowercaseLetter() {
            return LOWERCASE_LETTERS[this.faker.random().nextInt(LOWERCASE_LETTERS.length)];
        }

        public String specialChar() {
            return SPECIAL_CHARACTERS[this.faker.random().nextInt(SPECIAL_CHARACTERS.length)];
        }

        public String numberChar() {
            return NUMBERS[this.faker.random().nextInt(NUMBERS.length)];
        }

        public String anyChar(boolean upper, boolean lower, boolean special, boolean number) {
            List<String> options = new ArrayList<>();

            if (upper) options.add(this.uppercaseLetter());
            if (lower) options.add(this.lowercaseLetter());
            if (special) options.add(this.specialChar());
            if (number) options.add(this.numberChar());

            return options.get(this.faker.random().nextInt(options.size()));
        }
    }
}



