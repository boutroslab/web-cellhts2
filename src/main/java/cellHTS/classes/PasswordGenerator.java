/*
 * //
 * // Copyright (C) 2009 Boutros-Labs(German cancer research center) b110-it@dkfz.de
 * //
 * //
 * //    This program is free software: you can redistribute it and/or modify
 * //    it under the terms of the GNU General Public License as published by
 * //    the Free Software Foundation, either version 3 of the License, or
 * //    (at your option) any later version.
 * //
 * //    This program is distributed in the hope that it will be useful,
 * //    but WITHOUT ANY WARRANTY; without even the implied warranty of
 * //    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * //
 * //    You should have received a copy of the GNU General Public License
 * //    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package cellHTS.classes;

import java.util.Random;

/**
 *
 *  This class provides a simple tool to generate high quality passwords
 *
 *  * Created by IntelliJ IDEA.
 * User: oliverpelz
 * Date: 27.03.2009
 * Time: 15:50:35
 *
 */
public class PasswordGenerator {
    public static final int	DIGITS_ONLY = 0;
        public static final int	LETTERS_ONLY = 1;
        public static final int	MIXED = 2;

        private static final String	LETTERS = "qwertyuiopzxcvbnmasdfghjklAZERTYUIOPMLKJHGFDSQWXCVBN";
        private static final int	LETTERS_LENGTH = LETTERS.length();
        private static final String	NUMBERS = "1357924680";
        private static final int	NUMBERS_LENGTH = NUMBERS.length();

    /**
     *
     *  gets a new password of length length
     *
     * @param length  Length of the password
     * @return the generated password
     */
        public static String get(int length)
        {
            return get(new Random(System.currentTimeMillis()), length, MIXED);
        }

    /**
     *
     * gets a new password of length length with type type
     *
     * @param length length of generated password
     * @param type  0=only digits,1=letters only,2=mixed between digits and letters
     * @return the generated passord
     */
        public static String get(int length, int type)
        {
            return get(new Random(System.currentTimeMillis()), length, type);
        }
        /**
     *
     * gets a new password of length length with type type , a random object can be supplied too
     *
     * @param random a Random object 
     * @param length length of generated password
     * @param type  0=only digits,1=letters only,2=mixed between digits and letters
     * @return the generated passord
     */
        public static String get(Random random, int length, int type)
        {
            if (length <= 0)			throw new IllegalArgumentException("length has to be bigger zero");
            if (type != DIGITS_ONLY &&
                type != LETTERS_ONLY &&
                type != MIXED)			throw new IllegalArgumentException("invalid type");

            StringBuilder	generated_password = new StringBuilder("");
            boolean			type_selector = false;

            for (int i = 0; i < length; i++)
            {
                type_selector = random.nextBoolean();

                // characters
                if (LETTERS_ONLY == type ||
                    type != DIGITS_ONLY && type_selector)
                {
                    char c = LETTERS.charAt((int)((double)LETTERS_LENGTH * random.nextDouble()));
                    if (random.nextDouble() > 0.5D)
                    {
                        c = Character.toUpperCase(c);
                    }
                    generated_password.append(c);
                }
                // digits
                else
                {
                    generated_password.append(NUMBERS.charAt((int)((double)NUMBERS_LENGTH * random.nextDouble())));
                }
            }

            return generated_password.toString();
        }
     
}
